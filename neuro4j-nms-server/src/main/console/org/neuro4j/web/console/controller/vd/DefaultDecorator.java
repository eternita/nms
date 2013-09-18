package org.neuro4j.web.console.controller.vd;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.neuro4j.core.Connected;


public class DefaultDecorator implements ViewDecorator {

	public String render(Connected displayedEntity, String groupName, List<Connected> relations, HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();

		
		for (Connected r : relations)
		{
    		sb.append("<b><a href='entity-details?storage=" + request.getParameter("storage") + "&vt=graph&eid=" + r.getUuid() +"'>" + r.getName() + "</a></b><br/>");
    		sb.append("<br/>");
		}			
		
		
		return sb.toString();
	}

}
