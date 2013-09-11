<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>


<div class="container_12" >
	<div class="grid_12">

		<br/> 
		<c:forEach items="${words}" var="w">
		 <a href="translate.htm?from=${lang}&to=${lang}&q=${w}">${w}</a>&nbsp;
		</c:forEach>
	</div>

	<div class="grid_12">
	  <p/>
	  <p/>
	  <b><a href="translate.htm">&lt;&lt; Back</a></b>
	</div>
</div>


<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>
