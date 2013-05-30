<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="org.neuro4j.web.console.utils.RequestUtils"%>


<!DOCTYPE html>
<html lang="en">
<head>
    <c:choose>
      <c:when test="${null != html_title}">
          <title><c:out value="${html_title}" /></title>
      </c:when>
      <c:otherwise>
          <title>NEURO 4J - Network Management System Console</title>
      </c:otherwise>
    </c:choose>
    <c:choose>
      <c:when test="${use_browser_cache != null}">
        <meta http-equiv="Cache-Control" content="max-age=86400" />
      </c:when>
      <c:otherwise>
        <meta http-equiv="Expires" content="-1" />
        <meta http-equiv="Pragma" content="no-cache" />
        <meta http-equiv="Cache-Control" content="no-cache" />
      </c:otherwise>
    </c:choose>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	
	<!-- CSS Files -->
	<link type="text/css" href="console/css/console.css" rel="stylesheet" />
	<link type="text/css" href="console/css/from_chn.css" rel="stylesheet" />
	
	<link type="text/css" href="console/css/960.css" rel="stylesheet" />
	<link type="text/css" href="console/css/style.css" rel="stylesheet" />
	<link type="text/css" href="console/css/screen.css" rel="stylesheet" />

	<script language="javascript" type="text/javascript" src="console/js/jquery-1.7.2.js"></script>
    <script language="javascript" type="text/javascript" src="console/js/web-elements.js"></script>

    <c:if test="${include_accordion_js != null}">
        <script type="text/javascript" src="console/js/accordion/accordion.js"></script> 
    </c:if>

  <c:choose>
    <c:when test="${query_view != null || entity_view != null}">
		<!--[if IE]><script language="javascript" type="text/javascript" src="/js/excanvas.js"></script><![endif]-->
		
		<!-- JIT Library File -->
		<script language="javascript" type="text/javascript" src="console/js/jit.js"></script>

		
		<!-- Console custom code -->
		<script language="javascript" type="text/javascript" src="console/js/console.js"></script>
    </c:when>
    <c:otherwise>

    </c:otherwise>
  </c:choose>



</head>


  <c:choose>
    <c:when test="${query_view != null && null != entity}">
     <body onload="initView('${storage}', '<%= RequestUtils.escapeJSString((String)request.getAttribute("q")) %>');loadEntity('${storage}', '<%= RequestUtils.escapeJSString((String)request.getAttribute("q")) %>', '${view_depth}', '${entity.uuid}');">
    </c:when>
    <c:when test="${query_view != null && null == entity && null != q}">
     <body onload="initView('${storage}', '<%= RequestUtils.escapeJSString((String)request.getAttribute("q")) %>');loadEntity('${storage}', '<%= RequestUtils.escapeJSString((String)request.getAttribute("q")) %>', '${view_depth}');">
    </c:when>

    <c:when test="${entity_view != null}">
     <body onload="initView('${storage}', '<%= RequestUtils.escapeJSString((String)request.getAttribute("q")) %>');loadEntity('${storage}', '<%= RequestUtils.escapeJSString((String)request.getAttribute("q")) %>', '${view_depth}', '${entity.uuid}');">
    </c:when>
    <c:otherwise>
     <body>
    </c:otherwise>
  </c:choose>
  
    <div id="xHeader">
        <div>
            <a id="uLogo" target="_blank" href="http://www.neuro4j.org/"> NEURO 4J</a>
            <a id="uLogo" href="settings"> - Network Management System Console</a>
            <nav> 
                <ul>
                    
                    <li><a href="settings">Settings</a></li>
                    
                    <c:if test="${null != storage}">
                        <li><a href="storage-settings?name=${storage}">${storage}</a></li>
                        <li><a href="query?vt=graph&storage=${storage}">Query</a></li>
                    </c:if>
                </ul>
            </nav>
            
        </div>
    </div>
  
  <c:choose>
    <c:when test="${page_full_screen != null}">
      <div id="baseDivFullScreen" >
    </c:when>
    <c:otherwise>
      <div id="baseDiv" >
    </c:otherwise>
  </c:choose>
  
<br/>
