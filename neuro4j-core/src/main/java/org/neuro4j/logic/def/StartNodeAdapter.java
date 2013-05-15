package org.neuro4j.logic.def;

import org.neuro4j.core.Entity;
import org.neuro4j.logic.swf.SWFParametersConstants;
import org.neuro4j.logic.swf.enums.StartNodeTypes;

public class StartNodeAdapter {
	
	Entity entity;
	StartNodeTypes type;
	String flowPackage;
	
	private StartNodeAdapter()
	{
		super();
	}
	
	public StartNodeAdapter(Entity entity, String flowPackage)
	{
		this();
		this.entity = entity;
		this.flowPackage = flowPackage;
		proccessParameters(entity);
	}
	
	private void proccessParameters(Entity startNode) {
		String nodeType = startNode.getProperty(SWFParametersConstants.START_NODE_TYPE);
		nodeType = (nodeType == null || nodeType.trim().equals("")) ? StartNodeTypes.getDefaultType().name(): nodeType.toUpperCase();
		type = StartNodeTypes.valueOf(nodeType);
	}
	
	public boolean isPublic()
	{
		return type == StartNodeTypes.PUBLIC;
	}
	
	public String getPackage()
	{
		return flowPackage;
	}
	
	public String getName()
	{
		return entity.getName();
	}
	
	public Entity getEntity()
	{
		return entity;
	}
	
}
