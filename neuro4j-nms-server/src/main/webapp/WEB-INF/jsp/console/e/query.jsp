<%@ include file="/WEB-INF/jsp/console/inc/console_header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="vlh" uri="http://valuelist.sourceforge.net/tags-valuelist" %>

<%
	String listTabClass = "";
	String graphTabClass = "";
	if ("graph".equals(request.getAttribute("selected_tab"))) {
		graphTabClass = "hp";
	} else {
		listTabClass = "hp";
	}
%>

<table class="" border="0" cellspacing="0" cellpadding="0"  width="100%">
    <tr>
        <td colspan="7" align="left" class="">
            <form action="query" method="post">
                <input type="hidden" name="storage" value="${storage}"> 
                <input type="hidden" name="vt" value="${vt}"> 
                <textarea rows="1" cols="1" name="q" style="width: 850px;" >${q}</textarea>
                <input
                    class="m-button m-button-ur"
                    style="padding-left: 10px; padding-top: 5px; padding-bottom: 5px; padding-right: 10px;"
                    type="submit" name="search" value="Query">
            </form>
            
            <c:if test="${null != nql_error}">
              <div class="error_message">
              <b>${nql_error}</b>
              </div>
             
            </c:if>
            
        </td>
    </tr>
    <c:if test="${null != q && null == nql_error}">
    <tr>
        <td width="100px" class="l r t <%=listTabClass%>" align="center">
            <form action="query" method="post">
                <input type="hidden" name="storage" value="${storage}"> 
                <input type="hidden" name="q" value="${q}"> 
                <input class="m-button m-button-tab" type="submit" name="list" value="List">
            </form>
        </td>
        <td width="1px">&nbsp;</td>
        </td>
        <td width="100px" class="l r t <%=graphTabClass%>" align="center">
            <form action="query" method="post">
                <input type="hidden" name="vt" value="graph"> 
                <input type="hidden" name="storage" value="${storage}"> 
                <input type="hidden" name="q" value="${q}"> 
                <input class="m-button m-button-tab" type="submit" name="graph" value="Graph">
            </form>
        </td>
        <td width="1px">&nbsp;</td>
        <td width="100px" class="l r t" align="center">
            <form action="export" method="post">
                <input type="hidden" name="q" value="${q}"> 
                <input type="hidden" name="storage" value="${storage}"> 
                <input class="m-button m-button-tab" type="submit" name="export" value="Export">
            </form>
        </td>
        <td width="1px">&nbsp;</td>
        <td >&nbsp;
        
			<c:if test="${null != q}">
			 Queried ${e_size} entities, ${r_size} relations at ${qtime} ms 
			</c:if>
        
        </td>
    </tr>
    </c:if>
</table>


<c:if test="${null != q && null == nql_error}">

<c:choose>
     <c:when test="${'graph' == view}">
         <%@ include file="/WEB-INF/jsp/console/e/query_graph.jsp" %>
     </c:when>
     <c:when test="${'table' == view}">
         <%@ include file="/WEB-INF/jsp/console/e/query_sql_table.jsp" %>
     </c:when>
     <c:when test="${'jsplumb' == view}">
         <%@ include file="/WEB-INF/jsp/console/e/query_jsplumb.jsp" %>
     </c:when>     
     <c:otherwise>     
            <%@ include file="/WEB-INF/jsp/console/e/query_list_e.jsp" %>
     </c:otherwise>
</c:choose>
                
</c:if>


<%@ include file="/WEB-INF/jsp/console/inc/footer.jsp" %>
