<%@ include file="/WEB-INF/jsp/console/inc/console_header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="org.neuro4j.nms.server.*"%>
<%@page import="org.neuro4j.storage.*"%>
<%@page import="java.util.*"%>

        <%
        String storageName = (String) request.getAttribute("storage");
        NeuroStorage storage = (NeuroStorage) request.getAttribute("storageObj");
        %>

<div align="center"><a href="settings">Settings</a> &gt; <b><%= storageName %></b></div>
<br/>
	<table border="0" cellspacing="0" cellpadding="0" class="l r t b" width="100%" >

	    <tr>
            <td align="left" class="hp"><a href="query?vt=graph&storage=<%= storageName %>"><b><%= storageName %></b> - <%= storage.getClass().getName() %></a></td>

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
                    Properties roProps = storage.getConfig();
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
	        </td>
	    </tr>

	</table>

<br/>

              <table border="0" cellspacing="0" cellpadding="0" class="t" width="100%" >
                <tr>
                    <td colspan="2" align="center" class="hp l r b">Configs from storage.properties</td>
                </tr>
                    <%
                    Properties props = storage.getConfig();

                    for (Object keyObj : props.keySet())
                    {
                        String key = (String) keyObj;
                        String value = props.getProperty(key);
                    %>
                        <tr>
                            <td align="left" class="l b"><%=key %></td>
                            <td align="left" class="r b"><%=value %></td>
                        </tr>
                    <%
                    }
                    %>
              </table>

<br/>
    <table border="0" cellspacing="0" cellpadding="0" class="l r t b" width="100%" >

        <tr>
            <td align="center" class="hp b"> Libraries (*.jar extensions with code and flows)
            </td>
        </tr>

    </table>
            
<br/>
<br/>

            
<%@ include file="/WEB-INF/jsp/console/inc/footer.jsp" %>
