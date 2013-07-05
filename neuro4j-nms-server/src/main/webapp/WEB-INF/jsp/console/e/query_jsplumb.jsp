
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="vlh" uri="http://valuelist.sourceforge.net/tags-valuelist" %>

<%@page import="java.util.*"%>

<%@page import="org.neuro4j.web.console.controller.view.*"%>

<div id="container" class="t" >

<%
Set<ViewComponent> componentsList = (Set<ViewComponent>)request.getAttribute("components_list");

%>



			<div style="position:absolute">
			 <div >  
			  <% 
			  int top = 100;
			  for ( ViewComponent e: componentsList)  { 			      
				  
			  %>
			       <div class="w <%=e.getCss() %>" style="top:<%=e.getTop()%>px;left:<%=e.getLeft()%>px;" id="<%= e.getId() %>" ><a href="query?vt=${vt}&q=${q}&startNodeId=<%= e.getId() %>"><%= e.getName() %></a><div class="ep"></div></div>
			      
			  <% } %>
			 </div>
			</div>






         <link type="text/css" rel="stylesheet" href="../console/css/ran4j/jsPlumbDemo.css"/>
        <link type="text/css" rel="stylesheet" href="../console/css/ran4j/ran4j.css"/>
        
        <script language="javascript" type="text/javascript" src="../console/js/ran4j/jquery_1.7.1.js"></script>
        <script language="javascript" type="text/javascript" src="../console/js/ran4j/jquery-ui_1.8.13.js"></script>
        <script language="javascript" type="text/javascript" src="../console/js/ran4j/jquery.jsPlumb-1.3.9-all.js"></script>
        <script language="javascript" type="text/javascript" src="../console/js/ran4j/ran4jInit.js"></script>

     
<script type="text/javascript">

;(function() {
          jsPlumbDemo.initEndpoints = function(nextColour) {
               $(".ep").each(function(i,e) {
                               var p = $(e).parent();
                             var s =  jsPlumb.makeSource($(e), {
                                       parent:p,
                                       //anchor:"BottomCenter",
                                       anchor:"Continuous",
                                       connector:[ "StateMachine", { curviness:20 } ],
                                       maxConnections:-1
                                       
                               });

                       });
 
               <% 
               int c = 0;
               for ( ViewComponent e: componentsList)  {

            	   Set<ViewRelation> connections = e.getConnectionsTo();
            	   for ( ViewRelation connection: connections) {
            		   
               %>
			               var c =   jsPlumb.connect({
			                   source:"<%= connection.getSourceId()%>",
			                   target:"<%= connection.getTargetId()%>",
			                   anchor:"TopCenter",
			                   paintStyle :{ strokeStyle: "grey" }
			
			   					}
			               );

               <% }} %>

           };
       })();


</script>                        

</div>


       