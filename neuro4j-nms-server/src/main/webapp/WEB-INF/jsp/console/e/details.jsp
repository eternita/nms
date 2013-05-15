<%@ include file="/WEB-INF/jsp/console/inc/console_header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String listTabClass = "";
    String graphTabClass = "";
    if ("graph".equals(request.getAttribute("selected_tab")))
    {
        graphTabClass = "hp";
    } else {
        listTabClass = "hp";
    }
    
%>

   <table border="0" cellspacing="0" cellpadding="0"  class="" width="100%">
                <tr>
            <td width="100px" class="l r t <%= listTabClass%>" align="center">
                <c:choose>
                 <c:when test="${null == selected_tab || '' == selected_tab}">
                    Table
                 </c:when>
                 <c:otherwise>
                    <a href="entity-details?storage=${storage}&eid=${eid}">Table</a>
                 </c:otherwise>
                </c:choose>
            </td>
            
            <td width="1px">&nbsp;
            </td>
            
            <td width="100px" class="l r t <%= graphTabClass%>" align="center">
                <c:choose>
                 <c:when test="${'graph' != selected_tab}">
                    <a href="entity-details?storage=${storage}&eid=${eid}&vt=graph">Graph </a>
                 </c:when>
                 <c:otherwise>
                    Graph
                 </c:otherwise>
                </c:choose>
            </td>

<% 
Entity entity4tab = (Entity) request.getAttribute("entity");
if (null != entity4tab.getProperty("SWF_BLOCK_CLASS")) { %>

            <td width="1px">&nbsp;
            </td>

            <td width="100px" class="l r t" align="center">
                <a href='init-action?eid=<%= entity4tab.getUuid() %>'>Run it!</a>
            </td>


<% } %>

            <td width="1px">&nbsp;
            </td>

            <td align="left" class=""> &nbsp;
                <c:choose>
                 <c:when test="${'graph' == selected_tab}">
						Expand level:
						<a href="entity-details?storage=${storage}&eid=${entity.uuid}&vt=graph&view_depth=1">1</a>&nbsp;
						<a href="entity-details?storage=${storage}&eid=${entity.uuid}&vt=graph&view_depth=2">2</a>&nbsp;
						<a href="entity-details?storage=${storage}&eid=${entity.uuid}&vt=graph&view_depth=3">3</a>&nbsp;
						<a href="entity-details?storage=${storage}&eid=${entity.uuid}&vt=graph&view_depth=4">4</a>&nbsp;
                 </c:when>
                 <c:otherwise>
                 </c:otherwise>
                </c:choose>
            </td>
                </tr>
   </table>

    <c:choose>
     <c:when test="${'graph' == view}">
         <%@ include file="/WEB-INF/jsp/console/e/details_graph.jsp" %>
     </c:when>
     <c:otherwise>
        <%@ include file="/WEB-INF/jsp/console/e/details_list.jsp" %>
     </c:otherwise>
    </c:choose>
                


<%@ include file="/WEB-INF/jsp/console/inc/footer.jsp" %>
