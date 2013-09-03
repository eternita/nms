<%@ include file="/WEB-INF/jsp/console/inc/console_header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%@ page import="org.neuro4j.core.*" %>
<%@ page import="org.neuro4j.core.rel.*" %>
<%@ page import="java.util.*"%>

<%
ERBase entity = (ERBase) request.getAttribute("entity");
%>

<table style="margin-bottom: 3px;" width="100%"  border="0" cellspacing="0" cellpadding="0" class="l r t b">
    <tr>
        <td colspan="2" width="100%" align="center" valign="top" class="b pd3 hp">
        <b>Parameters</b>
        </td>
    </tr>
    <tr>
        <td colspan="2" width="100%" align="left" valign="top" class="b pd3">
        Parameter pairs should start from new line. Key - value should be separated by colon ':' <br/>
        Example <br/>
        PARAM_1 : VALUE_1 <br/>
        PARAM_2 : VALUE_2 <br/>
        </td>
    </tr>
    <tr>
        <td colspan="2" width="100%" align="center" valign="top" class="b pd3">
          <form action="run-action">
          <input type="hidden" name="eid" value="${eid}">
          <input type="hidden" name="storage" value="${storage}">
          <textarea rows="" cols="" style="width: 90%; height: 100px;" name="ctx_params">${ctx_params}</textarea>
           <input type="submit" name="Submit" value=" Run it ">
          </form>
        </td>
    </tr>
    <tr>
        <td width="150px" align="left" valign="top" class="b">
          Name
        </td>
        <td align="left" valign="top" class="l b">
          <a href="entity-details?storage=${storage}&eid=${entity.uuid}">${entity.name}</a> 
        </td>
    </tr>
    <tr>
        <td width="150px" align="left" valign="top" class="b">
          ID
        </td>
        <td align="left" valign="top" class="l b">
          ${entity.uuid}
        </td>
    </tr>
    <tr>
        <td width="150px" align="left" valign="top" class="b">
        Relations
        </td>
        <td align="left" valign="top" class="l b">
       
        <table width="100%"  border="0" cellspacing="0" cellpadding="0" class="">
          <tr>
           
          </tr>
            <%
            Map<String, List<ERBase>> groupedRelationMap = entity.groupConnectedByName();
            for (String groupName : groupedRelationMap.keySet())
            {
                %>
                  <tr>
                    <td class="hp"><%= groupName %> (<%= groupedRelationMap.get(groupName).size() %>) </td> 
                  </tr>
                <%
                  for (ERBase rel : groupedRelationMap.get(groupName))
                  {
                %> 
                  <tr>
                    <td class=""><a href="relation-details?storage=${storage}&uuid=<%=rel.getUuid()%>"><%=rel.getName()%></a> 
                        (<%
                          for (ERBase rp : rel.getConnected())
                          {
                        %> 
                          <a href="entity-details?storage=${storage}&eid=<%=rp.getUuid()%>"><%=rp.getName()%></a> 
                          &nbsp; 
                        <%
                          }
                        %>) 
                        [<%
                         	for (String key : rel.getPropertyKeys())
                                                   {
                         %> 
                          <%=key%>: <%=rel.getProperty(key)%>
                          &nbsp; 
                        <%
                          }
                        %>] 
                    </td> 
                  </tr>
                <%
                  } // for (Relation rel : groupedRelationMap.get(groupName))
            }
            %>        
        </table>
        
        </td>
    </tr>

    <tr>
        <td width="150px" align="left" valign="top" class="r">
        Properties
        </td>
        <td align="left" valign="top" >
           <table>
             <%
                for (String key : entity.getPropertyKeys()) {
             %> 
                <tr>
                  <td valign="top"><%=key%> : </td>
                  <td valign="top"><%=entity.getProperty(key)%>&nbsp;</td>
                </tr>           

            <%
              }
            %> 
           </table>
        
        </td>
    </tr>

</table>


<%@ include file="/WEB-INF/jsp/console/inc/footer.jsp" %>
