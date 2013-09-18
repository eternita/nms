package org.neuro4j.web.console.controller.vd;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.neuro4j.core.Connected;


public interface ViewDecorator {

	public String render(Connected displayedEntity, String groupName, List<Connected> relations, HttpServletRequest request);
	
}
