package org.neuro4j.logic.def;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.beanutils.ConstructorUtils;
import org.neuro4j.core.Connected;
import org.neuro4j.core.log.Logger;
import org.neuro4j.core.rel.DirectionRelation;
import org.neuro4j.logic.ExecutableEntity;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.LogicException;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.FlowInitializationException;
import org.neuro4j.logic.swf.SWEUtils;
import org.neuro4j.logic.swf.SWFConstants;

public abstract class LogicBlock  implements ExecutableEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	protected LogicBlockAdapter lba = null;
	
	protected static final int NEXT = 1;
	protected static final int ERROR = 2;
	
	LogicBlockAdapter getLogicBlockAdapter()
	{
		return lba;
	}

	protected LogicBlock() {
		super();
		lba = new LogicBlockAdapter();
		lba.setName(this.getClass().getSimpleName());
		lba.setProperty(SWFConstants.SWF_BLOCK_CLASS, this.getClass().getCanonicalName());
	}
	
	public void setParams(String... params)  throws LogicException
	{
		for (String param : params)
		{
			int idx = param.indexOf(":"); // use indexof() instead of split() because we check first : only (eg PARAM1:redirect:/mypage)
			if (idx > 1)
			{
				String key = param.substring(0, idx);				
				String value = param.substring(idx + 1);
				lba.setProperty(SWFConstants.SWF_PARAM_PREFIX + key, value); 
			} else {
				throw new LogicException("Wrong parameter declaration: " + param);
			}
			
		}
		return;
	}
	
	
	public void process(LogicContext ctx) throws FlowExecutionException {
		validate(ctx);
		execute(ctx);
	}


	public void validate(LogicContext ctx) throws FlowExecutionException
	{
		return;
	}

	public void load(Connected entity) throws FlowInitializationException {
		lba.init(entity);
	}
	
	/**
	 * return outgoing relations (lba.uuid == relation.from)
	 * filtered by relation name
	 * 
	 * @param relationName
	 * @return
	 */
	private  List<Connected> getOutgoingRelations(String relationName)
	{
		List<Connected> outRelations = new ArrayList<Connected>(3);
		
		for (Connected r : lba.getConnected())
		{
			if (lba.getUuid().equals(r.getProperty(DirectionRelation.FROM_KEY)))
			{
				// filter by direction only
				if (null == relationName)
				{
					outRelations.add(r);
					 
				// is used by Switch Block	
				} else if ("null".equals(relationName) && null == r.getName()) {
					outRelations.add(r);
					
				} else if (relationName.equals(r.getName())) {
					// filter by direction and relation name
					outRelations.add(r);
				}
			} // if (lba.getUuid().equals(r.getProperty(DirectionRelation.FROM_KEY)))
		} // for (Relation r : lba.getRelations())
		
		return outRelations;
	}

	protected  List<String> getOutgoingRelationsUUID(String relationName)
	{
		List<Connected> outRelations = getOutgoingRelations(relationName);
		if (outRelations.isEmpty())
		{
			return Collections.emptyList();
		}
		
		List<String> outRelationsUUIDs = new ArrayList<String>(outRelations.size());
		for (Connected relation: outRelations)
		{
			 Collection<Connected> collection = relation.getConnected();
			 for(Connected e: collection)
			 {
				 if (!e.getUuid().equals(lba.getUuid()))
				 {
					 outRelationsUUIDs.add(e.getUuid());
				 }
			 }
		}
		return outRelationsUUIDs;		
	}
	
	protected  Map<String, String> getOutgoingRelationsMap()
	{
		List<Connected> outRelations = getOutgoingRelations(null);
		if (outRelations.isEmpty())
		{
			return Collections.emptyMap();
		}
		
		Map<String, String> outRelationsMap = new HashMap<String, String>(outRelations.size());
		for (Connected relation: outRelations)
		{
			 Collection<Connected> collection = relation.getConnected();
			 for(Connected e: collection)
			 {
				 if (!e.getUuid().equals(lba.getUuid()))
				 {
					 outRelationsMap.put(relation.getName(), e.getUuid());
				 }
			 }
		}
		return outRelationsMap;		
	}
	
	protected String getNotEmptyProperty(String propertyname)
	{
		String value = lba.getProperty(propertyname);
		if(value != null && value.trim().equals(""))
		{
			return null;
		}
		
		return value;
	}

	abstract public int execute(LogicContext ctx) throws FlowExecutionException;

    protected final  void evaluateParameterValue(String source, String target, LogicContext ctx)
    {
    	Object obj = null;
		
		// 1) if null
		if (SWFConstants.NULL_VALUE.equalsIgnoreCase(source))
		{
			ctx.put(target, null);
			return;

	    // 2) if create new class expression	
		} else  if(source.startsWith(SWFConstants.NEW_CLASS_SYMBOL_START) && source.endsWith(SWFConstants.NEW_CLASS_SYMBOL_END)) {
			
			source = source.replace(SWFConstants.QUOTES_SYMBOL, "").replace("(", "").replace(")", "");
			
			obj = createNewInstance(source);
			
			ctx.put(target, obj);
			return;
		}

		
		String[] parts = source.split("\\+");
		
		// if concatenated string
	    if (parts.length > 1)
	    {
	    	String stringValue = "";
	    	
			for (String src: parts)
			{
				stringValue += (String)ctx.get(src);			 			
			}
			obj = stringValue;
			
	    } else {
	    	obj = ctx.get(source);
	    }


		ctx.put(target, obj);
	
    }
    
    
	private Object createNewInstance(String clazzName) {
		Class<?> beanClass = null;
		Object beanInstance = null;
		try {
			beanClass = Class.forName(clazzName);
			beanInstance = ConstructorUtils.invokeConstructor(beanClass, null);
		} catch (Exception e) {
			Logger.error(SWEUtils.class, e.getMessage(), e);			
		}

		return beanInstance;

	}
	
}
