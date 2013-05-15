<%@ page isErrorPage="true"%>
<%@ include file="/WEB-INF/jsp/console/inc/console_header.jsp" %>
<%@ page import="java.io.*"%>

<br/>
<h3 align="center">Error page</h3>
<br/>



<br/>
<div align="center">
We do work every day to make it better :)
<br/>

</div>


<br/>
<br/>
<div align="center">

 <% 
     String errorDump = "";
     if (null != exception)
     {
         try
         {
             ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
             exception.printStackTrace(new PrintStream(baos));
             errorDump = new String(baos.toByteArray());
             org.slf4j.LoggerFactory.getLogger(this.getClass()).error("error.jsp", exception);
         } catch (Exception ex) {
             try
             {
              org.slf4j.LoggerFactory.getLogger(this.getClass()).error("error.jsp", ex);
             } catch (Exception ex2) {
                 ex2.printStackTrace();
             }
         }
     }

 %>
 
<div align="left">
<%--
 --%>
 <pre width="100%" style="width: 100%;"><%= errorDump %></pre>
</div>
</div>
 
<%@ include file="/WEB-INF/jsp/console/inc/footer.jsp" %>

