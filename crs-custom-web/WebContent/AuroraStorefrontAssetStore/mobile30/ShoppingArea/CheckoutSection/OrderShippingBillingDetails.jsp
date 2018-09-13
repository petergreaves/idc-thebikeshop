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
  * This JSP displays a form to create or edit an existing shipping or/and
  * billing address
  *****
--%>

<!-- BEGIN OrderShippingBillingDetails.jsp -->

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://commerce.ibm.com/base" prefix="wcbase" %>
<%@ taglib uri="http://commerce.ibm.com/foundation" prefix="wcf" %>
<c:set var="isMobilePage" value="true" />

<%@ include file="../../../include/parameters.jspf" %>
<%@ include file="../../../Common/EnvironmentSetup.jspf" %>
<%@ include file="../../include/ErrorMessageSetup.jspf" %>
<%@ include file="../../UserArea/AccountSection/RegistrationSubsection/MandatoryUserRegistrationFields.jspf" %>

<%-- Required variables for breadcrumb support --%>
<c:choose>
	<c:when test="${!empty WCParam.fromPage && WCParam.fromPage == 'MyAccount'}">
		<c:set var="accountPageGroup" value="true" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="shoppingcartPageGroup" value="true" scope="request"/>
	</c:otherwise>
</c:choose>
<c:set var="billingDetailsPage" value="true" scope="request"/>

<c:set var="storeId" value="${WCParam.storeId}" />
<c:set var="catalogId" value="${WCParam.catalogId}" />

<c:set var="fromPage" value="" />
<c:if test="${!empty WCParam.fromPage}">
	<c:set var="fromPage" value="${WCParam.fromPage}" />
</c:if>

<c:set var="shipAddSelect" value="false" />
<c:if test="${!empty WCParam.shipAddSelect}">
	<c:set var="shipAddSelect" value="${WCParam.shipAddSelect}" />
</c:if>

<wcf:rest var="person" url="store/{storeId}/person/@self">
	<wcf:var name="storeId" value="${WCParam.storeId}" encode="true"/>
</wcf:rest>

<html xmlns="http://www.w3.org/1999/xhtml" lang="${shortLocale}" xml:lang="${shortLocale}">

	<head>
		<title><fmt:message bundle="${storeText}" key="ADDRESS_TITLE"/></title>
		<meta http-equiv="content-type" content="application/xhtml+xml" />
		<meta http-equiv="cache-control" content="max-age=300" />
		<meta name="viewport" content="${viewport}" />
		<link rel="stylesheet" type="text/css" href="${env_vfileStylesheet_m30}" />
		<script type="text/javascript" src="${jspStoreImgDir}${mobileBasePath}/javascript/DeviceEnhancement.js" ></script>

		<%@ include file="../../include/CommonAssetsForHeader.jspf" %>
	</head>

	<body onLoad="loadCountryStates();">
		<div id="wrapper" class="ucp_active"> <!-- User Control Panel is ON-->
			<%@ include file="../../include/HeaderDisplay.jspf" %>

			<!-- Start Breadcrumb Bar -->
			<div id="breadcrumb" class="item_wrapper_gradient">
				<a id="back_link" href="javascript:if (history.length>0) {history.go(-1);}"><div class="back_arrow left">
					<div class="arrow_icon"></div>
				</div></a>
				<div class="page_title left">
					<c:choose>
						<c:when test="${fromPage == 'MyAccount'}">
							<fmt:message bundle="${storeText}" key="ADDRESS_TITLE"/>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${shipAddSelect == 'true'}">
									<fmt:message bundle="${storeText}" key="CHECKOUT_SHIPPING_ADDRESS_TITLE"/>
								</c:when>
								<c:otherwise>
									<fmt:message bundle="${storeText}" key="CHECKOUT_BILLING_ADDRESS_TITLE"/>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</div>
				<div class="clear_float"></div>
			</div>
			<!-- End Breadcrumb Bar -->
			
			<!-- Start Notification Container -->
			<c:if test="${!empty errorMessage}">
				<div id="notification_container" class="item_wrapper notification" style="display:block">
					<p class="error"><c:out value="${errorMessage}"/><p>
				</div>

				<c:set var="nickName" value="${WCParam.nickName}"/>
				<c:set var="firstName" value="${WCParam.firstName}"/>
				<c:set var="lastName" value="${WCParam.lastName}"/>
				<c:set var="addressLine0" value="${WCParam.address1}"/>
				<c:set var="addressLine1" value="${WCParam.address2}"/>
				<c:set var="city" value="${WCParam.city}"/>
				<c:set var="postalCode" value="${WCParam.zipCode}"/>
				<c:set var="telephone1" value="${WCParam.phone1}"/>
				<c:set var="emailAddress1" value="${WCParam.email1}"/>
				<c:set var="countryDisplayName" value="${WCParam.country}"/>
				<c:set var="stateDisplayName" value="${WCParam.state}"/>
			</c:if>
			<!--End Notification Container -->

			<div class="item_wrapper item_wrapper_gradient">
				<p><fmt:message bundle="${storeText}" key="MUSREGU_UPDATE_MSG1"/><span class="required">*</span><fmt:message bundle="${storeText}" key="MUSREGU_UPDATE_MSG2" /></p>
			</div>			

			<div id="billing_address" class="item_wrapper">
				<c:choose>
					<c:when test="${!empty person.addressId && person.addressId eq WCParam.addressId}">
						<c:set var="contact" value="${person}"/>
					</c:when>
					<c:otherwise>
						<c:set var="personAddresses" value="${person}"/>
						<c:forEach var="addressBookContact" items="${personAddresses.contact}">
							<c:if test="${addressBookContact.addressId eq WCParam.addressId}">
								<c:set var="contact" value="${addressBookContact}"/>
							</c:if>
						</c:forEach>
					</c:otherwise>
				</c:choose>

				<c:if test="${!empty contact}">
					<c:set var="resolvedAddressId" value="${contact.addressId}"/>
					<c:set var="nickName" value="${contact.nickName}"/>
					<c:set var="addressType" value="${contact.addressType}"/>
					<c:set var="firstName" value="${contact.firstName}"/>
					<c:set var="lastName" value="${contact.lastName}"/>
					<c:set var="addressLine0" value="${contact.addressLine[0]}"/>
					<c:set var="addressLine1" value="${contact.addressLine[1]}"/>
					<c:set var="city" value="${contact.city}"/>
					<c:set var="postalCode" value="${contact.zipCode}"/>
					<c:set var="telephone1" value="${contact.phone1}"/>
					<c:set var="emailAddress1" value="${contact.email1}"/>
					<c:set var="countryDisplayName" value="${contact.country}"/>
					<c:set var="stateDisplayName" value="${contact.state}"/>
				</c:if>

				<c:set var="key1" value="store/${WCParam.storeId}/country/country_state_list+${langId}"/>
				<c:set var="countryBean" value="${cachedOnlineStoreMap[key1]}"/>
				<c:if test="${empty countryBean}">
					<wcf:rest var="countryBean" url="store/{storeId}/country/country_state_list" cached="true">
						<wcf:var name="storeId" value="${WCParam.storeId}" />
						<wcf:param name="langId" value="${langId}" />
					</wcf:rest>
					<wcf:set target = "${cachedOnlineStoreMap}" key="${key1}" value="${countryBean}"/>
				</c:if>

				<c:if test="${!empty countryDisplayName}">
					<c:forEach var="country" items="${countryBean.countries}">
						<c:if test="${!empty country.code && country.code == countryDisplayName}">
							<c:set var="countryDisplayName" value="${country.displayName}"/>
						</c:if>

						<c:if test="${!empty country.states}">
							<c:forEach var="state" items="${country.states}" varStatus="counter">
								<c:if test="${!empty state.code && state.code == stateDisplayName}">
									<c:set var="stateDisplayName" value="${state.displayName}"/>
								</c:if>
							</c:forEach>
						</c:if>
					</c:forEach>
				</c:if>
			
   

				<c:set var="personChangeServiceAction" value="PersonChangeServiceAddressAdd" />
				<c:if test="${!empty contact}">
					<c:set var="personChangeServiceAction" value="PersonChangeServiceAddressUpdate" />
				</c:if>
				<c:choose>	
            <c:when test="${locale == 'zh_CN'}">
                <c:set var="addressFieldOrder" value="LAST_NAME,first_name,new_line,COUNTRY/REGION,STATE/PROVINCE,new_line,CITY,ADDRESS,ZIP,new_line,EMAIL1,phone1" />
            </c:when>
            <c:when test="${locale eq 'ar_EG'}">
                <c:set var="addressFieldOrder" value="first_name,LAST_NAME,new_line,ADDRESS,CITY,new_line,STATE/PROVINCE,new_line,ZIP,new_line,COUNTRY/REGION,new_line,EMAIL1,phone1" />
            </c:when>
            <c:when test="${locale == 'ru_RU'}">
                <c:set var="addressFieldOrder" value="first_name,middle_name,new_line,LAST_NAME,ADDRESS,new_line,ZIP,CITY,new_line,state/province,COUNTRY/REGION,new_line,EMAIL1,phone1" />
            </c:when>
            <c:when test="${locale == 'zh_TW'}">
                <c:set var="addressFieldOrder" value="LAST_NAME,first_name,new_line,COUNTRY/REGION,STATE/PROVINCE,new_line,CITY,ZIP,new_line,ADDRESS,EMAIL1,phone1" />
            </c:when>
            <c:when test="${locale == 'ja_JP' || locale == 'ko_KR'}">
                <c:set var="addressFieldOrder" value="LAST_NAME,FIRST_NAME,new_line,COUNTRY/REGION,ZIP,new_line,STATE/PROVINCE,CITY,ADDRESS,EMAIL1,phone1" />
            </c:when>
            <c:when test="${locale == 'de_DE' || locale == 'es_ES' || locale == 'fr_FR' || locale == 'it_IT' || locale == 'ro_RO'}">
                <c:set var="addressFieldOrder" value="first_name,LAST_NAME,new_line,ADDRESS,ZIP,new_line,CITY,state/province,new_line,COUNTRY/REGION,EMAIL1,phone1" />
            </c:when>
            <c:when test="${locale == 'pl_PL'}">
                <c:set var="addressFieldOrder" value="first_name,LAST_NAME,new_line,ADDRESS,ZIP,new_line,CITY,STATE/PROVINCE,new_line,COUNTRY/REGION,EMAIL1,phone1" />
            </c:when>
            <c:otherwise>
                <c:set var="addressFieldOrder" value="first_name,LAST_NAME,new_line,ADDRESS,CITY,new_line,COUNTRY/REGION,STATE/PROVINCE,ZIP,new_line,EMAIL1,phone1" />
            </c:otherwise>
            </c:choose>
      

				<form id="billing_address_form" method="post" action="${personChangeServiceAction}">
				<input type="hidden" id="AddressForm_FieldsOrderByLocale" name="AddressForm_FieldsOrderByLocale" value="${addressFieldOrder }" autocomplete="off">
					<fieldset>
				<c:if test="${_androidHybridApp || _iPhoneHybridApp || _worklightHybridApp }" >
							<div class="single_button_container">
								<input type="button" id="choose_contact" value="<fmt:message bundle="${storeText}" key="CHOOSE_CONTACT"/>" class="secondary_button button_half" onClick="selectContactFromAddressBook()" />
							</div>
							<div class="clear_float"></div>
							<div class="item_spacer_10px"></div>
						</c:if>
						<c:choose>
							<c:when test="${!empty contact}">
								<div class="bold"><c:out value="${nickName}" escapeXml="true"/></div>
								<input type="hidden" id="addressId" name="addressId" value="<c:out value="${resolvedAddressId}" escapeXml="true"/>" />
								<input type="hidden" id="nickName" name="nickName" value="<c:out value="${nickName}" escapeXml="true"/>" class="inputfield input_width_standard" />
							</c:when>
							<c:otherwise>
								<div><label for="nickName"><span class="required">*</span><fmt:message bundle="${storeText}" key="NICK_NAME" /></label></div>
								<input type="text" id="nickName" name="nickName" value="<c:out value="${nickName}" escapeXml="true"/>" class="inputfield input_width_standard" />
							</c:otherwise>
						</c:choose>
						<div class="item_spacer"></div>

						<c:if test="${fromPage == 'MyAccount'}">
							<div class="dropdown_container">
								<select id="addressType" name="addressType" class="inputfield input_standard">
									<c:choose>
										<c:when test="${!empty contact && addressType == 'Shipping'}">
											<option value="Shipping" selected><fmt:message bundle="${storeText}" key="TYPE_SHIPPING_ADDRESS"/></option>
										</c:when>
										<c:otherwise>
											<option value="Shipping"><fmt:message bundle="${storeText}" key="TYPE_SHIPPING_ADDRESS"/></option>
										</c:otherwise>
									</c:choose>
									<c:choose>
										<c:when test="${!empty contact && addressType == 'Billing'}">
											<option value="Billing" selected><fmt:message bundle="${storeText}" key="TYPE_BILLING_ADDRESS"/></option>
										</c:when>
										<c:otherwise>
											<option value="Billing"><fmt:message bundle="${storeText}" key="TYPE_BILLING_ADDRESS"/></option>
										</c:otherwise>
									</c:choose>
									<c:choose>
										<c:when test="${!empty contact}">
											<c:choose>
												<c:when test="${addressType == 'ShippingAndBilling'}">
													<option value="ShippingAndBilling" selected><fmt:message bundle="${storeText}" key="TYPE_BOTH_ADDRESS"/></option>
												</c:when>
												<c:otherwise>
													<option value="ShippingAndBilling"><fmt:message bundle="${storeText}" key="TYPE_BOTH_ADDRESS"/></option>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<option value="ShippingAndBilling" selected><fmt:message bundle="${storeText}" key="TYPE_BOTH_ADDRESS"/></option>
										</c:otherwise>
									</c:choose>
								</select>
							</div>	
							<div class="item_spacer"></div>
						</c:if>
					
						<div><label for="firstName"><c:if test="${fn:contains(mandatoryFields, 'firstName')}"><span class="required">*</span></c:if><fmt:message bundle="${storeText}" key="MUSREG_FNAME"/></label></div>
						<input type="text" id="firstName" name="firstName" value="<c:out value="${firstName}" escapeXml="true"/>" class="inputfield input_width_standard" />
						<div class="item_spacer"></div>

						<div><label for="lastName"><c:if test="${fn:contains(mandatoryFields, 'lastName')}"><span class="required">*</span></c:if><fmt:message bundle="${storeText}" key="MUSREG_LNAME"/></label></div>
						<input type="text" id="lastName" name="lastName" value="<c:out value="${lastName}" escapeXml="true"/>" class="inputfield input_width_standard" />
						<div class="item_spacer"></div>

						<div><label for="address1"><c:if test="${fn:contains(mandatoryFields, 'address1')}"><span class="required">*</span></c:if><fmt:message bundle="${storeText}" key="MOSB_STREET_ADDRESS"/></label></div>
						<input type="text" id="address1" name="address1" value="<c:out value="${addressLine0}" escapeXml="true"/>" class="inputfield input_width_standard" />
						<div class="clear_float"></div>
						<label for="address2" class="nodisplay"><fmt:message bundle="${storeText}" key="MOSB_STREET_ADDRESS"/></label>
						<input type="text" id="address2" name="address2" value="<c:out value="${addressLine1}" escapeXml="true"/>" class="inputfield input_width_standard" />
						<div class="item_spacer"></div>

						<div><label for="city"><c:if test="${fn:contains(mandatoryFields, 'city')}"><span class="required">*</span></c:if><fmt:message bundle="${storeText}" key="MOSB_CITY"/></label></div>
						<input type="text" id="city" name="city" value="<c:out value="${city}" escapeXml="true"/>" class="inputfield input_width_standard" />
						<div class="item_spacer"></div>

						<div><label for="country"><c:if test="${fn:contains(mandatoryFields, 'country')}"><span class="required">*</span></c:if><fmt:message bundle="${storeText}" key="COUNTRY_REGION"/></label></div>
						<div class="dropdown_container">
							<c:set var="country_language_select" value="${fn:split(locale, '_')}" />
							<select onchange="loadCountryStates();" id="country" name="country" class="inputfield input_width_standard">
								<c:forEach var="country" items="${countryBean.countries}">
									<option value="<c:out value="${country.code}"/>"
							<c:choose>
								<c:when test="${!empty countryDisplayName}">
									<c:if test="${country.displayName == countryDisplayName}">
										selected="selected"
									</c:if>
								</c:when>
								<c:otherwise>
									<c:if test="${country.code eq country_language_select[1]}">
										selected="selected"
										<c:set var="country1" value="${country.code}" />
									</c:if>
								</c:otherwise>
							</c:choose>
							>
						<c:out value="${country.displayName}"/>
									</option>
								</c:forEach>
							</select>
						</div>		
						<div class="item_spacer"></div>

						<div><label for="state">
								<flow:ifEnabled feature="RequireStateProvince">
									<c:if test="${fn:contains(mandatoryFields, 'state')}"><span class="required">*</span></c:if>
								</flow:ifEnabled>
								<fmt:message bundle="${storeText}" key="MOSB_STATE_PROVINCE"/>
							</label>
						</div>
						<div class="dropdown_container">
							<%--	Create seperate select boxes for countries with state
									lists.  Hide the list unless the country is selected.
							--%>

							<c:forEach var="country" items="${countryBean.countries}">
								<c:if test="${!empty country.states}">
									<div id=${country.code}_states_div style="display:none">
										<label for="${country.code}_states" class="nodisplay"><fmt:message bundle="${storeText}" key="MOSB_STATE_PROVINCE"/></label>
										<select id="${country.code}_states" class="inputfield input_width_standard">
											<c:forEach var="state" items="${country.states}" varStatus="counter">
												<option value="${state.code}"
													<c:if test="${state.displayName == stateDisplayName}">
														selected="selected"
													</c:if>
												><c:out value="${state.displayName}" /></option>
											</c:forEach>
										</select>
									</div>
								</c:if>
							</c:forEach>
							<div id="states_div"><input type="text" id="state" name="state" value="<c:out value="${stateDisplayName}" escapeXml="true"/>" class="inputfield input_width_standard" /></div>
						</div>
						<div class="item_spacer"></div>

						<div><label for="zipCode"><c:if test="${fn:contains(mandatoryFields, 'zipCode')}"><span class="required">*</span></c:if><fmt:message bundle="${storeText}" key="ZIPCODE_POSTALCODE"/></label></div>
						<input type="text" id="zipCode" name="zipCode" value="<c:out value="${postalCode}" escapeXml="true"/>" class="inputfield input_width_standard" />
						<div class="item_spacer"></div>

						<div><label for="phone1"><c:if test="${fn:contains(mandatoryFields, 'phone1')}"><span class="required">*</span></c:if><fmt:message bundle="${storeText}" key="MOSB_PHONE_NUMBER"/></label></div>
						<input type="tel" id="phone1" name="phone1" value="<c:out value="${telephone1}" escapeXml="true"/>" class="inputfield input_width_standard" />
						<div class="item_spacer"></div>

						<div><label for="email1"><c:if test="${fn:contains(mandatoryFields, 'email1')}"><span class="required">*</span></c:if><fmt:message bundle="${storeText}" key="MUSREG_EMAIL"/></label></div>
						<input type="email" id="email1" name="email1" value="<c:out value="${emailAddress1}" escapeXml="true"/>" class="inputfield input_width_standard" />
						<div class="item_spacer"></div>

						<input type="hidden" name="storeId" value="${storeId}" />
						<input type="hidden" name="catalogId" value="${catalogId}" />
						<c:if test="${!empty WCParam.orderId}">
							<c:set var="order" value="${requestScope.order}" />
							<c:if test="${empty order || order==null}">
								<wcf:rest var="order" url="store/{storeId}/cart/@self">
									<wcf:var name="storeId" value="${storeId}" encode="true"/>
								</wcf:rest>
								<wcf:rest var="shippingInfo" url="store/{storeId}/cart/@self/shipping_info">
									<wcf:var name="storeId" value="${storeId}" encode="true"/>
								</wcf:rest>
							</c:if>

							<c:if test="${shippingInfo.orderItem[0].addressId eq resolvedAddressId}">
								<%-- We are now updating an address that is already used in the order as the shipping address.  Update the order with the newly revised address by calling the RESTOrderShipInfoUpdate service --%>
								<wcf:url var="updateOrderShippingInfoURL" value="RESTOrderShipInfoUpdate">
									<wcf:param name="authToken" value="${authToken}" />
								</wcf:url>
								<c:set var="updateOrderShippingInfo" value="${updateOrderShippingInfoURL}&URL="/>
							</c:if>
						</c:if>
					
						<c:choose>
							<c:when test="${shipAddSelect == 'true'}">
								<input type="hidden" name="URL" value="${updateOrderShippingInfo}m30OrderShippingAddressSelection" />
							</c:when>
							<c:otherwise>
								<input type="hidden" name="URL" value="${updateOrderShippingInfo}m30OrderBillingAddressSelection" />
							</c:otherwise>
						</c:choose>
						<input type="hidden" name="fromPage" value="${fn:escapeXml(WCParam.fromPage)}" />
						<input type="hidden" name="errorViewName" value="m30OrderBillingDetails" />
						<input type="hidden" name="authToken" value="${authToken}"/>

						<div class="single_button_container">
							<c:choose>				
								<c:when test="${fromPage == 'MyAccount'}">
									<input type="submit" id="update" name="update" value="<fmt:message bundle="${storeText}" key="MADDR_UPDATE"/>" class="primary_button button_half" onclick="submitAddressUpdate('billing_address_form');return false;" />
								</c:when>
								<c:otherwise>
									<input type="submit" id="continue_checkout" name="continue_checkout" value="<fmt:message bundle="${storeText}" key="CONTINUE_CHECKOUT"/>" class="primary_button button_half" onclick="submitAddressUpdate('billing_address_form');return false;" />
								</c:otherwise>
							</c:choose>
						</div>
						<div class="item_spacer_5px"></div>
					</fieldset>
				</form>
			</div>

			<%@ include file="../../include/FooterDisplay.jspf" %>
		</div>

	<script type="text/Javascript">
	//<![CDATA[

		function submitAddressUpdate(formName) {
			var country = document.getElementById('country').value;
			var stateBox = document.getElementById(country + "_states");
			if(stateBox != null) {
				document.getElementById("billing_address_form").state.value = document.getElementById(country + "_states").value;
			}
			var form = document.forms[formName];
			if (!AddressHelper.validateAddressForm(form)){return false;}
			document.getElementById("billing_address_form").submit();
			

			
		}

		function hideCountryStates() {
			<c:forEach var="country" items="${countryBean.countries}">
				<c:if test="${!empty country.states}">
					document.getElementById("${country.code}_states_div").style.display = "none";
				</c:if>
			</c:forEach>
			document.getElementById("states_div").style.display = "none";
		}

		function loadCountryStates() {
			var country = document.getElementById('country').value;
			var stateBox = document.getElementById(country + "_states_div");
			hideCountryStates();
			if(stateBox != null) {
				document.getElementById(country + "_states_div").style.display = "block";
			}
			else {
				document.getElementById("states_div").style.display = "block";
			}
		}

	//]]> 
	</script>
	<script type="text/javascript">
        //messages used by AddressBookForm.js
        $(document).ready( function() {
            <fmt:message bundle="${storeText}" key="ERROR_RecipientTooLong" var="ERROR_RecipientTooLong"/>
            <fmt:message bundle="${storeText}" key="ERROR_FirstNameTooLong" var="ERROR_FirstNameTooLong"/>
            <fmt:message bundle="${storeText}" key="ERROR_LastNameTooLong" var="ERROR_LastNameTooLong"/>
            <fmt:message bundle="${storeText}" key="ERROR_MiddleNameTooLong" var="ERROR_MiddleNameTooLong"/>
            <fmt:message bundle="${storeText}" key="ERROR_AddressTooLong" var="ERROR_AddressTooLong"/>
            <fmt:message bundle="${storeText}" key="ERROR_CityTooLong" var="ERROR_CityTooLong"/>
            <fmt:message bundle="${storeText}" key="ERROR_StateTooLong" var="ERROR_StateTooLong"/>
            <fmt:message bundle="${storeText}" key="ERROR_CountryTooLong" var="ERROR_CountryTooLong"/>
            <fmt:message bundle="${storeText}" key="ERROR_ZipCodeTooLong" var="ERROR_ZipCodeTooLong"/>
            <fmt:message bundle="${storeText}" key="ERROR_PhoneTooLong" var="ERROR_PhoneTooLong"/>
            <fmt:message bundle="${storeText}" key="ERROR_RecipientEmpty" var="ERROR_RecipientEmpty"/>
            <fmt:message bundle="${storeText}" key="ERROR_FirstNameEmpty" var="ERROR_FirstNameEmpty"/>
            <fmt:message bundle="${storeText}" key="ERROR_LastNameEmpty" var="ERROR_LastNameEmpty"/>
            <fmt:message bundle="${storeText}" key="ERROR_MiddleNameEmpty" var="ERROR_MiddleNameEmpty"/>
            <fmt:message bundle="${storeText}" key="ERROR_AddressEmpty" var="ERROR_AddressEmpty"/>
            <fmt:message bundle="${storeText}" key="ERROR_CityEmpty" var="ERROR_CityEmpty"/>
            <fmt:message bundle="${storeText}" key="ERROR_StateEmpty" var="ERROR_StateEmpty"/>
            <fmt:message bundle="${storeText}" key="ERROR_CountryEmpty" var="ERROR_CountryEmpty"/>
            <fmt:message bundle="${storeText}" key="ERROR_ZipCodeEmpty" var="ERROR_ZipCodeEmpty"/>
            <fmt:message bundle="${storeText}" key="ERROR_PhonenumberEmpty" var="ERROR_PhonenumberEmpty"/>
            <fmt:message bundle="${storeText}" key="ERROR_EmailEmpty" var="ERROR_EmailEmpty"/>
            <fmt:message bundle="${storeText}" key="PWDREENTER_DO_NOT_MATCH" var="PWDREENTER_DO_NOT_MATCH"/>
            <fmt:message bundle="${storeText}" key="REQUIRED_FIELD_ENTER" var="REQUIRED_FIELD_ENTER"/>
            <fmt:message bundle="${storeText}" key="AB_UPDATE_SUCCESS" var="AB_UPDATE_SUCCESS"/>
            <fmt:message bundle="${storeText}" key="AB_DELETE_SUCCESS" var="AB_DELETE_SUCCESS"/>
            <fmt:message bundle="${storeText}" key="AB_ADDNEW_SUCCESS" var="AB_ADDNEW_SUCCESS"/>
            <fmt:message bundle="${storeText}" key="WISHLIST_INVALIDEMAILFORMAT" var="WISHLIST_INVALIDEMAILFORMAT"/>
            <fmt:message bundle="${storeText}" key="ERROR_INVALIDPHONE" var="ERROR_INVALIDPHONE"/>
            <fmt:message bundle="${storeText}" key="AB_SELECT_ADDRTYPE" var="AB_SELECT_ADDRTYPE"/>
            <fmt:message bundle="${storeText}" key="ERROR_DEFAULTADDRESS" var="ERROR_DEFAULTADDRESS"/>
            <fmt:message bundle="${storeText}" key="ERROR_INVALIDEMAILFORMAT" var="ERROR_INVALIDEMAILFORMAT"/>
            MessageHelper.setMessage("ERROR_RecipientTooLong", <wcf:json object="${ERROR_RecipientTooLong}"/>);
            MessageHelper.setMessage("ERROR_FirstNameTooLong", <wcf:json object="${ERROR_FirstNameTooLong}"/>);
            MessageHelper.setMessage("ERROR_LastNameTooLong", <wcf:json object="${ERROR_LastNameTooLong}"/>);
            MessageHelper.setMessage("ERROR_MiddleNameTooLong", <wcf:json object="${ERROR_MiddleNameTooLong}"/>);
            MessageHelper.setMessage("ERROR_AddressTooLong", <wcf:json object="${ERROR_AddressTooLong}"/>);
            MessageHelper.setMessage("ERROR_CityTooLong", <wcf:json object="${ERROR_CityTooLong}"/>);
            MessageHelper.setMessage("ERROR_StateTooLong", <wcf:json object="${ERROR_StateTooLong}"/>);
            MessageHelper.setMessage("ERROR_CountryTooLong", <wcf:json object="${ERROR_CountryTooLong}"/>);
            MessageHelper.setMessage("ERROR_ZipCodeTooLong", <wcf:json object="${ERROR_ZipCodeTooLong}"/>);
            MessageHelper.setMessage("ERROR_PhoneTooLong", <wcf:json object="${ERROR_PhoneTooLong}"/>);
            MessageHelper.setMessage("ERROR_RecipientEmpty", <wcf:json object="${ERROR_RecipientEmpty}"/>);
            /*Although for English, firstname is not mandatory. But it is mandatory for other languages.*/
            MessageHelper.setMessage("ERROR_FirstNameEmpty", <wcf:json object="${ERROR_FirstNameEmpty}"/>);
            MessageHelper.setMessage("ERROR_LastNameEmpty", <wcf:json object="${ERROR_LastNameEmpty}"/>);
            MessageHelper.setMessage("ERROR_MiddleNameEmpty", <wcf:json object="${ERROR_MiddleNameEmpty}"/>);
            MessageHelper.setMessage("ERROR_AddressEmpty", <wcf:json object="${ERROR_AddressEmpty}"/>);
            MessageHelper.setMessage("ERROR_CityEmpty", <wcf:json object="${ERROR_CityEmpty}"/>);
            MessageHelper.setMessage("ERROR_StateEmpty", <wcf:json object="${ERROR_StateEmpty}"/>);
            MessageHelper.setMessage("ERROR_CountryEmpty", <wcf:json object="${ERROR_CountryEmpty}"/>);
            MessageHelper.setMessage("ERROR_ZipCodeEmpty", <wcf:json object="${ERROR_ZipCodeEmpty}"/>);
            MessageHelper.setMessage("ERROR_PhonenumberEmpty", <wcf:json object="${ERROR_PhonenumberEmpty}"/>);
            MessageHelper.setMessage("ERROR_EmailEmpty", <wcf:json object="${ERROR_EmailEmpty}"/>);
            MessageHelper.setMessage("PWDREENTER_DO_NOT_MATCH", <wcf:json object="${PWDREENTER_DO_NOT_MATCH}"/>);
            MessageHelper.setMessage("REQUIRED_FIELD_ENTER", <wcf:json object="${REQUIRED_FIELD_ENTER}"/>);
            MessageHelper.setMessage("AB_UPDATE_SUCCESS", <wcf:json object="${AB_UPDATE_SUCCESS}"/>);
            MessageHelper.setMessage("AB_DELETE_SUCCESS", <wcf:json object="${AB_DELETE_SUCCESS}"/>);
            MessageHelper.setMessage("AB_ADDNEW_SUCCESS", <wcf:json object="${AB_ADDNEW_SUCCESS}"/>);
            MessageHelper.setMessage("WISHLIST_INVALIDEMAILFORMAT", <wcf:json object="${WISHLIST_INVALIDEMAILFORMAT}"/>);
            MessageHelper.setMessage("ERROR_INVALIDPHONE", <wcf:json object="${ERROR_INVALIDPHONE}"/>);
            MessageHelper.setMessage("AB_SELECT_ADDRTYPE", <wcf:json object="${AB_SELECT_ADDRTYPE}"/>);
            MessageHelper.setMessage("ERROR_DEFAULTADDRESS", <wcf:json object="${ERROR_DEFAULTADDRESS}"/>);
            MessageHelper.setMessage("ERROR_INVALIDEMAILFORMAT", <wcf:json object="${ERROR_INVALIDEMAILFORMAT}"/>);
            AddressBookFormJS.setCommonParameters("<c:out value='${langId}'/>", "<c:out value='${WCParam.storeId}'/>","<c:out value='${WCParam.catalogId}'/>");
        });
    </script>
 
	<%@ include file="../../../Common/JSPFExtToInclude.jspf"%> </body>
</html>

<!-- END OrderShippingBillingDetails.jsp -->
