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
	        <td colspan="3" width="100%" align="center" class="b"><b>Storages</b></td>
	    </tr>
	    <%
	    for (String storageName : nmsConfig.getStorageNames())
	    {
	    	NeuroStorage storage = nmsConfig.getStorage(storageName);
	    %>
	    
	    <tr>
            <td align="center" class="hp"><a href="query?vt=graph&storage=<%= storageName %>"><b><%= storageName %></b> - <%= storage.getClass().getName() %></a></td>

            <td align="right" width="30px" class="hp">
	            <form action="query" method="post">
	                <input type="hidden" name="storage" value="<%= storageName %>"> 
	                <input type="hidden" name="vt" value="graph"> 
	                <input
	                    class="m-button m-button-ur"
	                    style="padding-left: 10px; padding-top: 5px; padding-bottom: 5px; padding-right: 10px;"
	                    type="submit" name="search" value="Query">
	            </form>            
            </td>            
            <td align="right" width="30px" class="hp">
                <form action="import" method="get">
                    <input type="hidden" name="storage" value="<%= storageName %>"> 
                    <input
                        class="m-button m-button-ur"
                    <%
                    Properties roProps = NMSServerConfig.loadProperties(nmsConfig.getProperty(NMSServerConfig.STORAGE_PREFIX + storageName));
                    if (null != roProps.getProperty("n4j.storage.read_only") 
                            && "true".equalsIgnoreCase(roProps.getProperty("n4j.storage.read_only")))
                    {
                        %>
                        disabled="disabled"
                    <%
                    }
                    %>
                        style="padding-left: 10px; padding-top: 5px; padding-bottom: 5px; padding-right: 10px;"
                        type="submit" name="search" value="Import">
                </form>            
            </td>            
	    </tr>
	    <tr>
	        <td colspan="3" align="center">
              <table border="0" cellspacing="0" cellpadding="0" class="t" width="100%" >
		        <tr>
		            <td colspan="2" align="center" class="hp b">Storage properties</td>
		        </tr>
			        <%
                    String storagePropFileName = nmsConfig.getProperty(NMSServerConfig.STORAGE_PREFIX + storageName);
                    Properties props = NMSServerConfig.loadProperties(storagePropFileName);

                    for (Object keyObj : props.keySet())
			        {
                        String key = (String) keyObj;
                        String value = props.getProperty(key);
			        %>
                        <tr>
                            <td align="left" class="b"><%=key %></td>
                            <td align="left" class="b"><%=value %></td>
                        </tr>
			        <%
			        }
			        %>
              </table>
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
