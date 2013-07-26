<%@ include file="/WEB-INF/jsp/console/inc/console_header.jsp" %>

<%@ page import="org.neuro4j.core.*"%>
<%@ page import="org.neuro4j.core.rel.*" %>
<%@ page import="java.util.*"%>
<%
Relation rel = (Relation) request.getAttribute("r");
%>


<br/>

<table style="margin-bottom: 3px;" width="100%"  border="0" cellspacing="0" cellpadding="0" class="l r t b">
    <tr>
        <td width="150px" align="left" valign="top" class="b">
          Name
        </td>
        <td align="left" valign="top" class="l b">
          ${r.name}
        </td>
    </tr>
    <tr>
        <td width="150px" align="left" valign="top" class="b">
          ID
        </td>
        <td align="left" valign="top" class="l b">
          ${r.uuid}
        </td>
    </tr>
    <tr>
        <td width="150px" align="left" valign="top" class="r b">
        Properties
        </td>
        <td align="left" valign="top" class="b">
           <table>
             <%
                for (String key : rel.getPropertyKeys()) {
             %> 
                <tr>
                  <td valign="top"><%=key%> : </td>
                  <td valign="top"><%=rel.getProperty(key)%>&nbsp;</td>
                </tr>           
            <%
              }
            %> 


           </table>
        
        </td>
    </tr>
    <tr>
        <td width="150px" align="left" valign="top" class="b">
        Participants
        </td>
        <td align="left" valign="top" class="l b">
                <c:forEach items="${r.participants}" var="rp">
                  <a href="entity-details?storage=${storage}&eid=${rp.uuid}">${rp.name}</a>
                  , 
                </c:forEach>
        </td>
    </tr>

    <tr>
        <td width="150px" align="left" valign="top" class="r">
        Representations
        </td>
        <td align="left" valign="top" >
           <table width="100%">
             <%
                for (Representation rep : rel.getRepresentations()) {
             %> 
                <tr>
                  <td valign="top" class="b">
                    <a href="representation-details?storage=${storage}&id=<%=rep.getUuid()%>">download</a>  
                    <br/>
		             <%
		                for (String repKey : rep.getPropertyKeys()) {
		             %> 
                          <%=repKey%>: <%=rep.getProperty(repKey)%>
	                    <br/>                  
		            <%
		              }
		            %> 
                  </td>
                  
                </tr>           
            <%
              }
            %> 


           </table>
        
        </td>
    </tr>

</table>


<%@ include file="/WEB-INF/jsp/console/inc/footer.jsp" %>
