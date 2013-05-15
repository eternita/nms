package org.neuro4j.logic.def;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Relation;
import org.neuro4j.core.rel.DirectionRelation;
import org.neuro4j.logic.ExecutableEntity;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.LogicException;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.FlowInitializationException;
import org.neuro4j.logic.swf.SWFConstants;

public abstract class LogicBlock  implements ExecutableEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected final transient Logger logger = Logger.getLogger(getClass().getName());
	
	protected LogicBlockAdapter lba = null;
	
	private boolean loaded = false;
	
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
		
		// get info about context parameters and put it to properties
		LogicBlockInfo lbi = getLogicBlockInfo();
		if (null != lbi)
		{
			String blockDesc = lbi.getDescription();
			if (null != blockDesc)
				lba.setProperty(SWFConstants.SWF_BLOCK_DESCRIPTION, blockDesc);
			
			Set<String> parametersInfo = lbi.getParametersInfo();
			if (null != parametersInfo)
				for(String paramInfo : parametersInfo)
					lba.setProperty(SWFConstants.SWF_BLOCK_PARAM_INFO, paramInfo);
		}
		

	}


	protected LogicBlock(String name, String actionClass) {
		this();
		lba = new LogicBlockAdapter();
		lba.setName(this.getClass().getSimpleName());
		lba.setProperty(SWFConstants.SWF_BLOCK_CLASS, actionClass);
	}
	
	
	protected LogicBlock(String name, String customBlockClass, String... params)  throws LogicException {
		this(name, customBlockClass);
		setParams(params);
	}
		
	protected LogicBlock(String... params) throws LogicException {
		this();
		setParams(params);
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
	

	
	public LogicBlockInfo getLogicBlockInfo() {
		LogicBlockInfo lbi = new LogicBlockInfo();
		return lbi;
	}

	public void load(Entity entity) throws FlowInitializationException {
		lba.init(entity);
	}

	public final boolean isLoaded() {
		return loaded;
	}

	public final void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}	
	
	protected  List<Relation> getOutgoingRelations(String relationName)
	{
		List<Relation> outRelations = new ArrayList<Relation>(3);
		
		for (Relation r : lba.getRelations())
		{
			if (lba.getUuid().equals(r.getProperty(DirectionRelation.FROM_KEY)) && r.getName() != null
					&& r.getName().equals(relationName)){
				outRelations.add(r);
			} if (r.getName() == null && (relationName == null || "".equals(relationName.trim())))
			{
				outRelations.add(r);
			}

		}
		
		return outRelations;
	}

	protected  List<String> getOutgoingRelationsUUID(String relationName)
	{
		List<Relation> outRelations = getOutgoingRelations(relationName);
		if (outRelations.isEmpty())
		{
			return Collections.emptyList();
		}
		
		List<String> outRelationsUUIDs = new ArrayList<String>(outRelations.size());
		for (Relation relation: outRelations)
		{
			 Collection<Entity> collection = relation.getAllParticipants();
			 for(Entity e: collection)
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
		List<Relation> outRelations = getOutgoingRelations(null);
		if (outRelations.isEmpty())
		{
			return Collections.emptyMap();
		}
		
		Map<String, String> outRelationsMap = new HashMap<String, String>(outRelations.size());
		for (Relation relation: outRelations)
		{
			 Collection<Entity> collection = relation.getAllParticipants();
			 for(Entity e: collection)
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

	
	
}
