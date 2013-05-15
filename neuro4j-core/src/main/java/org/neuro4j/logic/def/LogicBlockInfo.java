package org.neuro4j.logic.def;

import java.util.LinkedHashSet;
import java.util.Set;

public class LogicBlockInfo {
	
	private String description;
	private Set<String> parametersInfo = new LinkedHashSet<String>();
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Set<String> getParametersInfo() {
		return parametersInfo;
	}
	
	/**
	 * 
	 * @deprecated Use addParameterInfo(String inout, String mo, String key, Class clazz, String description)
	 * 
	 * @param parameterInfo
	 */
	public void addParameterInfo(String parameterInfo)
	{
		parametersInfo.add(parameterInfo);
	}
	
	/**
	 * 
	 * @param inout IN/OUT
	 * @param mo MANDATORY/OPTIONAL
	 * @param key Context key name
	 * @param clazz implementation class name
	 * @param description 
	 */
	public void addParameterInfo(String inout, String mo, String key, Class clazz, String description)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(inout).append(":");
		sb.append(mo).append(":");
		sb.append(clazz.getName()).append(":");
		sb.append(description).append(":");
		parametersInfo.add(sb.toString());
	}
	
	
}
