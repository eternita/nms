<%@ include file="/WEB-INF/jsp/console/inc/console_header.jsp" %>

<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<form:form modelAttribute="uploadItem" name="frm" method="post" enctype="multipart/form-data" >

<fieldset><legend>Upload File</legend>

<table>

<tr>

<td><form:label for="fileData" path="fileData">File</form:label><br />

</td>

<td><form:input path="fileData" id="image" type="file" /></td>

</tr>

<tr>

<td><br />

</td>

<td><input type="submit" value="Upload" /></td>

</tr>

</table>

</fieldset>

</form:form>

<%@ include file="/WEB-INF/jsp/console/inc/footer.jsp" %>


