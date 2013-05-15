package org.neuro4j.logic.swf;

import java.lang.annotation.Annotation;

public class ParameterDefinitionImpl implements ParameterDefinition {

	private String name;
	private String type;
	private Boolean optional;
	
	public ParameterDefinitionImpl(String name, String type, Boolean optional)
	{
		this.name = name;
		this.type = type;
		this.optional = optional;
	}
	
	public Class<? extends Annotation> annotationType() {
		// TODO Auto-generated method stub
		return ParameterDefinition.class;
	}

	public String name() {
		// TODO Auto-generated method stub
		return name;
	}

	public String type() {
		// TODO Auto-generated method stub
		return type;
	}

	public boolean isOptional() {
		// TODO Auto-generated method stub
		return optional;
	}
	
	

}
