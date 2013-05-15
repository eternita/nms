package org.neuro4j.web.console.controller.vd;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Relation;


public interface ViewDecorator {

	public String render(Entity displayedEntity, String groupName, List<Relation> relations, HttpServletRequest request);
	
}
