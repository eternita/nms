<%@ include file="/WEB-INF/jsp/console/inc/console_header.jsp" %>
<%@ page import="org.neuro4j.core.Entity" %>
<%@ page import="org.neuro4j.web.console.controller.EntitiesController" %>
<%@ page import="java.util.List" %>

New relation
<br/>
        <%
        List<Entity> eList = EntitiesController.getEntityList(request);
        %>

 <form id="frm1" action="add-relation" method="post">
    Relation Name:<input type="text" name="rname" value=""> <br/>
    Entity Name1:
    <select name="ename1">
        <option id="" value=""></option> 
        <% for (Entity e : eList)
        { %> 
        <option id="<%= e.getUuid() %>" value="<%= e.getUuid() %>"><%= e.getName() %></option> 
        <% } %>
     </select><br/>
    Entity Name2:
    <select name="ename2">
        <option id="" value=""></option> 
        <% for (Entity e : eList)
        { %> 
        <option id="<%= e.getUuid() %>" value="<%= e.getUuid() %>"><%= e.getName() %></option> 
        <% } %>
     </select><br/>
    Entity Name3:
    <select name="ename3">
        <option id="" value=""></option> 
        <% for (Entity e : eList)
        { %> 
        <option id="<%= e.getUuid() %>" value="<%= e.getUuid() %>"><%= e.getName() %></option> 
        <% } %>
     </select><br/>
    Entity Name4:
    <select name="ename4">
        <option id="" value=""></option> 
        <% for (Entity e : eList)
        { %> 
        <option id="<%= e.getUuid() %>" value="<%= e.getUuid() %>"><%= e.getName() %></option> 
        <% } %>
     </select><br/>
    
    
        <input class="" type="submit" name="submit" value="Create">
 </form>



<%@ include file="/WEB-INF/jsp/console/inc/footer.jsp" %>
