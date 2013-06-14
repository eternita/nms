
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@page import="org.neuro4j.web.console.utils.StringUtils"%>
<%@page import="org.neuro4j.core.*"%>
<%@page import="org.neuro4j.core.rel.*"%>
<%@page import="java.util.*"%>
<%@page import="org.neuro4j.web.console.controller.vd.*"%>

<%
Entity entity = (Entity) request.getAttribute("entity");
String localizedEntityName = entity.getName();
%>
<div align="center">
<b><a title="${entity.name}" href='entity-details?storage=${storage}&vt=graph&eid=${entity.uuid}'><%=StringUtils.getShortStr(localizedEntityName, 30) %></a></b><br/>

<%
if (null != entity.getProperty("SWF_BLOCK_CLASS"))
{
%>
<br/><a href='entity-details?storage=${storage}&eid=${entity.uuid}'>Edit</a>
<br/><a href="query?storage=${storage}&vt=jsplumb&q=${q}&startNodeId=${entity.uuid}">View as flow</a>
<br/><a href='init-action?storage=${storage}&eid=${entity.uuid}'>Run it!</a>
<%	
}
%>
<br/>
<div style="padding: 10px; font-size: medium;"><b>Relations (<%= entity.getRelationsKeys().size() %>)</b></div>

<div align="left" style="vertical-align: top;" class="b">


<ul class="acc" id="acc_video" > 

<%

Map<String, List<Relation>> groupedRelationMap = (Map<String, List<Relation>>) request.getAttribute("grouped_relation_map");
for (String groupName : groupedRelationMap.keySet())
{
	String groupNameStr = groupName + " (" + groupedRelationMap.get(groupName).size() + ")";
    %>
    <li class="l r t" > 
        <h3 title="<%= groupNameStr %>"><%= StringUtils.getShortStr(groupNameStr, 29) %>
         </h3> 
        <div class="acc-section"> 
            <div class="acc-content">   

                 <%= RelationDetailsViewDecoratorFactory.render(entity, groupName, groupedRelationMap.get(groupName), request) %>  
  
                 
            </div>     
        </div> 
    </li> 
    <%
}
%>

    <li class="l r t"> 
        <h3>Properties</h3> 
        <div class="acc-section"> 
            <div class="acc-content" style="padding: 1px; margin: 1px;">   
              <table width="100%">
                   <tr>
                     <td width="50%" class="r" ><span title="id">id</span></td>
                     <td width="50%"><span title="<%=entity.getUuid()%>"><%=StringUtils.getShortStr(entity.getUuid(), 15) %></span></td>
                   </tr>
                  <%
                  	for (String key : entity.getPropertyKeys())
                    {
                  %> 
			            <tr>
			              <td width="50%" class="r" ><span title="<%=key%>"><%=StringUtils.getShortStr(key, 15) %></span></td>
			              <td width="50%"><span title="<%=entity.getProperty(key)%>"><%=StringUtils.getShortStr(entity.getProperty(key), 15) %></span></td>
			            </tr>
                  <%
                    }
                  %>   
              </table>
            </div>     
        </div> 
    </li> 
    <%
      if (entity.getRepresentations().size() > 0)
      {
    %> 
    <%
      }
    %>   
     
    <li class="l r t"> 
        <h3>Representations</h3> 
        <div class="acc-section"> 
            <div class="acc-content" style="padding: 1px; margin: 1px;">   
              <table width="100%">
                  <%
                    for (Representation rep : entity.getRepresentations())
                    {
                  %> 
                  <%
                    for (String key : rep.getPropertyKeys())
                    {
                  %> 
                        <tr>
                          <td width="50%" class="r" ><span title="<%=key%>"><%=StringUtils.getShortStr(key, 15) %></span></td>
                          <td width="50%"><span title="<%=rep.getProperty(key)%>"><%=StringUtils.getShortStr(rep.getProperty(key), 15) %></span></td>
                        </tr>
                  <%
                    }
                  %>   
                   
                  <%
                    }
                  %>   
                   
              </table>
            </div>     
        </div> 
    </li> 


</ul> 


</div> <!-- <div align="left"> -->

  
</div>

            