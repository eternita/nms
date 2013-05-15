package org.neuro4j.web.console.controller.view;

import org.neuro4j.core.Entity;
import org.neuro4j.logic.swf.SWFConstants;



public class ViewRelation {
	String sourceId;
	String targetId;
	String anchor = "TopCenter";
	
	public ViewRelation(Entity source, Entity target)
	{
		this.sourceId = source.getUuid();
		this.targetId = target.getUuid();

		String sourceClazz = source.getProperty(SWFConstants.SWF_BLOCK_CLASS);
		if (null == sourceClazz)
			return;
		sourceClazz = sourceClazz.substring(sourceClazz.lastIndexOf(".") + 1);
		String targetClazz = target.getProperty(SWFConstants.SWF_BLOCK_CLASS);
		if (null == targetClazz)
			return;
		targetClazz = targetClazz.substring(targetClazz.lastIndexOf(".") + 1);
		if ("LoopBlock".equals(targetClazz) && "JoinBlock".equals(sourceClazz))
		{
			setAnchor("RightMiddle");
		}
		

	}
	
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	
	public String getAnchor() {
		return anchor;
	}

	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sourceId == null) ? 0 : sourceId.hashCode());
		result = prime * result
				+ ((targetId == null) ? 0 : targetId.hashCode());
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
		ViewRelation other = (ViewRelation) obj;
		if (sourceId == null) {
			if (other.sourceId != null)
				return false;
		} else if (!sourceId.equals(other.sourceId))
			return false;
		if (targetId == null) {
			if (other.targetId != null)
				return false;
		} else if (!targetId.equals(other.targetId))
			return false;
		return true;
	}
	
	

}
