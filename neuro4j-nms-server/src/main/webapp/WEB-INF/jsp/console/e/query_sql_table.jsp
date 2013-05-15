
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="vlh" uri="http://valuelist.sourceforge.net/tags-valuelist" %>

<%@ page import="net.mlw.vlh.ValueList"%>
<%@ page import="net.mlw.vlh.ValueListInfo"%>

            <!-- start table with topics --> 
            <% int currentRowIdx = 0; %>    
            <vlh:root value="e_list" url="?" includeParameters="*" id="_el">
                <vlh:retrieve name="coinReader" />
            <table class="hp l r t" width="100%">
                <tr>
                    <td width="90%" align="center">&nbsp;</td>
                    <td align="right"><c:if
                            test="${e_list.valueListInfo.totalNumberOfPages > 1}">
                            <vlh:paging pages="10">
                                <c:out value="${page}" />
                            </vlh:paging>
                        </c:if>
                        </td>
                </tr>
            </table>
            <table width="100%" cellspacing="0" cellpadding="0" border="0" class="l r t b">
                <%
                Set<String> headers = (Set<String>) request.getAttribute("header_columns");
                %>
                <tr>
                   <%
                   for (String key : headers)
                   {
                       String keyShort = StringUtils.getShortStr(key, 110);
                       %>
                     <td valign="top" align="center" class="pd3 r vhl_tr<%= (currentRowIdx % 2) %>"> 
                          &nbsp;<span title="<%= key %>"><%= keyShort %></span>
                     </td>
                       <%
                   }
                   %>
                </tr>
    
                <c:if test="${e_list.valueListInfo.totalNumberOfEntries > 0}">
                   <vlh:row bean="e">
                         <%
                         Entity en = (Entity) pageContext.getAttribute("e");
                         for (String key : headers)
                         {
                             String v = en.getProperty(key);
                             if (null == v)
                            	  v = "";
                             String vShort = StringUtils.getShortStr(v, 110);
                             %>
		                        <td valign="top" align="center" class="pd3 r vhl_tr<%= (currentRowIdx % 2) %>"> 
		                             &nbsp;<span title="<%= v %>"><%= vShort %></span>
		                        </td>
                             <%
                         }
                         %>

                       <% currentRowIdx++;%>
                   </vlh:row>
                </c:if>
               
            </table>
           <c:if test="${e_list.valueListInfo.totalNumberOfPages > 1}">     
                <table width="100%" class="hp l r b">
                            <tr class="vhl_tr1" align="right">
                            <td width="90%">&nbsp;</td>
                                <td align="right">
                                
                                        <vlh:paging pages="10">
                                            <c:out value="${page}" />
                                        </vlh:paging>
                                    
                                </td>
                            </tr>
           </table>
           </c:if>
            </vlh:root>
            <!-- end table with topics -->       







