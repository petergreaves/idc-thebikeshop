<%--
 =================================================================
  Licensed Materials - Property of IBM

  WebSphere Commerce

  (C) Copyright IBM Corp. 2009, 2012 All Rights Reserved.

  US Government Users Restricted Rights - Use, duplication or
  disclosure restricted by GSA ADP Schedule Contract with
  IBM Corp.
 =================================================================
--%>

<!--  
//********************************************************************
//*-------------------------------------------------------------------
//* The sample contained herein is provided to you "AS IS".
//*
//* It is furnished by IBM as a simple example and has not been thoroughly tested
//* under all conditions.  IBM, therefore, cannot guarantee its reliability, 
//* serviceability or functionality.  
//*
//* This sample may include the names of individuals, companies, brands and products 
//* in order to illustrate concepts as completely as possible.  All of these names
//* are fictitious and any similarity to the names and addresses used by actual persons 
//* or business enterprises is entirely coincidental.
//*--------------------------------------------------------------------------------------
//*
-->

<%@ page language="java" %>
<%@ page import="javax.servlet.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.ibm.commerce.server.ECConstants" %>
<%@ page import="com.ibm.commerce.foundation.common.util.logging.LoggingHelper"%>
<%@ page import="com.ibm.commerce.foundation.logging.ExtendedErrorInfo"%>
<%@ page import="java.util.logging.Logger"%>
<%@ page import="java.util.logging.Level"%>
<%@ page isErrorPage="true" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://commerce.ibm.com/foundation-fep/stores" prefix="wcst" %>

<wcst:setBundle  basename="GenericApplicationError" var="myResourceBundle"  location="store"/>
<wcst:alias name="JSPHelper" var="jHelper">
	<wcf:param name="parameterSource" value="javax.servlet.jsp.jspRequest"/>
</wcst:alias>
<wcst:mapper source="jHelper" method="rollbackTransaction" var="rollbackTransaction" />
<c:set var="rollbackResult" value="${rollbackTransaction['false']}"/>
<%
if (exception != null && exception.getCause() != null) {
	if (exception.getCause().toString().indexOf("categories")!=-1) {
%>
		<fmt:message var="displayMessage" key="genericErrSOLRNotSetupText" bundle="${myResourceBundle}" scope="request"/>
<%
	}
}
%>

<% response.setContentType("text/html;charset=UTF-8"); %>
<% response.setHeader("Pragma", "No-cache");           %>
<% response.setDateHeader("Expires", 0);               %>
<% response.setHeader("Cache-Control", "no-cache");    %>

<c:if test="${empty storeId }">
	<c:set var="storeId" value="${WCParam.storeId}"/>
</c:if>
<c:if test="${empty catalogId }">
	<c:set var="catalogId" value="${WCParam.catalogId}"/>
</c:if>

<c:if test="${empty catalogId}">
	<wcf:rest var="storeDB" url="store/{storeId}/online_store" cached="true">
		<wcf:var name="storeId" value="${storeId}" encode="true"/>
		<wcf:param name="profileName" value="IBM_Admin_Summary" encode="true"/>
	</wcf:rest>
	<c:set var="catalogId" value="${storeDB.resultList[0].defaultCatalog[0].catalogIdentifier.uniqueID}"/>
</c:if>

<wcst:aliasBean id="errorBean" name="ErrorDataBean"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html lang="en">
<head>
	<title>
		<fmt:message key="title" bundle="${myResourceBundle}" />
	</title>
</head>
<body>

<br/>
<c:choose>
	<c:when test="${errorBean.messageKey != null}">
		<c:choose>
			<c:when test="${errorBean.messageKey eq '_ERR_BAD_STORE_STATE' || errorBean.messageKey eq '_ERR_VIEW_NAME_NOT_FOUND'}">
				<span class="warning"><fmt:message bundle="${myResourceBundle}" key="GENERICERR_TEXT4" /></span>
			</c:when>
			<c:otherwise>
			<h1 role="main"><fmt:message key="genericErrMainText" bundle="${myResourceBundle}" /></h1>
			</c:otherwise>
		</c:choose>
	</c:when>
</c:choose>
<br/>
<br/>

</body>
</html>

<%
ExtendedErrorInfo.getInstance().logInfo();
final Logger LOGGER = LoggingHelper.getLogger(LoggingHelper.class);
if (exception != null){
	LOGGER.logp(Level.SEVERE, this.getServletName(), "-", exception.getLocalizedMessage(), exception);
}

String displayMessage = null;
displayMessage = (String)request.getAttribute("displayMessage");
if (displayMessage != null) {
	LOGGER.logp(Level.SEVERE, this.getServletName(), "-", displayMessage);
}
%>
