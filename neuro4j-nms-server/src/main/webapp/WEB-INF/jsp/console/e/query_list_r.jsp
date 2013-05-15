
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="vlh" uri="http://valuelist.sourceforge.net/tags-valuelist" %>

<%@ page import="net.mlw.vlh.ValueList"%>
<%@ page import="net.mlw.vlh.ValueListInfo"%>

            <!-- start table with topics --> 
            <% int currentRowIdx = 0; %>    
            <vlh:root value="r_list" url="?" includeParameters="*" id="_el">
                <vlh:retrieve name="coinReader" />
            <table class="hp l r t" width="100%">
                <tr>
                    <td width="90%" align="center">&nbsp;</td>
                    <td align="right"><c:if
                            test="${r_list.valueListInfo.totalNumberOfPages > 1}">
                            <vlh:paging pages="10">
                                <c:out value="${page}" />
                            </vlh:paging>
                        </c:if>
                        </td>
                </tr>
            </table>
            <table width="100%" cellspacing="0" cellpadding="0" border="0" class="l r t b">
			    <tr>
                    <td width="40px" align="center" valign="top" class="pd3 r hp b">
                      <b>#</b>
                    </td>
			        <td align="center" valign="top" class="pd3 hp b">
			          <b>Relations (${r_size})</b>
			        </td>
			    </tr>
    
                <c:if test="${r_list.valueListInfo.totalNumberOfEntries > 0}">
                            <vlh:row bean="r">
                                <td valign="top" align="center" class="pd3 r vhl_tr<%= (currentRowIdx % 2) %>"> 
                                     <%
                                     ValueList vl = (ValueList) request.getAttribute("r_list");
                                     ValueListInfo vli = vl.getValueListInfo();
                                     %>
                                     <%= (vli.getPagingPage() -1)*vli.getPagingNumberPer() + (currentRowIdx + 1) %>
                                     &nbsp;
                                </td>
                                
                                <td class="pd3 r vhl_tr<%= (currentRowIdx % 2) %>" valign="top">
                                  <a href="relation-details?storage=${storage}&uuid=${r.uuid}"><b>${r.name}</b></a>
                                  <br/>
                                    Participants: <c:forEach items="${r.participants}" var="e">
                                        <a href="entity-details?storage=${storage}&eid=${e.uuid}">${e.name}</a> 
                                    </c:forEach>    
                                    <c:if test="${!r.completeLoaded}"> ...</c:if>    
                                    
                                  <br/>
                                  Properties:

                                      <%
                                      Relation r1 = (Relation) pageContext.getAttribute("r");
                                      %>
                                      id : <%= r1.getUuid() %>&nbsp;&nbsp;
                                      <%
                                      for (String key : r1.getPropertyKeys())
                                      {
                                          String v = r1.getProperty(key);
                                          String vShort = StringUtils.getShortStr(v, 110);
                                          %>
                                          <br/><%= key %> : <span title="<%= v %>"><%= vShort %></span>
                                          <%
                                      }
                                      %>
                                      
                                         <br/>


                                </td>
                                
                                <% currentRowIdx++;%>
                            </vlh:row>
                </c:if>
               
            </table>
           <c:if test="${r_list.valueListInfo.totalNumberOfPages > 1}">     
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







