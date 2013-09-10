package org.neuro4j.weblog.block;


import static org.neuro4j.weblog.block.BindRequestToPage.IN_REQUEST;
import static org.neuro4j.weblog.block.BindRequestToPage.OUT_PAGE;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.neuro4j.NetworkUtils;
import org.neuro4j.core.ERBase;
import org.neuro4j.core.Network;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.def.CustomBlock;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.ParameterDefinition;
import org.neuro4j.logic.swf.ParameterDefinitionList;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;

@ParameterDefinitionList(input={
                                	@ParameterDefinition(name=IN_REQUEST, isOptional=false, type= "org.neuro4j.core.ERBase")},
						output={
						     		@ParameterDefinition(name=OUT_PAGE, isOptional=false, type= "org.neuro4j.core.ERBase")})	

public class BindRequestToPage extends CustomBlock {
    
	static final String CURRENT_STORAGE = "CURRENT_STORAGE";

	static final String IN_REQUEST = "request";
	
	static final String OUT_PAGE = "page";
      
    
    @Override
    public int execute(LogicContext ctx) throws FlowExecutionException {
		
    	ERBase request = (ERBase) ctx.get(IN_REQUEST);
        
		Storage currentStorage = (Storage) ctx.get(CURRENT_STORAGE); 
		Network net = null;
		try {
			net = currentStorage.query("select r(name='website_pages')/e()");
			
			Set<ERBase> pages = net.getWithProperty("page_match_pattern");
			
			String urlStr = request.getProperty("request-url");
			ERBase pageTemplate = getPage(pages, urlStr);
			if (null == pageTemplate)
				pageTemplate = net.getFirst("name", "default-page");
			
			net = currentStorage.query("select e(page-template-id=? and session-id=?)", 
					new String[]{"" + pageTemplate.getUuid(), request.getProperty("session-id")});
			
			ERBase contextPage = null;
			if (null != net && net.getSize() > 0)
				contextPage = net.getById(net.getIds()[0]);
					
			if (null == contextPage)
			{
				contextPage = pageTemplate.cloneBase();
				contextPage.setProperty("session-id", request.getProperty("session-id"));
				contextPage.setProperty("page-template-id", pageTemplate.getUuid());
			}
			
			net = new Network();
			net.add(contextPage);
			net.add(request);
			NetworkUtils.addRelation(net, contextPage, request, "request-page");
			
			currentStorage.save(net);
			
			ctx.put(OUT_PAGE, contextPage); 
			
		} catch (NQLException e) {
			e.printStackTrace();
			return ERROR;
		} catch (StorageException e) {
			e.printStackTrace();
			return ERROR;
		}

		

		return NEXT;
	}
	
    
	private ERBase getPage (Set<ERBase> pages, String urlStr)
	{
		for (ERBase page : pages)
		{
			String patterns = page.getProperty("page_match_pattern");
			String[] patternArray = patterns.split("\\|"); 
			for (String matchRegexpPatterm : patternArray)
			{
				if ( match(urlStr, matchRegexpPatterm)) //(urlStr.split(matchRegexpPatterm).length > 1)
					return page;
			}
		}
		return null;
	}
	
	private boolean match(String str, String regexp)
	{
		Pattern p = Pattern.compile(regexp);
		 Matcher m = p.matcher(str);
		 return m.matches();
	}	
	
}
