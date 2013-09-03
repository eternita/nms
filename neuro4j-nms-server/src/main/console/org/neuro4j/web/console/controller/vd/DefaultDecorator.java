package org.neuro4j.web.console.controller.vd;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.neuro4j.core.ERBase;


public class DefaultDecorator implements ViewDecorator {

	public String render(ERBase displayedEntity, String groupName, List<ERBase> relations, HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();

		
		for (ERBase r : relations)
		{
    		sb.append("<b><a href='entity-details?storage=" + request.getParameter("storage") + "&vt=graph&eid=" + r.getUuid() +"'>" + r.getName() + "</a></b><br/>");
    		sb.append("<br/>");
		}			
		
		
		return sb.toString();
	}

}
