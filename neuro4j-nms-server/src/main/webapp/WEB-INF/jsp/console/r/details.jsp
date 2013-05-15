<%@ include file="/WEB-INF/jsp/console/inc/console_header.jsp" %>

<%@ page import="org.neuro4j.core.*"%>

Relation details
<br/>
                ${r.name} (
                <c:forEach items="${r.participants}" var="rp">
                  <a href="entity-details?storage=${storage}&eid=${rp.uuid}">${rp.name}</a>
                  , 
                </c:forEach>)




           <table>
             <%
             Relation rel = (Relation) request.getAttribute("r");
             for (String key : rel.getPropertyKeys())
             {
                 %>
                 
                <tr>

                  <td valign="top" width="40%" class="r"><%= key %></td>
                  <td valign="top" class=""><%= rel.getProperty(key) %></td>

                </tr>
                 <%
             }
             %>
           
           </table>


<%@ include file="/WEB-INF/jsp/console/inc/footer.jsp" %>
