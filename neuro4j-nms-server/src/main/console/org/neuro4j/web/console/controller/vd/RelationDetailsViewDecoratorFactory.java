package org.neuro4j.web.console.controller.vd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Relation;


public class RelationDetailsViewDecoratorFactory {

	private static Map<String, ViewDecorator> decorators = new HashMap<String, ViewDecorator>();
	
	private static ViewDecorator DEFAULT_DECORATOR = new DefaultDecorator();
	
	public static String render(Entity displayedEntity, String groupName, List<Relation> relations, HttpServletRequest request)
	{
		ViewDecorator d = decorators.get(groupName);
		if (null == d)
			d = DEFAULT_DECORATOR;
		
		return d.render(displayedEntity, groupName, relations, request);
	}
	
}
