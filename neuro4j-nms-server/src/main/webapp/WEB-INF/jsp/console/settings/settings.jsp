<%@ include file="/WEB-INF/jsp/console/inc/console_header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="org.neuro4j.nms.server.*"%>
<%@page import="org.neuro4j.storage.*"%>
<%@page import="java.util.*"%>


<div align="center"><b>Settings</b></div>
<br/>
<%
NMSServerConfig nmsConfig = NMSServerConfig.getInstance();

%>
	<table border="0" cellspacing="0" cellpadding="0" class="l r t b" width="100%" >
	    <tr>
	        <td colspan="2" width="100%" align="center" class="hp b"><b>Storages</b></td>
	    </tr>
	    <%
	    for (String storageName : nmsConfig.getStorageNames())
	    {
	    	NeuroStorage storage = nmsConfig.getStorage(storageName);
	    %>
	    
	    <tr>
            <td align="left" class="b"><a href="storage-settings?name=<%= storageName %>"><b><%= storageName %></b> &nbsp;&nbsp; (<%= storage.getClass().getName() %>)</a></td>

            <td align="right" width="30px" class="b">
	            <form action="query" method="post">
	                <input type="hidden" name="storage" value="<%= storageName %>"> 
	                <input type="hidden" name="vt" value="graph"> 
	                <input
	                    class="m-button m-button-ur"
	                    style="padding-left: 10px; padding-top: 5px; padding-bottom: 5px; padding-right: 10px;"
	                    type="submit" name="search" value="Query">
	            </form>            
            </td>            
	    </tr>
	    <%
	    }
	    %>
	</table>
            
<br/>
<br/>

    <table border="0" cellspacing="0" cellpadding="0" class="l r t b" width="100%" >
        <tr>
            <td colspan="2" width="100%" align="center" class="b hp"><b>NMS Properties</b></td>
        </tr>
       <%
       for (String key : nmsConfig.getPropertyKeys())
       {
           String value = nmsConfig.getProperty(key);
       %>
           <tr>
               <td align="left" class="b"><%=key %></td>
               <td align="left" class="b"><%=value %></td>
           </tr>
       <%
       }
       %>
    </table>
            
<%@ include file="/WEB-INF/jsp/console/inc/footer.jsp" %>
