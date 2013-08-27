package org.neuro4j.logic.def;

import java.util.List;
import java.util.Set;

import org.neuro4j.core.ERBase;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.LogicException;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.FlowInitializationException;
import org.neuro4j.logic.swf.ParameterDefinition;
import org.neuro4j.logic.swf.ParameterDefinitionList;
import org.neuro4j.logic.swf.SWEUtils;
import org.neuro4j.logic.swf.SWFConstants;
import org.neuro4j.logic.swf.SWFParametersConstants;



/**
 * 
 * Entry extension point for writing new blocks. 
 *
 */
public abstract class CustomBlock extends LogicBlock {
	
	private static final String NEXT_EXIT_RELATION = SWFConstants.NEXT_RELATION_NAME;
	private static final String ERROR_EXIT_RELATION = "ERROR";
	

	
	private String mainExit = null;
	private String errorExit = null;

	public CustomBlock() {
		super();
	}
	
	
	public final void load(ERBase entity) throws FlowInitializationException
	{
		 super.load(entity);
		 init();
		 
		 List<String> nextRelations = getOutgoingRelationsUUID(NEXT_EXIT_RELATION);
		 if (nextRelations != null && nextRelations.size() >= 1)
		 {
			 mainExit = nextRelations.get(0);
		 }
		 
		 List<String> falseRelations = getOutgoingRelationsUUID(ERROR_EXIT_RELATION);
		 if (falseRelations != null && falseRelations.size() >= 1)
		 {
		
			 errorExit = falseRelations.get(0);
		 }
		
	}
	
	public final void process(LogicContext context)
			throws FlowExecutionException {
		validate(context);	
		int result = execute(context);
		if (result != ERROR)
		{
			doOutputMapping(context);
			context.setNextRelation(mainExit);
		} else {
			if (errorExit == null)
			{
				throw new FlowExecutionException("CustomBlock: Error connector not defined.");
			}
			context.setNextRelation(errorExit);
		}

	}
	
	@Override
	public final void validate(LogicContext ctx) throws FlowExecutionException {
		super.validate(ctx);
		ParameterDefinitionList parameterDefinitionList  = getClass().getAnnotation(org.neuro4j.logic.swf.ParameterDefinitionList.class);
		ParameterDefinition[] parameters = parameterDefinitionList.input();
		for(ParameterDefinition parameter: parameters)
		{
			String name = parameter.name();
			doMapping(ctx, name, SWFParametersConstants.CUSTOM_BLOCK_INPUT_PARAMETER_PREFIX);
			Object obj = ctx.get(name);
			if (!parameter.isOptional())
			{			
				if (obj == null)
				{
					throw new FlowExecutionException("Parameter " + name + " is mandatory for " + getClass().getName());
				}
			}
			checkPatameterType(parameter, obj);
			
		}
		
		if (mainExit == null)
		{
			throw new FlowExecutionException("CustomBlock: Connector not defined.");
		}
	}
	

	
	private void doOutputMapping(LogicContext ctx) throws FlowExecutionException
	{
		ParameterDefinitionList parameterDefinitionList  = getClass().getAnnotation(org.neuro4j.logic.swf.ParameterDefinitionList.class);
		ParameterDefinition[] parameters = parameterDefinitionList.output();
		for(ParameterDefinition parameter: parameters)
		{
			String name = parameter.name();
			String key = doOutMapping(ctx, name, SWFParametersConstants.CUSTOM_BLOCK_OUTPUT_PARAMETER_PREFIX);
			Object obj = ctx.get(key);
			if (!parameter.isOptional())
			{			
				if (obj == null)
				{
					throw new FlowExecutionException("Parameter " + name + " is mandatory for " + getClass().getName());
				}
				
			}
			checkPatameterType(parameter, obj);
			
		}

	}
	
	private void doMapping(LogicContext ctx, String originalName, String prefix)
	{
		Set<String> parameterKeys = lba.getPropertyKeys();
		
		for (String key: parameterKeys)
		{
			if (key.startsWith(SWFParametersConstants.CUSTOM_BLOCK_PARAMETER_PREFIX) && key.endsWith(":" + prefix))
			{
				String mappedValue = lba.getProperty(key);
				if (mappedValue != null && mappedValue.startsWith(originalName))
				{
					String[] splittedValue = SWEUtils.getMappedParameters(mappedValue);
					Object obj = ctx.get(splittedValue[1]);
					ctx.put(originalName, obj);
				}

			}
		}

	}
	
	private String doOutMapping(LogicContext ctx, String originalName, String prefix)
	{
		Set<String> parameterKeys = lba.getPropertyKeys();
		
		for (String key: parameterKeys)
		{
			if (key.startsWith(SWFParametersConstants.CUSTOM_BLOCK_PARAMETER_PREFIX) && key.endsWith(":" + prefix))
			{
				String mappedValue = lba.getProperty(key);
				if (mappedValue != null && mappedValue.startsWith(originalName))
				{
					String[] splittedValue = SWEUtils.getMappedParameters(mappedValue);
					Object obj = ctx.remove(originalName);
					String newName = splittedValue[1];
					ctx.put(newName, obj);
					return newName;
				}

			}
		}
		
		return originalName;

	}
	
	
	/**
	 * Checks if type in parameterDefinition is the same with object's type.
	 * @param parameterDefinition
	 * @param obj
	 * @throws LogicException
	 */
	private void checkPatameterType(ParameterDefinition parameterDefinition, Object obj) throws FlowExecutionException
	{
		if (obj == null)
		{
			return;
		}
		String className = parameterDefinition.type();
		if (className == null)
		{
			if (!parameterDefinition.isOptional())
			{
				throw new FlowExecutionException("Type should be not empty for mandatory parameter");
			}
			return;
			
		}
		if (className.equals(obj.getClass().getCanonicalName()))
		{
			return;
		}
		
		try {
			Class<?> cl =SWEUtils.class.getClassLoader().loadClass(className);
			
			if (!cl.isAssignableFrom(obj.getClass()))
			{
				throw new FlowExecutionException("Wrong parameter type. Expected type: " + className + " actual type: " + obj.getClass().getCanonicalName());
			} 
		} catch (ClassNotFoundException e) {
			throw new FlowExecutionException(e);
		}

	}
	
	protected void init() throws FlowInitializationException
	{
		
	}



}
