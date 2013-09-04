<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@page import="java.util.*"%>
<%@page import="org.neuro4j.core.*"%>
<%@page import="org.neuro4j.kms.Config"%>

<div class="container_12" >
	<div class="grid_12 l r t b" style="padding: 10px;">
	
		<br/> 

 <%
 Map<ERBase, Set<String>> translations = (Map<ERBase, Set<String>>) request.getAttribute("translations"); 
 Map<String, Set<String>> reverseTranslations = (Map<String, Set<String>>) request.getAttribute("reverseTranslations"); 
 %>                
		
	    <form action="translate.htm" method="get">
            <div class="grid_6">
	              From:
	              <% 
	              String checkedLangFrom = request.getParameter("from");
            	  if (null == checkedLangFrom)
	              	checkedLangFrom = Config.languages.keySet().iterator().next();

	              for(String langFrom : Config.languages.keySet())
	              {
	            	  %>
			              <input type="radio" name="from" value="<%= langFrom%>" 
			              <% if (checkedLangFrom.equals(langFrom))
			              {
			            	out.print("checked=\"checked\"");  
			              }
			              %>
			              ><%= Config.languages.get(langFrom)%> &nbsp;
	            	  <%
	              }
	              %>
	              
	              <br>
	              <br>
	              <textarea rows="3" cols="45" name="q">${q}</textarea>
	              <br>
                  <input type="submit" name="translate" value="Translate">
   				 <% 
				    if (null != translations && translations.size() > 0)
				    {
				    	%>
			    		<a target="_blank" href="/n4j-nms/query?vt=graph&storage=kms&q=select (name='${q}' and language='${from}')/[depth='8']">View Network</a>
				    	<%
				    }
   				 %>
            </div>

            <div class="grid_6">
                  To:
	              <% 
	              String checkedLangTo = request.getParameter("to");
            	  if (null == checkedLangTo)
            		  checkedLangTo = Config.languages.keySet().iterator().next();

	              for(String langTo : Config.languages.keySet())
	              {
	            	  %>
			              <input type="radio" name="to" value="<%= langTo%>" 
			              <% if (checkedLangTo.equals(langTo))
			              {
			            	out.print("checked=\"checked\"");  
			              }
			              %>
			              ><%= Config.languages.get(langTo)%> &nbsp;
	            	  <%
	              }
	              %>
	              <br>
	              <br>
	              
                <div><!-- start translated area -->
				 <%
				    if (null != translations)
				    {
				    	int entityCounter = 1;
						for (ERBase translationEntity : translations.keySet())
						{
							%>
		                    <b><%=entityCounter++ %>.</b>
		                       
		                    <%
							Set<String> translationSet = translations.get(translationEntity);
							for (String translation : translationSet)
							{
								%>
					                    <b><a href="translate.htm?q=<%=translation %>&from=${to}&to=${from}"><%=translation %></a></b>
					                       
					                       <%
					                       if (reverseTranslations.get(translation).size() > 0)
					                       {
					                    	   %>&nbsp;&nbsp; - &nbsp;&nbsp;<%
					                       }
					                       %>
					                       
					                       <%
					                       for (String reverseTr : reverseTranslations.get(translation))
					                       {
					                    	   %><a href="translate.htm?q=<%=reverseTr %>&from=${from}&to=${to}"><%=reverseTr %></a> 
					                    	   <%
					                       }
					                       %>
					                       <br/>
								<%
							}					
							%>
							<br/>
							
							<%
							// use translationEntity for rep list
					        for (Representation rep : translationEntity.getRepresentations())
					        {
					    		%>
					    		<a target="_blank" href="internal-representation.htm?id=<%=rep.getUuid()%>"><img alt="" height="50px" src="internal-representation.htm?id=<%=rep.getUuid()%>"></a>
					    		<%
					        }
							%>
							<br/>------------------------------<br/>
							<%
						}
				    }
				 %>                
                </div><!-- end translated area -->
            </div>
	    </form>
	
	</div>
	
    <div class="grid_12 l r t b" style="padding: 10px;">
	  <br/>
	  <br/>
	  <br/>
	  <b>Words in dictionaries</b>:
      <% 
      for(String lang : Config.languages.keySet())
      {
    	  %>
        <a href="dictionary.htm?lang=<%= lang%>"><%= Config.languages.get(lang)%></a> &nbsp;
    	  <%
      }
      %>
	  <br/>
	
	</div>
	
</div>


<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>
