
<!DOCTYPE html PUBLIC "-//WAPFORUM//DTD XHTML Mobile 1.2//EN" "http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd">
<%--
 =================================================================
  Licensed Materials - Property of IBM
  WebSphere Commerce
  (C) Copyright IBM Corp. 2011, 2014 All Rights Reserved.
  US Government Users Restricted Rights - Use, duplication or
  disclosure restricted by GSA ADP Schedule Contract with
  IBM Corp.
 =================================================================
--%>
<%--
  *****
  * This JSP displays the user registration update page for the mobile store front. 
  *****
--%>
<!-- BEGIN UserRegistrationUpdateForm.jsp -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://commerce.ibm.com/base" prefix="wcbase" %>
<%@ taglib uri="http://commerce.ibm.com/foundation" prefix="wcf" %>
<c:set var="isMobilePage" value="true" />
<%@ include file="../../../../include/parameters.jspf" %>
<%@ include file="../../../../Common/EnvironmentSetup.jspf"%>
<%@ include file="../../../include/ErrorMessageSetup.jspf" %>
<%@ include file="./MandatoryUserRegistrationFields.jspf" %>

<%-- Required variables for breadcrumb support --%>
<c:set var="accountPageGroup" value="true" scope="request" />
<c:set var="personalInfoDisplayPage" value="true" scope="request" />
<wcf:rest var="person" url="store/{storeId}/person/@self">
	<wcf:var name="storeId" value="${WCParam.storeId}" encode="true"/>
</wcf:rest>
<c:set var="logonId" value="${person.logonId}" />
<c:choose>
	<c:when test="${empty storeError.key}">
		<c:set var="logonPassword" value="${person.logonPassword}" />
		<c:set var="logonPasswordVerify" value="${person.logonPassword}" />
		<c:set var="firstName" value="${person.firstName}" />
		<c:set var="lastName" value="${person.lastName}" />
		<c:set var="email1" value="${person.email1}" />
		<c:set var="preferredLanguage" value="${person.preferredLanguage}" />
		<c:set var="preferredCurrency" value="${person.preferredCurrency}" />
	</c:when>
	<c:otherwise>
		<c:set var="logonPassword" value="${WCParam.logonPassword}"/>
		<c:set var="logonPasswordVerify" value="${WCParam.logonPasswordVerify}"/>
		<c:set var="firstName" value="${WCParam.firstName}" />
		<c:set var="lastName" value="${WCParam.lastName}" />
		<c:set var="email1" value="${WCParam.email1}" />
		<c:set var="preferredLanguage" value="${WCParam.preferredLanguage}" />
		<c:set var="preferredCurrency" value="${WCParam.preferredCurrency}" />
	</c:otherwise>
</c:choose>
<c:if test="${empty supportedCurrencies || empty supportedLanguages}">
	<c:set var="key1" value="store/${storeId}/configuration/com.ibm.commerce.foundation.supportedCurrencies+com.ibm.commerce.foundation.supportedLanguages+${langId}"/>
	<c:set var="queryConfigurationResult" value="${cachedOnlineStoreMap[key1]}"/>
	<c:if test="${empty queryConfigurationResult}">
		<wcf:rest var="queryConfigurationResult" url="store/{storeId}/configuration" cached="true">
			<wcf:var name="storeId" value="${storeId}"/>
			<wcf:param name="langId" value="${langId}" />
			<wcf:param name="q" value="byConfigurationIds"/>
			<wcf:param name="configurationId" value="com.ibm.commerce.foundation.supportedCurrencies"/>
			<wcf:param name="configurationId" value="com.ibm.commerce.foundation.supportedLanguages"/>
		</wcf:rest>
		<wcf:set target = "${cachedOnlineStoreMap}" key="${key1}" value="${queryConfigurationResult}"/>
	</c:if>
	<c:forEach var="result" items="${queryConfigurationResult.resultList}">
		<c:if test="${result.configurationId == 'com.ibm.commerce.foundation.supportedCurrencies'}">
			<c:set var="supportedCurrencies" value="${result.configurationAttribute}" scope="request"/>
		</c:if>
		<c:if test="${result.configurationId == 'com.ibm.commerce.foundation.supportedLanguages'}">
			<c:set var="supportedLanguages" value="${result.configurationAttribute}" scope="request"/>
		</c:if>
      </c:forEach>

      <%--  Filter out the currencies that are not added to the current store  --%>
      <wcf:useBean var="tempSupportedCurrencies" classname="java.util.ArrayList"/>
	<c:set var="key2" value="store/${storeId}/online_store"/>
	<c:set var="storeSupportedCurrencies" value="${cachedOnlineStoreMap[key2].supportedCurrencies.supportedCurrencies}" />
	
	<c:if test="${fn:length(storeSupportedCurrencies) > 0}">
		<c:forEach var="currency" items="${supportedCurrencies}">
			<c:forEach var="storeCur" items="${storeSupportedCurrencies}">
				<c:if test="${currency.additionalValue[0].value eq storeCur}">
					<wcf:set target="${tempSupportedCurrencies}" value="${currency}"/>
				</c:if>
			</c:forEach>
		</c:forEach>
		<c:set var="supportedCurrencies" value="${tempSupportedCurrencies}" scope="request"/>
	</c:if>
</c:if>
<html xmlns="http://www.w3.org/1999/xhtml" lang="${shortLocale}" xml:lang="${shortLocale}">
	<head>
		<title>
			<fmt:message bundle="${storeText}" key="MUSREGU_TITLE">
				<fmt:param value="${storeName}" />
			</fmt:message>
		</title>
		<meta http-equiv="content-type" content="application/xhtml+xml" />
		<meta http-equiv="cache-control" content="max-age=300" />
		<meta name="viewport" content="${viewport}" />
		<%@ include file="../../../include/CommonAssetsForHeader.jspf" %>
	</head>
	
	<body>
		<div id="wrapper" class="ucp_active"> <!-- User Control Panel is ON-->
			<%@ include file="../../../include/HeaderDisplay.jspf" %>	
			<!-- Start Breadcrumb Bar -->
			<div id="breadcrumb" class="item_wrapper_gradient">
				<a id="back_link" href="javascript:if (history.length>0) {history.go(-1);}"><div class="back_arrow left">
					<div class="arrow_icon"></div>
				</div></a>
				<div class="page_title left"><fmt:message bundle="${storeText}" key="MUSER_REGU"/></div>
				<div class="clear_float"></div>
			</div>
			<!-- End Breadcrumb Bar -->
			
			<!-- Start Notification Container -->
			<c:choose>
				<c:when test="${!empty errorMessage}">
					<div id="notification_container" class="item_wrapper notification" style="display:block">
						<p class="error"><c:out value="${errorMessage}" /></p>
					</div>
				</c:when>	
				<c:otherwise>
					<c:if test="${!empty storeError.key}">
						<div id="notification_container" class="item_wrapper notification" style="display:block">
							<p class="error"><c:out value="${storeError.key}" /></p>
						</div>
					</c:if>
				</c:otherwise>
			</c:choose>	
			<!--End Notification Container -->
             <!-- Start User Registration -->
             <div class="item_wrapper item_wrapper_gradient">
				<p><fmt:message bundle="${storeText}" key="MUSREGU_UPDATE_MSG1"/><span class="required">*</span><fmt:message bundle="${storeText}" key="MUSREGU_UPDATE_MSG2"/></p>
			</div>
                  <c:choose>
                <c:when test="${locale == 'zh_CN'}">
                    <c:set var="userdetailsFieldOrder" value="LAST_NAME,first_name, EMAIL1" />
                </c:when>
                <c:when test="${locale eq 'ar_EG'}">
                    <c:set var="userdetailsFieldOrder" value="first_name,LAST_NAME,EMAIL1" />
                </c:when>
                <c:when test="${locale == 'ru_RU'}">
                    <c:set var="userdetailsFieldOrder" value="first_name,LAST_NAME,EMAIL1" />
                </c:when>
                <c:when test="${locale == 'zh_TW'}">
                    <c:set var="userdetailsFieldOrder" value="LAST_NAME,first_name,EMAIL1" />
                </c:when>
                <c:when test="${locale == 'ja_JP' || locale == 'ko_KR'}">
                    <c:set var="userdetailsFieldOrder" value="LAST_NAME,FIRST_NAME, EMAIL1" />
                </c:when>
                <c:when test="${locale == 'de_DE' || locale == 'es_ES' || locale == 'fr_FR' || locale == 'it_IT' || locale == 'ro_RO'}">
                    <c:set var="userdetailsFieldOrder" value="first_name,LAST_NAME,EMAIL1" />
                </c:when>
                <c:when test="${locale == 'pl_PL'}">
                    <c:set var="userdetailsFieldOrder" value="first_name,LAST_NAME, EMAIL1" />
                </c:when>
                <c:otherwise>
                    <c:set var="userdetailsFieldOrder" value="first_name,LAST_NAME,EMAIL1" />
                </c:otherwise>
            </c:choose>
                  <form id="my_account_personal_information_form" method="post" action="RESTUserRegistrationUpdate">
                        <input type="hidden" id="UserDetailsForm_FieldsOrderByLocale" name="UserDetailsForm_FieldsOrderByLocale" value="${userdetailsFieldOrder}" autocomplete="off">
                        <input type="hidden" name="storeId" value="<c:out value="${WCParam.storeId}" />" id="WC_MUserRegistrationUpdateForm_FormInput_storeId" />
                        <input type="hidden" name="catalogId" value="<c:out value="${WCParam.catalogId}" />" id="WC_MUserRegistrationUpdateForm_FormInput_catalogId" />
                        <input type="hidden" name="langId" value="<c:out value="${langId}" />" id="WC_MUserRegistrationUpdateForm_FormInput_langId" />
                        <input type="hidden" name="URL" value="m30MyAccountDisplay" id="WC_MUserRegistrationUpdateForm_FormInput_URL" />
                        <input type="hidden" name="errorViewName" value="m30UserRegistrationUpdate" id="WC_MUserRegistrationUpdateForm_FormInput_errorViewName" />
                        <input type="hidden" name="registerType" value="RegisteredPerson" id="WC_MUserRegistrationUpdateForm_FormInput_registerType" />
                        <input type="hidden" name="editRegistration" value="Y" id="WC_MUserRegistrationUpdateForm_FormInput_editRegistration" />
                        <input type="hidden" name="logonId" value="<c:out value="${logonId}" />" id="WC_MUserRegistrationUpdateForm_FormInput_logonId" />
                        <input type="hidden" name="authToken" value="${authToken}" id="WC_MUserRegistrationUpdateForm_FormInput_authToken" />
                        <div id="my_account_personal_information1" class="item_wrapper">
                              <fieldset> 
                                    <div><label for="WC_MUserRegistrationUpdateForm_FormInput_logonPassword"><fmt:message bundle="${storeText}" key="MUSREG_PASSWORD1"/> </label></div>
                                    <input type="password" id="WC_MUserRegistrationUpdateForm_FormInput_logonPassword" name="logonPasswordOld" value="<c:out value="${logonPassword}" />" class="inputfield input_width_standard" />            
                                    <div class="item_spacer"></div>
                                    
                                    <div><label for="WC_MUserRegistrationUpdateForm_FormInput_logonPasswordVerify"><fmt:message bundle="${storeText}" key="MUSREG_VPASSWORD"/></label></div>
                                    <input type="password" id="WC_MUserRegistrationUpdateForm_FormInput_logonPasswordVerify" name="logonPasswordVerifyOld" value="<c:out value="${logonPasswordVerify}" />" class="inputfield input_width_standard" />
                                    <div class="item_spacer"></div>
                                    
                                    <div><label for="WC_MUserRegistrationUpdateForm_FormInput_firstName"><c:if test="${fn:contains(mandatoryFields, 'firstName')}"><span class="required">*</span></c:if><fmt:message bundle="${storeText}" key="MUSREG_FNAME"/></label></div>
                                    <input type="text" id="WC_MUserRegistrationUpdateForm_FormInput_firstName" name="firstName" value="<c:out value="${firstName}" />" class="inputfield input_width_standard" />
                                    <div class="item_spacer"></div>
                                    
                                    <div><label for="WC_MUserRegistrationUpdateForm_FormInput_lastName"><c:if test="${fn:contains(mandatoryFields, 'lastName')}"><span class="required">*</span></c:if><fmt:message bundle="${storeText}" key="MUSREG_LNAME"/></label></div>
                                    <input type="text" id="WC_MUserRegistrationUpdateForm_FormInput_lastName" name="lastName" value="<c:out value="${lastName}" />" class="inputfield input_width_standard" />
                                    <div class="item_spacer"></div>
                                                                        
                                    <div><label for="WC_MUserRegistrationUpdateForm_FormInput_email1"><c:if test="${fn:contains(mandatoryFields, 'email1')}"><span class="required">*</span></c:if><fmt:message bundle="${storeText}" key="MUSREG_EMAIL"/></label></div>
                                    <input type="email" id="WC_MUserRegistrationUpdateForm_FormInput_email1" name="email1" value="<c:out value="${email1}" />" class="inputfield input_width_standard" />
                              </fieldset>
                        </div>
                        
                        <div id="my_account_personal_information2" class="item_wrapper">
                              <fieldset>
                                    <flow:ifEnabled feature="preferredLanguage">
                                    <c:if test="${_iPhoneHybridApp != true}">
                                          <div><label for="WC_MUserRegistrationUpdateForm_FormInput_preferredLanguage"><fmt:message bundle="${storeText}" key="MUSREG_PREF_LANG"/></label></div>
                                          <div class="dropdown_container">
                                                <select id="WC_MUserRegistrationUpdateForm_FormInput_preferredLanguage" name="preferredLanguage" class="inputfield input_width_standard">
                                                      <c:forEach var="supportedLanguage" items="${supportedLanguages}">
                                                            <c:set var="currentLocaleName" value="${json:findJSONObject(supportedLanguage.additionalValue, 'name', 'localeName').value}"/>
                                                            <c:set var="currentLanguageId" value="${json:findJSONObject(supportedLanguage.additionalValue, 'name', 'languageId').value}"/>
                                                            <c:choose>
                                                                  <%-- pre-select the appropriate value in the drop down list. --%>
                                                                  <c:when test="${(currentLocaleName == preferredLanguage) || (currentLanguageId == preferredLanguage)}">
                                                                        <option value="${currentLanguageId}" selected="selected"><c:out value="${supportedLanguage.primaryValue.value}" /></option>
                                                                  </c:when>
                                                                  <c:otherwise>
                                                                        <option value="${currentLanguageId}"><c:out value="${supportedLanguage.primaryValue.value}" /></option>
                                                                  </c:otherwise>
                                                            </c:choose>
                                                      </c:forEach>
                                                </select>
                                          </div>                  
                                          <div class="item_spacer"></div>
                                    </c:if>
                                    </flow:ifEnabled>
						<flow:ifDisabled feature="preferredLanguage">
							<%-- The value is set to the previously entered preferredLanguage if available.  Otherwise, the value is set to the locale in the CommandContext.  The default language code for B2C is the locale - en_US. But that cannot be used for B2B. The default preferredLanguage for B2B is an integerger.. --%>
	                        <c:set var="preferredLanguage" value="${paramSource.preferredLanguage}"/> <c:if test="${empty preferredLanguage}">  <c:set var="preferredLanguage" value="${CommandContext.locale}"/> </c:if>
	                        <c:if test="${!empty preferredLanguage}">  <c:forEach items="${sdb.supportedLanguages}" var="language"> 
							<c:if test="${language.localeName == preferredLanguage || language.languageId == preferredLanguage }"> <c:set var="preferredLanguage" value="${language.languageId}"/>  </c:if> </c:forEach> </c:if>
		                        <input type="hidden" name="preferredLanguage" value="<c:out value="${preferredLanguage}" default="${CommandCOntext.local }" />" id="WC_MUserRegistrationUpdateForm_FormInput_preferredLanguage"/>
                        </flow:ifDisabled>
                                    <flow:ifEnabled feature="preferredCurrency">
                                    <div><label for="WC_MUserRegistrationUpdateForm_FormInput_preferredCurrency"><fmt:message bundle="${storeText}" key="MUSREG_PREF_CURR"/></label></div>
                                    <div class="dropdown_container">
                                          <select id="WC_MUserRegistrationUpdateForm_FormInput_preferredCurrency" name="preferredCurrency" class="inputfield input_width_standard">
                                                <c:forEach var="supportedCurrency" items="${supportedCurrencies}">
                                                      <c:set var="currentCurrencyCode" value="${json:findJSONObject(supportedCurrency.additionalValue, 'name', 'currencyCode').value}"/>
                                                      <c:choose>
                                                            <%-- pre-select the appropriate value in the drop down list. --%>
                                                            <c:when test="${(currentCurrencyCode == preferredCurrency) || (currentCurrencyCode == CommandContext.currency)}">
                                                                  <option value="${currentCurrencyCode}" selected="selected"><c:out value="${supportedCurrency.primaryValue.value}"/></option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                  <option value="${currentCurrencyCode}"><c:out value="${supportedCurrency.primaryValue.value}"/></option>
                                                            </c:otherwise>
                                                      </c:choose>
                                                </c:forEach>
                                          </select>
                                    </div>                  
                                    </flow:ifEnabled>
                                    <flow:ifDisabled feature="preferredCurrency">
                                          <input type="hidden" name="preferredCurrency" value="<c:out value="${preferredCurrency}" default = "${CommandContext.currency}"/>" id="WC_MUserRegistrationUpdateForm_FormInput_preferredCurrency"/>
                                    </flow:ifDisabled>
                              </fieldset>
                        </div>
                        <div class="item_wrapper_button">
                              <div class="single_button_container">
                                    <input type="button" id="my_account_personal_information_form_submit" name="my_account_personal_information_form_submit" value="<fmt:message bundle="${storeText}" key="MUSREGU_UPDATE"/>" class="primary_button button_half" onclick="checkField('my_account_personal_information_form');return false;"  />
                              </div>
                              <div class="clear_float"></div>
                        </div>
                  </form>
                  <!-- End User Registration -->

                  <%@ include file="../../../include/FooterDisplay.jspf" %> 
                  
            </div>
            
      <script type="text/javascript">
      //<![CDATA[
            function checkField(formName) {
            var form = document.forms[formName];
            var lastname = form['WC_MUserRegistrationUpdateForm_FormInput_lastName'].value;
                  if (!AddressHelper.validateAddressForm(form,'WC_MUserRegistrationUpdateForm_FormInput_')){return false;}
                  if ((form.logonPasswordOld.value != null && form.logonPasswordOld.value.length > 0)  ||
                        (form.logonPasswordVerifyOld.value != null && form.logonPasswordVerifyOld.value.length > 0)) {
                        form.logonPasswordOld.name = "logonPassword";
                        form.logonPasswordVerifyOld.name = "logonPasswordVerify";
                  }
                  
                  <c:if test="${_iPhoneHybridApp != true}">
                        // Update the current page language to the selected language preference.
                        var selectedLanguageId = document.getElementById("WC_MUserRegistrationUpdateForm_FormInput_preferredLanguage").value;
                        <c:forEach var="dbLanguage" items="${sdb.supportedLanguages}">
                              if("<c:out value="${dbLanguage.languageId}"/>" == selectedLanguageId) {
                                    document.getElementById("WC_MUserRegistrationUpdateForm_FormInput_langId").value = "<c:out value="${dbLanguage.languageId}"/>";
                                    var selectedLanguageLocale = "<c:out value="${dbLanguage.localeName}"/>";;
                              }
                        </c:forEach>
                  </c:if>
                  
                  <c:choose>
                        <c:when test="${_androidHybridApp}">
                              var oldLanguageLocale = "<c:out value="${preferredLanguage}" />";
                              if (oldLanguageLocale != selectedLanguageLocale) {
                                    if ("true" == Android.isLanguageUpdateNeedToRestart(selectedLanguageLocale)) {
                                          if (confirm(Android.getConfirmLanguageUpdateWarning())) {
                                                form.submit();
                                          }
                                    } else {
                                          form.submit();
                                    }
                              } else {
                                    form.submit();
                              }
                        </c:when>
                        <c:otherwise>
                              form.submit();
                        </c:otherwise>
                  </c:choose>
                  
            }
      //]]>
      </script>
      <script type="text/javascript">
        //messages used by AddressHelper.js
        $(document).ready( function() {
            <fmt:message bundle="${storeText}" key="ERROR_FirstNameTooLong" var="ERROR_FirstNameTooLong"/>         
            <fmt:message bundle="${storeText}" key="ERROR_FirstNameEmpty" var="ERROR_FirstNameEmpty"/>
            <fmt:message bundle="${storeText}" key="ERROR_LastNameEmpty" var="ERROR_LastNameEmpty"/>         
            <fmt:message bundle="${storeText}" key="ERROR_EmailEmpty" var="ERROR_EmailEmpty"/>
            <fmt:message bundle="${storeText}" key="PWDREENTER_DO_NOT_MATCH" var="PWDREENTER_DO_NOT_MATCH"/>
            <fmt:message bundle="${storeText}" key="REQUIRED_FIELD_ENTER" var="REQUIRED_FIELD_ENTER"/>
            <fmt:message bundle="${storeText}" key="AB_UPDATE_SUCCESS" var="AB_UPDATE_SUCCESS"/>
            <fmt:message bundle="${storeText}" key="AB_DELETE_SUCCESS" var="AB_DELETE_SUCCESS"/>
            <fmt:message bundle="${storeText}" key="AB_ADDNEW_SUCCESS" var="AB_ADDNEW_SUCCESS"/>
            <fmt:message bundle="${storeText}" key="WISHLIST_INVALIDEMAILFORMAT" var="WISHLIST_INVALIDEMAILFORMAT"/>
            <fmt:message bundle="${storeText}" key="ERROR_INVALIDPHONE" var="ERROR_INVALIDPHONE"/>
            <fmt:message bundle="${storeText}" key="ERROR_DEFAULTADDRESS" var="ERROR_DEFAULTADDRESS"/>
            <fmt:message bundle="${storeText}" key="ERROR_INVALIDEMAILFORMAT" var="ERROR_INVALIDEMAILFORMAT"/>
            MessageHelper.setMessage("ERROR_FirstNameTooLong", <wcf:json object="${ERROR_FirstNameTooLong}"/>);
            MessageHelper.setMessage("ERROR_LastNameTooLong", <wcf:json object="${ERROR_LastNameTooLong}"/>);
            MessageHelper.setMessage("ERROR_FirstNameEmpty", <wcf:json object="${ERROR_FirstNameEmpty}"/>);
            MessageHelper.setMessage("ERROR_LastNameEmpty", <wcf:json object="${ERROR_LastNameEmpty}"/>);        
            MessageHelper.setMessage("ERROR_PhonenumberEmpty", <wcf:json object="${ERROR_PhonenumberEmpty}"/>);
            MessageHelper.setMessage("ERROR_EmailEmpty", <wcf:json object="${ERROR_EmailEmpty}"/>);
            MessageHelper.setMessage("PWDREENTER_DO_NOT_MATCH", <wcf:json object="${PWDREENTER_DO_NOT_MATCH}"/>);
            MessageHelper.setMessage("REQUIRED_FIELD_ENTER", <wcf:json object="${REQUIRED_FIELD_ENTER}"/>);
                  MessageHelper.setMessage("ERROR_INVALIDEMAILFORMAT", <wcf:json object="${ERROR_INVALIDEMAILFORMAT}"/>);
            AddressBookFormJS.setCommonParameters("<c:out value='${langId}'/>", "<c:out value='${WCParam.storeId}'/>","<c:out value='${WCParam.catalogId}'/>");
        });
    </script>
            
      <%@ include file="../../../../Common/JSPFExtToInclude.jspf"%> </body>
      
</html>
<!-- END UserRegistrationUpdateForm.jsp -->
