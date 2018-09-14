<!DOCTYPE HTML>

<%--
 =================================================================
  Licensed Materials - Property of IBM

  WebSphere Commerce

  (C) Copyright IBM Corp. 2008, 2015 All Rights Reserved.

  US Government Users Restricted Rights - Use, duplication or
  disclosure restricted by GSA ADP Schedule Contract with
  IBM Corp.
 =================================================================
--%>
<!-- BEGIN ExtProfileForm.jsp -->

<%--
  *****
  * This JSP will display the profile form with the following fields:
  *  - Marital status
  *  - Number of people in household
  *  - Number of children
  *****
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://commerce.ibm.com/base" prefix="wcbase" %>
<%@ taglib uri="flow.tld" prefix="flow" %>
<%@ include file="../../Common/EnvironmentSetup.jspf" %>
<%@ include file="../../Common/nocache.jspf" %>
<%-- ErrorMessageSetup.jspf is used to retrieve an appropriate error message when there is an error --%>
<%@ include file="../../include/ErrorMessageSetup.jspf" %>
<%@ taglib uri="http://commerce.ibm.com/foundation" prefix="wcf" %>
<%@ taglib uri="http://commerce.ibm.com/coremetrics"  prefix="cm" %>

<c:set var="pageCategory" value="MyAccount" scope="request"/>

<wcf:url var="LogonForm" value="AjaxLogonForm">
	<wcf:param name="storeId" value="${WCParam.storeId}" />
	<wcf:param name="catalogId" value="${WCParam.catalogId}" />
	<wcf:param name="myAcctMain" value="1" />
</wcf:url>

<c:choose>
	<c:when test='${!empty WCParam.changePasswordPage && WCParam.changePasswordPage == true }'>
		<c:import url = "${env_jspStoreDir}/Widgets/Header/DepartmentDropDown.jsp" />
	</c:when>
	<c:when test='${!empty WCParam.miniCartContent && WCParam.miniCartContent == true }'>				
		<c:import url = "${env_jspStoreDir}Widgets/MiniShopCartDisplay/MiniShopCartDisplay.jsp" >						
			<c:param name="page_view" value="dropdown"/>
		</c:import>
	</c:when>
	<c:otherwise>

<html xmlns="http://www.w3.org/1999/xhtml" lang="${shortLocale}" xml:lang="${shortLocale}">
<head>
	<%@ include file="../../Common/CommonCSSToInclude.jspf"%>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title><fmt:message bundle="${storeText}" key="EXT_PROFILE_TITLE"/></title>
	<%@ include file="../../Common/CommonJSToInclude.jspf"%>

</head>
<body>
<%@ include file="../../Common/CommonJSPFToInclude.jspf"%>

<!-- Page Start -->
<div id="page">
	<!-- Header Widget -->
	<div class="header_wrapper_position" id="headerWidget">
		<%out.flush();%>
			<c:import url = "${env_jspStoreDir}/Widgets/Header/Header.jsp" />
		<%out.flush();%>
	</div>
	
	<!-- Main Content Start -->
	<div id="contentWrapper">
		<div id="content" role="main">		
			<div class="row margin-true">
				<div class="col12">				
					<%out.flush();%>
						<wcpgl:widgetImport useIBMContextInSeparatedEnv="${isStoreServer}" url="${env_siteWidgetsDir}com.ibm.commerce.store.widgets.BreadcrumbTrail/BreadcrumbTrail.jsp">  														
							<wcpgl:param name="pageGroup" value="Content"/>
						</wcpgl:widgetImport>
					<%out.flush();%>					
				</div>
			</div>
			<div class="rowContainer" id="container_MyAccountDisplayB2B">
				<div class="row margin-true">					
					<div class="col4 acol12 ccol3">
						<%out.flush();%>
							<c:import url="/Widgets_S0126i/com.ibm.commerce.store.widgets.MyAccountNavigation/MyAccountNavigation.jsp"/>
						<%out.flush();%>		
					</div>
					<div class="col8 acol12 ccol9 right">	
						<div class="content_wrapper_position" role="main">
		<div class="content_wrapper">
			<div class="content_left_shadow">
				<div class="content_right_shadow">
					<div class="main_content">
						<div class="container_full_width" id="content_wrapper_border">
							<!-- Content Start -->
							<div id="box">
								<div class="sign_in_registration" id="WC_ExtProfileForm_div_1">
									<div class="title" id="WC_ExtProfileForm_div_2">
										<h1><fmt:message bundle="${storeText}" key="EXT_PROFILE_TITLE"/></h1>
									</div>

									<div class="forgot_password_container" id="WC_ExtProfileForm_div_3">
										<div class="forgot_password_content" id="WC_ExtProfileForm_div_6">
											 <div class="align" id="WC_ExtProfileForm_div_7">
												<c:if test="${!empty errorMessage}">
													<span class="error_msg" id="error_msg"><c:out value="${errorMessage}"/></span>
													<c:set var="aria_invalid" value="aria-invalid=true"/>
													<script type="text/javascript">
														$(document).ready(function() { 
															increaseHeight("WC_ExtProfileForm_div_7", 20);
														});
													</script>
												</c:if>

												<form name="Logon" method="post" action="RESTUserRegistrationUpdate" id="Logon">
													<input type="hidden" name="storeId" value='<c:out value="${WCParam.storeId}" />' id="WC_ExtProfileForm_FormInput_storeId_In_Logon_1"/>
													<input type="hidden" name="catalogId" value='<c:out value="${WCParam.catalogId}" />' id="WC_ExtProfileForm_FormInput_catalogId_In_Logon_1"/>
													<input type="hidden" name="langId" value='<c:out value="${langId}" />' id="WC_ExtProfileForm_FormInput_langId_In_Logon_1"/>
													<%--<input type="hidden" name="logonId" value='<c:out value="${WCParam.logonId}" />' id="WC_ExtProfileForm_FormInput_logonId_In_Logon_1"/>--%>
													<input type="hidden" name="errorViewName" value="ExtProfileFormView" id="WC_ExtProfileForm_FormInput_Error_In_Logon_1"/>
													<input type="hidden" name="URL" value="<c:out value="${LogonForm}"/>" id="WC_ExtProfileForm_FormInput_URL_In_Logon_1"/>
													<input type="hidden" name="myAcctMain" value="1" id="WC_ExtProfileForm_FormInput_myAcctMain_In_Logon_1"/>
													<input type="hidden" name="authToken" value="${authToken}"  id="WC_ExtProfileForm_FormInput_authToken_In_Logon_1"/>
													<input type="hidden" name="registerType" value="RegisteredPerson" id="WC_ExtProfileForm_FormInput_registerType_In_Logon_1"/>
													<input type="hidden" name="editRegistration" value="Y" id="WC_ExtProfileForm_FormInput_editRegistration_In_Logon_1"/>

													<div id="WC_ExtProfileForm_div_8">
															<label for="WC_ExtProfileForm_FormInput_maritalStatus_In_Logon_1">
																<fmt:message bundle="${storeText}" key="MARITAL_STATUS"/>
															</label>
													</div>
													<div id="WC_ExtProfileForm_div_9">
															<input <c:out value="${aria_invalid}"/> aria-required="true" aria-describedby="error_msg" size="25" maxlength="50" name="maritalStatus" autocomplete="off" value="" 
															id="WC_ExtProfileForm_FormInput_maritalStatus_In_Logon_1"/>
													</div>
													<div id="WC_ExtProfileForm_div_10">
															<label for="WC_ExtProfileForm_FormInput_household_In_Logon_1">
																<fmt:message bundle="${storeText}" key="HOUSEHOLD"/>
															</label>
													</div>
													<div id="WC_ExtProfileForm_div_11">
															<input <c:out value="${aria_invalid}"/> aria-required="true" aria-describedby="error_msg" size="25" maxlength="50" name="household" autocomplete="off" value="" 
															id="WC_ExtProfileForm_FormInput_household_In_Logon_1"/>
													</div>
													<div id="WC_ExtProfileForm_div_12">
															<label for="WC_ExtProfileForm_FormInput_children_In_Logon_1">
																<fmt:message bundle="${storeText}" key="CHILDREN"/>
															</label>
													</div>
													<div id="WC_PasswordUpdateForm_div_13">
															<input <c:out value="${aria_invalid}"/> aria-required="true" aria-describedby="error_msg" size="25" maxlength="50" name="children" autocomplete="off" value="" 
															id="WC_ExtProfileForm_FormInput_children_In_Logon_1"/>
													</div>
													<div class="button_footer_line">
														<a href="#" role="button" class="button_primary" id="WC_ExtProfileForm_Link_1" onclick="javascript:submitSpecifiedForm(document.Logon);return false;">
															<div class="left_border"></div>
															<div class="button_text"><fmt:message bundle="${storeText}" key="SUBMIT"/></div>
															<div class="right_border"></div>
														</a>
													</div>
												</form>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
					</div>
				</div>
			</div>			
		</div>
	</div>	
	<!-- Main Content End -->	
	<!-- Footer Start Start -->
	<div class="footer_wrapper_position">
		<%out.flush();%>
		<c:import url = "${env_jspStoreDir}/Widgets/Footer/Footer.jsp" />
		<%out.flush();%>
	</div>
	<!-- Footer Start End -->
</div>

<flow:ifEnabled feature="Analytics"><cm:pageview/></flow:ifEnabled>
<%@ include file="../../Common/JSPFExtToInclude.jspf"%> </body>
</html>
	</c:otherwise>
</c:choose>
<!-- END ExtProfileForm.jsp -->
