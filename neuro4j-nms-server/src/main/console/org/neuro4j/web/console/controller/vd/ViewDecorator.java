package org.neuro4j.web.console.controller.vd;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.neuro4j.core.ERBase;


public interface ViewDecorator {

	public String render(ERBase displayedEntity, String groupName, List<ERBase> relations, HttpServletRequest request);
	
}
