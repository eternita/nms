package org.neuro4j.web.console.controller.view;

import java.util.LinkedHashSet;
import java.util.Set;

import org.neuro4j.core.Entity;
import org.neuro4j.logic.swf.SWFConstants;

public  class ViewComponent {
	
	Entity entity;

	int top;
	int left = 0;
	
	Set<ViewRelation> connections = new LinkedHashSet<ViewRelation>();
	
	public ViewComponent(Entity entity)
	{
		this.entity = entity;
	}
	
	public String getId() {
		return this.entity.getUuid();
	}

	public String getName() {
		return entity.getName();
	}

	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}

	public Set<ViewRelation> getConnectionsTo() {
		return connections;
	}
	
	public void addConnectionTo(ViewRelation connection)
	{
		connections.add(connection);
	}
	
	public int getConnectionCount()
	{
		return connections.size();
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	public String getCss() {

		String clazz = this.entity.getProperty(SWFConstants.SWF_BLOCK_CLASS);
		if (null == clazz)
			return "";

		clazz = clazz.substring(clazz.lastIndexOf(".") + 1);
		if ("StartBlock".equalsIgnoreCase(clazz)) {
			return "triangle";
		} else if ("ConditionBlock".equalsIgnoreCase(clazz)
				|| "LoopBlock".equalsIgnoreCase(clazz)
				|| "FollowByRelation".equalsIgnoreCase(clazz)) {
           return "rhombus";
		} else if ("EndBlock".equalsIgnoreCase(clazz)){
			return "pentagon";
//		}else if ("KeyMapper".equalsIgnoreCase(clazz)){
//			return "rectangle";
		}else if ("JoinBlock".equalsIgnoreCase(clazz)){
			return "circle-sm";
		}
		return "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ViewComponent other = (ViewComponent) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		return true;
	}


	
	
}
