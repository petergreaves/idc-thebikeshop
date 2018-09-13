//-----------------------------------------------------------------
// Licensed Materials - Property of IBM
//
// WebSphere Commerce
//
// (C) Copyright IBM Corp. 2013, 2016 All Rights Reserved.
//
// US Government Users Restricted Rights - Use, duplication or
// disclosure restricted by GSA ADP Schedule Contract with
// IBM Corp.
//-----------------------------------------------------------------


if(typeof(SearchJS) == "undefined" || SearchJS == null || !SearchJS){

    SearchJS = {

        /**
         * This variable controls the timer handler before triggering the autoSuggest.  If the user types fast, intermittent requests will be cancelled.
         * The value is initialized to -1.
         */
        autoSuggestTimer: -1,

        /**
         * This variable controls the delay of the timer in milliseconds between the keystrokes before firing the search request.
         * The value is initialized to 400.
         */
        autoSuggestKeystrokeDelay : 400,

        /**
         * This variable indicates whether or not the user is hovering over the autoSuggest results popup display.
         * The value is initialized to false.
         */
        autoSuggestHover : false,

        /**
         * This variable stores the old search term used in the auto suggest search box
         * The value is initialized to empty string.
         */
        autoSuggestPreviousTerm : "",

        /**
         * This variable stores the URL of currently selected static autosuggest recommendation
         * The value is initialized to empty string.
         */
        autoSuggestURL : "",

        /**
         * This variable stores the index of the selected auto suggestion item when using up/down arrow keys.
         * The value is initialized to -1.
         */
        autoSelectOption : -1,

        /**
         * This variable stores the index offset of the first previous history term
         * The value is initialized to -1.
         */
        historyIndex : -1,

        /**
         * This variable indicates whether a the cached suggestions have been retrieved.
         * The value is initialized to false.
         */
        retrievedCachedSuggestions : false,

        /**
         * This variable sets the total number of static autosuggest recommendations used for each static category/grouping.
         * The value is initialized to 4.
         */
        TOTAL_SUGGESTED : 4,

        /**
         * This variable sets the total number of previous search history terms.
         * The value is initialized to 2.
         */
        TOTAL_HISTORY : 2,

        /**
         * This variable controls when to trigger the auto suggest box.  The number of characters greater than this threshold will trigger the auto suggest functionality.
         * The static/cached auto suggest will be performed if this threshold is exceeded.
         * The value is initialized to 1.
         */
        AUTOSUGGEST_THRESHOLD : 1,

        /**
         * This variable controls when to trigger the dynamic auto suggest.  The number of characters greater than this threshold will trigger the request for keyword search.
         * The static/cached auto suggest will be be displayed if the characters exceed the above config parameter, but exceeding this threshold will additionally perform the dynamic search to add to the results in the static/cached results.
         * This value should be greater or equal than the AUTOSUGGEST_THRESHOLD, as the dynamic autosuggest is secondary to the static/cached auto suggest.
         * The value is initialized to 1.
         */
        DYNAMIC_AUTOSUGGEST_THRESHOLD : 1,

        /**
         * This variable is an internal constant used in the element ID's generated in the autosuggest content.
         * The value is initialized to 1000.
         */
        CACHED_AUTOSUGGEST_OFFSET : 1000,

        /**
         * This variable is used to indicate whether or not the auto suggest selection has reached the end of the list.
         * The value is initialized to false.
         */
        END_OF_LIST : false,
        /**
          * The auto suggest container ID's
         */
        STATIC_CONTENT_SECTION_DIV: ["autoSuggestStatic_1", "autoSuggestStatic_2", "autoSuggestStatic_3"],


        /**
         * NLS message for header
        */
        staticContentHeaderHistory:"",

        /**
         * URL to retrieve auto suggest keywords
        */
        SearchAutoSuggestServletURL:"",

        /**
         * Timeout variable for department dropdown list
        */
        searchDepartmentHoverTimeout:"",
        /**
         * Timeout variable for suggestions dropdown list
        */
        searchSuggestionHoverTimeout:"",
        /**
         * Handle for on mousedown event.
         */
        mouseDownConnectHandle: null,

        searchDepartmentSelect: function (categoryId, lel) {
            $("#searchDepartmentLabel").html(lel.innerHTML);
            $('#search_categoryId').val() = categoryId;
            this.hideSearchDepartmentList();
            return false;
        },

        cancelEvent: function(e) {
            if (e.stopPropagation) {
                e.stopPropagation();
            }
            if (e.preventDefault) {
                e.preventDefault();
            }
            e.cancelBubble = true;
            e.cancel = true;
            e.returnValue = false;
        },

        searchDepartmentKeyPressed: function(event, pos, size, categoryId, item){
            if (event.keyCode === KeyCodes.RETURN) { // enter
                this.searchDepartmentSelect(categoryId, item);
                var scrElement = document.getElementById("mobileSearchDropdown");
                if (scrElement != null && scrElement.style.display == 'block'){
                    $("#MobileSimpleSearchForm_SearchTerm").focus();
                }else{
                    document.CatalogSearchForm.searchTerm.focus();
                }
            } else if (event.keyCode === KeyCodes.UP_ARROW) { // up arrow
                if (pos != 0) {
                    $('#searchDepartmentList_' + (pos - 1)).focus();
                    this.cancelEvent(event);
                }
            } else if (event.keyCode === KeyCodes.DOWN_ARROW) { // down arrow
                if (pos != size) {
                    $('#searchDepartmentList_' + (pos + 1)).focus();
                    this.cancelEvent(event);
                }
            } else if (event.keyCode === KeyCodes.ESCAPE) { // escape
                var scrElement = document.getElementById("mobileSearchDropdown");
                if (scrElement != null && scrElement.style.display == 'block'){
                    $("#MobileSimpleSearchForm_SearchTerm").focus();
                }else{
                    document.CatalogSearchForm.searchTerm.focus();
                }
                this.hideSearchDepartmentList();
            } else if (event.shiftKey && event.keyCode === KeyCodes.TAB) { // tab
                var scrElement = document.getElementById("mobileSearchDropdown");
                if (scrElement != null && scrElement.style.display == 'block'){
                    $("#MobileSimpleSearchForm_SearchTerm").focus();
                }else{
                    document.CatalogSearchForm.searchTerm.focus();
                }
                this.cancelEvent(event);
                this.hideSearchDepartmentList();
            } else if (event.keyCode === KeyCodes.TAB) { // tab
                $('#search_submit').focus();
                this.cancelEvent(event);
                this.hideSearchDepartmentList();
            }

            return false;
        },

        hideSearchDepartmentList: function () {
            $('#searchDepartmentList').css('display', 'none');
        },

	init:function(){
	     $(document.CatalogSearchForm.searchTerm).on("focus",$.proxy (SearchJS._onFocus, SearchJS));
	     $(document.CatalogSearchForm.searchTerm).on("keydown",$.proxy (SearchJS._onKeyDown, SearchJS));
	     $(document.CatalogSearchForm.searchTerm).on("keyup",$.proxy (SearchJS._onKeyUp, SearchJS));
	     $("#searchDropdown").on("keyup",$.proxy (SearchJS._onKeyTab, SearchJS));
	//   $("#"Mobilesearch_submit").on("click",$.proxy (SearchJS._MobileonClick, SearchJS));
	//   $("#"navSearchMobile").on("click",$.proxy (SearchJS._showMobileSearchComponent, SearchJS));
	//   $("#"search_submit").on("click",$.proxy (SearchJS._onClick, SearchJS));

	     //this.staticContentHeaderHistory = Utils.getLocalizationMessage("HISTORY");
        },

        showSearchComponent:function(){
            var srcElement = document.getElementById("searchDropdown");
            if(srcElement != null) {
                srcElement.style.display= 'block';
            }
          },

        hideSearchComponent:function(){
            var srcElement = document.getElementById("searchDropdown");
            if(srcElement != null) {
                srcElement.style.display= 'none';
            }
        },

        _showMobileSearchComponent:function(){
            var srcElement = document.getElementById("mobileSearchDropdown");
            if(srcElement != null) {
              if(srcElement.style.display == "block") {
                  DepartmentJS.close('mobileSearchDropdown');
                srcElement.style.display= 'none';
              }
              else
              {
                $(".subDeptDropdown ").each(function (i, node) {
                    DepartmentJS.close(node.id);
                });
                DepartmentJS.close("departmentsDropdown");
                srcElement.style.display='block';
              }
            }
          },

        setAutoSuggestURL:function(url){
            this.SearchAutoSuggestServletURL = getAbsoluteURL() + url;
        },

        _onFocus:function(evt){
            this.showSearchComponent();
            this.retrieveCachedSuggestions();
        },

        _MobileonFocus:function(evt){
            this.showSearchComponent();
            this.retrieveCachedSuggestions();
        },

        _MobileonBlur:function(evt){
            clearTimeout(this.searchSuggestionHoverTimeout);
            this.searchSuggestionHoverTimeout = setTimeout("SearchJS.showAutoSuggest(false)",100);
        },

        _onKeyPress:function(evt){
            return evt.keyCode !== KeyCodes.RETURN;
        },
        _onKeyDown:function(evt){
            if (evt.keyCode === KeyCodes.RETURN) {
                this._handleEnterKey();
                this.cancelEvent(evt);
            }
            else if (evt.keyCode === KeyCodes.TAB) {
                clearTimeout(this.searchSuggestionHoverTimeout);
                this.searchSuggestionHoverTimeout = setTimeout("SearchJS.showAutoSuggest(false)",200);
            }
        },
        _onKeyUp:function(evt){
            var srcElement = document.getElementById("searchDropdown");
            srcElement.style.display='block';
            this.doAutoSuggest(evt, this.SearchAutoSuggestServletURL, document.CatalogSearchForm.searchTerm.value);
        },
        _onKeyTab: function (evt) {
            if (evt.keyCode === KeyCodes.TAB) {
                $("#searchFilterButton").focus();
            }
        },
        _MobileonKeyUp:function(evt){
            var srcElement = document.getElementById("mobileSearchDropdown");
            srcElement.style.display='block';
            this.doAutoSuggest(evt, this.SearchAutoSuggestServletURL, $("#MobileSimpleSearchForm_SearchTerm").val());
        },

        _handleEnterKey:function() {
            document.CatalogSearchForm.searchTerm.value = trim(document.CatalogSearchForm.searchTerm.value);
            if(document.CatalogSearchForm.searchTerm.value.length > 0) {
                if(this.END_OF_LIST) {
                    this.gotoAdvancedSearch(byId("advancedSearch").href);
                }
                else if(this.autoSuggestURL !== "" &&  this.autoSuggestURL != (document.location.href + "#")) {
                    //When enter key is hit with one of the suggested keywords or results highlighted, then go to the URL specified for that result..
                    // go to suggested URL
                    document.location.href = appendWcCommonRequestParameters(this.autoSuggestURL);
                }
                else {
                    //Enter key is hit, when the focus was in search term input box.. Submit the form and get the results..
                    document.CatalogSearchForm.searchTerm.value = trim(document.CatalogSearchForm.searchTerm.value);
                    submitSpecifiedForm(document.CatalogSearchForm);
                }
            }

        },


        _onClick:function(evt){
            document.CatalogSearchForm.searchTerm.value = trim(document.CatalogSearchForm.searchTerm.value);
            if(document.CatalogSearchForm.searchTerm.value.length > 0) {
                if(typeof TealeafWCJS != "undefined"){
                    TealeafWCJS.processDOMEvent(evt);
                }
                submitSpecifiedForm(document.CatalogSearchForm);
            }
            return false;
        },

        _MobileonClick:function(evt){
            document.MobileCatalogSearchForm.searchTerm.value = trim(document.MobileCatalogSearchForm.searchTerm.value);
            if(document.MobileCatalogSearchForm.searchTerm.value.length > 0) {
                if(typeof TealeafWCJS != "undefined"){
                    TealeafWCJS.processDOMEvent(evt);
                }
                submitSpecifiedForm(document.MobileCatalogSearchForm);
            }
            return false;
        },

        doDynamicAutoSuggest:function(url, searchTerm, showHeader) {
            // if pending autosuggest triggered, cancel it.
            if(this.autoSuggestTimer != -1) {
                clearTimeout(this.autoSuggestTimer);
                this.autoSuggestTimer = -1;
            };

            // call the auto suggest
            this.autoSuggestTimer = setTimeout(function() {
                var newurl = url + "&term=" + encodeURIComponent(searchTerm) + "&showHeader=" + showHeader;
                $("#autoSuggestDynamic_Result_div").refreshWidget("updateUrl", newurl);
                console.debug("update autosuggest "+url);
                wcRenderContext.updateRenderContext("AutoSuggest_Context", {});
                this.autoSuggestTimer = -1;
            }, this.autoSuggestKeystrokeDelay);
        },

        gotoAdvancedSearch:function(url) {
            var searchTerm = $("#SimpleSearchForm_SearchTerm").val();
            document.location.href = appendWcCommonRequestParameters(url) + '&searchTerm=' + searchTerm;

        },

        showAutoSuggest:function(display) {
            var autoSuggest_Result_div = document.getElementById("autoSuggest_Result_div");
            var ie_version = Utils.get_IE_version();
            if (ie_version && ie_version < 7) {
                var autoSuggest_content_div = document.getElementById("autoSuggest_content_div");
                var autoSuggestDropDownIFrame = document.getElementById("autoSuggestDropDownIFrame");
            }

            if(autoSuggest_Result_div != null && typeof autoSuggest_Result_div != 'undefined') {
                if(display) {
                    autoSuggest_Result_div.style.display = "block";
                    if (ie_version && ie_version < 7) {
                        autoSuggestDropDownIFrame.style.height = autoSuggest_content_div.scrollHeight;
                        autoSuggestDropDownIFrame.style.display = "block";
                    }
                    this.registerMouseDown();
                }
                else {
                    if (ie_version && ie_version < 7) {
                        autoSuggestDropDownIFrame.style.display = "none";
                        autoSuggestDropDownIFrame.style.height = 0;
                    }
                    autoSuggest_Result_div.style.display = "none";
                    this.unregisterMouseDown();
                }
            }
            else {
                this.unregisterMouseDown();
            }
        },

        showAutoSuggestIfResults:function() {
            // if no results, hide the autosuggest box

            var scrElement = document.getElementById("mobileSearchDropdown");
            if(typeof(staticContent) != "undefined" && $("#" + this.STATIC_CONTENT_SECTION_DIV[0]).html() === "" && $("#autoSuggestHistory").html() === "" && document.getElementById("dynamicAutoSuggestTotalResults") == null) {
                this.showAutoSuggest(false);
            }
            else if(scrElement != null && scrElement.style.display == 'block')
            {
                    if($("#MobileSimpleSearchForm_SearchTerm").val().length <= this.AUTOSUGGEST_THRESHOLD)
                    {
                        this.showAutoSuggest(false);
                    }
                    else
                    {
                        this.showAutoSuggest(true);
                    }
            }
            else {
                    if(document.CatalogSearchForm.searchTerm.value.length <= this.AUTOSUGGEST_THRESHOLD)
                    {
                        this.showAutoSuggest(false);
                    }
                    else
                    {
                        this.showAutoSuggest(true);
                    }
            }
        },

        selectAutoSuggest:function(term) {
            var scrElement = document.getElementById("mobileSearchDropdown");
            if (scrElement != null && scrElement.style.display == 'block'){
                var searchBox = document.getElementById("MobileSimpleSearchForm_SearchTerm");
            }else{
                var searchBox = document.CatalogSearchForm.searchTerm;
            }

            searchBox.value = term;
            searchBox.focus();
            this.autoSuggestPreviousTerm = term;
            if(typeof TealeafWCJS != "undefined"){
                TealeafWCJS.createExplicitChangeEvent(searchBox);
            }
            submitSpecifiedForm(document.CatalogSearchForm);
        },

        highLightSelection:function(state, index) {
            var selection = document.getElementById("autoSelectOption_" + index);
            if(selection != null && typeof selection != 'undefined') {
                if(state) {
                    selection.className = "autoSuggestSelected";
                    var scrElement = document.getElementById("mobileSearchDropdown");
                    if (scrElement != null && scrElement.style.display == 'block'){
                        var searchBox = document.getElementById("MobileSimpleSearchForm_SearchTerm");
                    }else{
                        var searchBox = document.CatalogSearchForm.searchTerm;
                    }
                    $(searchBox).attr("aria-activedescendant", "suggestionItem_" + index);
                    var totalDynamicResults = document.getElementById("dynamicAutoSuggestTotalResults");
                    if((totalDynamicResults != null && typeof totalDynamicResults != 'undefined' && index < totalDynamicResults.value) || (index >= this.historyIndex)) {
                        searchBox.value = selection.title;
                        this.autoSuggestPreviousTerm = selection.title;
                    }
                    this.autoSuggestURL = selection.href;
                }
                else {
                    selection.className = "";
                }
                return true;
            }
            else {
                return false;
            }
        },

        enableAutoSelect:function(index) {
            this.highLightSelection(false, this.autoSelectOption);
            var item = document.getElementById('autoSelectOption_' + index);
            item.className = "autoSuggestSelected";
            this.autoSelectOption = index;
        },

        resetAutoSuggestKeyword:function() {
            var originalKeyedSearchTerm = document.getElementById("autoSuggestOriginalTerm");
            if(originalKeyedSearchTerm != null && typeof originalKeyedSearchTerm != 'undefined') {
                var scrElement = document.getElementById("mobileSearchDropdown");
                if (scrElement != null && scrElement.style.display == 'block')
                {
                    var searchBox = document.getElementById("MobileSimpleSearchForm_SearchTerm");
                }else{
                    var searchBox = document.CatalogSearchForm.searchTerm;
                }
                searchBox.value = originalKeyedSearchTerm.value;
                this.autoSuggestPreviousTerm = originalKeyedSearchTerm.value;
            }
        },


        clearAutoSuggestResults:function() {
            // clear the static search results.
            for (var i = 0; i < staticContent.length; i++) {
                $("#" + this.STATIC_CONTENT_SECTION_DIV[i]).html("");
            }
            this.autoSuggestPreviousTerm = "";
            this.autoSuggestURL = "";
            // clear the dynamic search results;
            $("#autoSuggestDynamic_Result_div").html("");
            this.showAutoSuggest(false);
        },

        doAutoSuggest:function(event, url, searchTerm) {
            searchTerm = searchTerm.trim();
            if(searchTerm.length <= this.AUTOSUGGEST_THRESHOLD ) {
                this.showAutoSuggest(false);
            }

            if (event.keyCode === KeyCodes.RETURN) {
                return;
            }

            if (event.keyCode === KeyCodes.TAB) {
                this.autoSuggestHover = true;
                return;
            }

            if (event.keyCode === KeyCodes.ESCAPE) {
                this.showAutoSuggest(false);
                return;
            }

            if (event.keyCode === KeyCodes.UP_ARROW) {
                var totalDynamicResults = document.getElementById("dynamicAutoSuggestTotalResults");
                if(this.END_OF_LIST) {
                    $("#autoSuggestAdvancedSearch").removeClass("autoSuggestSelected");
                    this.END_OF_LIST = false;
                    this.autoSelectOption--;
                    if(!this.highLightSelection(true, this.autoSelectOption)) {
                        if(this.autoSelectOption == this.CACHED_AUTOSUGGEST_OFFSET && totalDynamicResults != null && typeof totalDynamicResults != 'undefined') {
                            this.autoSelectOption = totalDynamicResults.value-1;
                            this.highLightSelection(true, this.autoSelectOption);
                        }
                    }
                }
                else if (this.highLightSelection(true, this.autoSelectOption-1)) {
                    this.highLightSelection(false, this.autoSelectOption);
                    if(this.autoSelectOption == this.historyIndex) {
                        this.resetAutoSuggestKeyword();
                    }
                    this.autoSelectOption--;
                }
                else if(this.autoSelectOption == this.CACHED_AUTOSUGGEST_OFFSET && totalDynamicResults != null && typeof totalDynamicResults != 'undefined') {
                    this.highLightSelection(false, this.CACHED_AUTOSUGGEST_OFFSET);
                    this.autoSelectOption = totalDynamicResults.value-1;
                    this.highLightSelection(true, this.autoSelectOption);
                }
                else {
                    // up arrow back to the very top
                    this.highLightSelection(false, this.autoSelectOption);
                    this.autoSelectOption = -1;
                    var originalKeyedSearchTerm = document.getElementById("autoSuggestOriginalTerm");
                    this.resetAutoSuggestKeyword();
                }
                return;
            }

            if (event.keyCode === KeyCodes.DOWN_ARROW) {
                if(this.highLightSelection(true, this.autoSelectOption+1)) {
                    this.highLightSelection(false, this.autoSelectOption);
                    this.autoSelectOption++;
                }
                else if(this.autoSelectOption < this.CACHED_AUTOSUGGEST_OFFSET && this.highLightSelection(true, this.CACHED_AUTOSUGGEST_OFFSET)) {
                    // down arrow into the cached autosuggest section
                    this.highLightSelection(false, this.autoSelectOption);
                    this.autoSelectOption = this.CACHED_AUTOSUGGEST_OFFSET;
                    this.resetAutoSuggestKeyword();
                }
                else if(!this.END_OF_LIST) {
                    $("#autoSuggestAdvancedSearch").addClass("autoSuggestSelected");
                    this.highLightSelection(false, this.autoSelectOption);
                    this.autoSelectOption++;
                    this.END_OF_LIST = true;
                    var scrElement = document.getElementById("mobileSearchDropdown");
                    if (scrElement != null && scrElement.style.display == 'block'){
                        var searchBox = document.getElementById("MobileSimpleSearchForm_SearchTerm");
                    }else{
                        var searchBox = document.CatalogSearchForm.searchTerm;
                    }
                    $(searchBox).attr("aria-activedescendant", "advancedSearch");
                }
                return;
            }

            if(searchTerm.length > this.AUTOSUGGEST_THRESHOLD && searchTerm == this.autoSuggestPreviousTerm) {
                return;
            }
            else {
                this.autoSuggestPreviousTerm = searchTerm;
            }

            if(searchTerm.length <= this.AUTOSUGGEST_THRESHOLD) {
                return;
            };

            // cancel the dynamic search if one is pending
            if(this.autoSuggestTimer != -1) {
                clearTimeout(this.autoSuggestTimer);
                this.autoSuggestTimer = -1;
            }

            if(searchTerm !== "") {
                this.autoSelectOption = -1;
                var hasResults = this.doStaticAutoSuggest(searchTerm);
                if(searchTerm.length > this.DYNAMIC_AUTOSUGGEST_THRESHOLD) {
                    var showHeader = true; // hasResults;
                    this.doDynamicAutoSuggest(url, searchTerm, showHeader);
                }
                else {
                    // clear the dynamic results
                    $("#autoSuggestDynamic_Result_div").html("");
                }
            }
            else {
                this.clearAutoSuggestResults();
            }
        },

        tokenizeForBidi:function(displayName, searchName, searchTerm, searchTermLower) {
            var tokens = displayName.split( " > " );
            var html = "";
            var str = "";
            if(tokens.length > 0) {
                html = html + "<div class='category_list'>";
                for(i = 0; i < tokens.length; i++) {
                    if(i!=0) {
                        // not the first token
                        html = html + "<span class='gt'>&nbsp; > &nbsp;</span>";
                    }
                    if(i == tokens.length - 1) {
                        // last token
                        var index = searchName.toLowerCase().indexOf(searchTermLower);
                        var subStringBefore = searchName.substr(0, index);
                        var subStringAfter =  searchName.substr(index + searchTerm.length);
                        var highLighted = "<span class='highlight'>" + searchTerm + "</span>";
                        str = subStringBefore + highLighted + subStringAfter;
                    }
                    else {
                        str = tokens[i];
                    }
                    html = html + str;
                }
                html = html + "</div>";
            }
            return html;
        },

        doStaticAutoSuggest:function(searchTerm) {
            var resultList = ["", "", "", "", "", ""];
            var emptyCell = 0;
            var searchTermLower = searchTerm.toLowerCase();
            var listCount = this.CACHED_AUTOSUGGEST_OFFSET;
            var divStart = "<div class='list_section'><div";
            var divEnd =   "</div></div>";

            for(var i = 0; i < staticContent.length; i++) {
                var count = 0;
                for(var j = 0; j < staticContent[i].length; j++) {
                    var searchName = staticContent[i][j][0];
                    var searchURL = staticContent[i][j][1];
                    var displayName = staticContent[i][j][2];
                    var index = searchName.toLowerCase().indexOf(searchTermLower);
                    if(index != -1) {

                        var htmlDisplayName = this.tokenizeForBidi(displayName, searchName, searchTerm, searchTermLower);

                        resultList[i] = resultList[i] + "<ul class='autoSuggestDivNestedList'><li id='suggestionItem_" + listCount + "' role='listitem' tabindex='-1'><a id='autoSelectOption_" + listCount + "' title='" + escapeXml(searchName, true) + "' onmouseout='this.className=\"\"; this.autoSuggestURL=\"\";' onclick='SearchJS.hideSearchComponent();' onmouseover='SearchJS.enableAutoSelect(" + listCount + "); this.autoSuggestURL=this.href;' href=\"" + searchURL + "\">" + htmlDisplayName + "</a></li></ul>";
                        count++;
                        listCount++;
                        if(count >= this.TOTAL_SUGGESTED) {
                            break;
                        }
                    }
                }
            }

            for (var i = 0; i < staticContent.length; i++) {
                $("#" + this.STATIC_CONTENT_SECTION_DIV[i]).html("");
                if(resultList[i] !== "") {
                    var heading =  "<ul class='autoSuggestDivNestedList'><li class='heading'><span>" + staticContentHeaders[i] + "</span></li></ul>";
                    $("#" + this.STATIC_CONTENT_SECTION_DIV[emptyCell]).html(heading + divStart + " role='list' title='" + staticContentHeaders[i] + "' aria-label='" + staticContentHeaders[i] + "'>" + resultList[i] + divEnd);
                    emptyCell++;
                }
            }

            var historyList = "";
            var searchHistorySection = document.getElementById("autoSuggestHistory");
            searchHistorySection.innerHTML = "";
            var historyArray = [];
            this.historyIndex = listCount;

            var searchHistoryCookie = getCookie("searchTermHistory");
            if(typeof(searchHistoryCookie) != 'undefined') {
                var termsArray = searchHistoryCookie.split("|");
                var count = 0;
                for(var i = termsArray.length - 1; i > 0; i--) {
                    var theTerm = termsArray[i];
                    var theLowerTerm = theTerm.toLowerCase();
                    if(theLowerTerm.match("^"+searchTermLower) == searchTermLower) {
                        var repeatedTerm = false;
                        for(var j = 0; j < historyArray.length; j++) {
                            if(historyArray[j] == theLowerTerm) {
                                repeatedTerm = true;
                                break;
                            }
                        }
                        if(!repeatedTerm) {
                            if(count >= this.TOTAL_HISTORY) {
                                break;
                            }
                            historyList = historyList + "<ul class='autoSuggestDivNestedList'><li id='suggestionItem_" + listCount + "' role='listitem' tabindex='-1'><a href='#' onmouseout='this.className=\"\"' onmouseover='SearchJS.enableAutoSelect(" + listCount + ");' onclick='SearchJS.selectAutoSuggest(this.title); return false;' title='" + theTerm + "' id='autoSelectOption_" + listCount+ "'><strong>" + searchTerm + "</strong>" + theTerm.substring(searchTerm.length, theTerm.length) + "</a></li></ul>";
                            historyArray.push(theLowerTerm);
                            count++;
                            listCount++;
                        }
                    }
                }
            }

            if (this.staticContentHeaderHistory === "") {
                this.staticContentHeaderHistory = Utils.getLocalizationMessage("HISTORY");
            }

            if(historyList !== "") {
                var heading =  "<ul class='autoSuggestDivNestedList'><li class='heading'><span>" + this.staticContentHeaderHistory + "</span></li></ul>"
                searchHistorySection.innerHTML = heading + divStart + " title='" + this.staticContentHeaderHistory + "'>" + historyList + divEnd;
                emptyCell++;
            }

            if(emptyCell > 0) {
                this.showAutoSuggest(true);
                return true;
            }

            return false;
        },

        retrieveCachedSuggestions:function() {
            if(!this.retrievedCachedSuggestions) {
                wcRenderContext.updateRenderContext("CachedSuggestions_Context", {});
            }
        },

        /**
         * Updates the searchTermHistory cookie value...
         */
        updateSearchTermHistoryCookie:function(updatedSearchTerm){
            var cookieKey = "searchTermHistory";
            var cookieValue = "|" + updatedSearchTerm;
            var searchTermHistoryCookie = getCookie(cookieKey);
            if(typeof(searchTermHistoryCookie) != 'undefined') {
                cookieValue = setCookie(cookieKey) + cookieValue;
            }
            setCookie(cookieKey, cookieValue, {path:'/', domain:cookieDomain});
        },

        updateSearchTermHistoryCookieAndRedirect:function(updatedSearchTerm, redirectURL){
            this.updateSearchTermHistoryCookie(updatedSearchTerm);
            if (navigator.userAgent.toLowerCase().indexOf('firefox') == -1) {document.location.href = appendWcCommonRequestParameters(redirectURL);}
        },

        isValidNumber:function(n) {
            return !isNaN(parseFloat(n)) && isFinite(n) && n >= 0;
        },

        /**
         * Validation method for advanced search form
         */
        validateForm: function(form) {
            form["minPrice"].value = trim(form["minPrice"].value);
            form["maxPrice"].value = trim(form["maxPrice"].value);

            var minValue = form["minPrice"].value;
            var maxValue = form["maxPrice"].value;

            var minIsValid = this.isValidNumber(minValue);
            var maxIsValid = this.isValidNumber(maxValue);

            if(minValue.length > 0 && !minIsValid) {
                MessageHelper.formErrorHandleClient(form["minPrice"].id, MessageHelper.messages["EDPPaymentMethods_AMOUNT_NAN"]);
                return false;
            }
            else if(maxValue.length > 0 && !maxIsValid) {
                MessageHelper.formErrorHandleClient(form["maxPrice"].id, MessageHelper.messages["EDPPaymentMethods_AMOUNT_NAN"]);
                return false;
            }
            else if (minValue.length > 0 && maxValue.length > 0 && parseFloat(minValue) > parseFloat(maxValue)) {
                MessageHelper.formErrorHandleClient(form["maxPrice"].id, MessageHelper.messages["ERROR_PRICE_RANGE"]);
                return false;
            }

            form["searchTerm"].value = trim(form["searchTerm"].value);
            form["filterTerm"].value = trim(form["filterTerm"].value);
            form["manufacturer"].value = trim(form["manufacturer"].value);

            var searchTerm = form["searchTerm"].value;
            var filterTerm = form["filterTerm"].value;
            var manufacturer = form["manufacturer"].value;

            if (searchTerm.length == 0 && filterTerm.length == 0 && manufacturer.length == 0) {
                MessageHelper.formErrorHandleClient(form["searchTerm"].id, MessageHelper.messages["ERROR_EMPTY_SEARCH_FIELDS"]);
                return false;
            }

            processAndSubmitForm(form);
        },

        registerMouseDown: function() {
            if (this.mouseDownConnectHandle == null) {
                this.mouseDownConnectHandle = $(document.documentElement).on("mousedown", $.proxy(this.handleMouseDown, this));
            }
        },

        unregisterMouseDown: function() {
            if (this.mouseDownConnectHandle != null) {
                $(document.documentElement).off("mousedown");
                this.mouseDownConnectHandle = null;
            }
        },

        handleMouseDown: function(evt) {
            var node = evt.target;
            if (node != document.documentElement) {
                var searchDropdown = document.getElementById("searchDropdown");
                var searchTerm = document.CatalogSearchForm.searchTerm;
                var close = true;
                while (node) {
                    if (node == searchDropdown || node == searchTerm) {
                        close = false;
                        break;
                    }
                    node = node.parentNode;
                }
                if (close) {
                    this.showAutoSuggest(false);
                }
            }
        },

        declareAutoSuggestRefreshArea: function() {
            // ============================================
            // div: autoSuggestDynamic_Result_div refresh area
            // Declares a new refresh controller for Auto Suggest

            // common render context
            wcRenderContext.declare("AutoSuggest_Context", ["autoSuggestDynamic_Result_div"], null);

            /**
             * Displays the keyword suggestions from the search index
             * This function is called when a render context changed event is detected.
             */
            var renderContextChangedHandler = function() {
                var renderContext = wcRenderContext.getRenderContextProperties("AutoSuggest_Context");
                $("#autoSuggestDynamic_Result_div").refreshWidget("refresh", renderContext);
            };

            /**
             * Display the results.
             */
            var postRefreshHandler = function() {
                  var response = document.getElementById('suggestedKeywordResults');
                  var productsResponse = document.getElementById('suggestedProductsResults');
                  if(response == null && productsResponse == null) {
                    // No response or an error page.   Clear the contents.
                    $("#autoSuggestDynamic_Result_div").html("");
                  }
                  SearchJS.showAutoSuggestIfResults();
            };

            // initialize widget
            $("#autoSuggestDynamic_Result_div").refreshWidget({renderContextChangedHandler: renderContextChangedHandler, postRefreshHandler: postRefreshHandler});
        },

        declareAutoSuggestCachedSuggestionRefreshArea: function() {
            // ============================================
            // div: autoSuggestCachedSuggestions_div refresh area
            // Declares a new refresh controller for Auto Suggest

            // common render context
            wcRenderContext.declare("CachedSuggestions_Context", ["autoSuggestCachedSuggestions_div"], null);

           /**
            * Retrieves the cached suggestions used in the autosuggest box.
            * This function is called when a render context changed event is detected.
            */
            var renderContextChangedHandler = function() {
                var renderContext = wcRenderContext.getRenderContextProperties("CachedSuggestions_Context");
                $("#autoSuggestCachedSuggestions_div").refreshWidget("refresh", renderContext);
            };

            /**
             * Updates the cached suggestions.
             */
            var postRefreshHandler = function() {
                var response = document.getElementById('cachedSuggestions');
                if(response == null) {
                    // No response or an error page.   Clear the contents.
                    $("#autoSuggestCachedSuggestions_div").html("");
                }
                else {
                    var scripts = response.getElementsByTagName("script");
                    var j = scripts.length;
                    for (var i = 0; i < j; i++){
                        var newScript = document.createElement('script');
                        newScript.type = "text/javascript";
                        newScript.text = scripts[i].text;
                        document.getElementById('autoSuggestCachedSuggestions_div').appendChild (newScript);
                    }
                    SearchJS.retrievedCachedSuggestions = true;
                    var scrElement = document.getElementById("mobileSearchDropdown");
                    if (scrElement != null && scrElement.style.display == 'block')
                    {
                        searchTerm = $("#MobileSimpleSearchForm_SearchTerm").val();
                    }
                        else
                    {
                    searchTerm = document.CatalogSearchForm.searchTerm.value;
                    }
                    if(searchTerm.length > SearchJS.AUTOSUGGEST_THRESHOLD) {
                        SearchJS.doStaticAutoSuggest(searchTerm);
                    }
                }
            };

            // initialize widget
            $("#autoSuggestCachedSuggestions_div").refreshWidget({renderContextChangedHandler: renderContextChangedHandler, postRefreshHandler: postRefreshHandler});
        }
    };
};

