package org.neuro4j.logic.def;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.core.Relation;
import org.neuro4j.core.rel.DirectionRelation;
import org.neuro4j.logic.ExecutableEntityNotFoundException;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.LogicProcessor;
import org.neuro4j.logic.LogicProcessorException;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.FlowInitializationException;
import org.neuro4j.logic.swf.SWFConstants;
import org.neuro4j.storage.NeuroStorage;
import org.neuro4j.storage.StorageException;

public class WorkFlowProcessor implements LogicProcessor
{

	private static final Logger logger = Logger.getLogger(WorkFlowProcessor.class.getName());
	
	public WorkFlowProcessor() {
		super();
	}

	public void init(Properties properties) throws LogicProcessorException {
		
	}
	

	public LogicContext action(Entity start, Network network, NeuroStorage neuroStorage, LogicContext logicContext) throws FlowExecutionException
	{
		if (null == logicContext)
			throw new RuntimeException("LogicContext must not be null");
		
		Entity nextStep = start; 
		Entity currentStep = start; 
		while (null != nextStep)
		{
			currentStep = nextStep; 

			// reload from storage to have fresh 'relations' in case of remote storage (TODO: rework for performance)
			currentStep = getEntityByUUID(currentStep.getUuid(), network, neuroStorage);
			
			nextStep = actionImpl(currentStep, network, neuroStorage, logicContext); 

		} // while (null != nextStep)
		
		
		return logicContext;
	}
	
	private static Entity actionImpl(Entity currentStep, Network network, NeuroStorage neuroStorage, LogicContext logicContext) throws FlowExecutionException
	{
		if (null == logicContext)
			throw new FlowExecutionException("LogicContext must not be null");

		Entity nextStep = null;
		String className = getExecutableClass(currentStep);

		if (null != className)
		{
			
			LogicBlock logicNode;
			try {
				logicNode = LogicBlockLoader.getInstance().lookupBlock(currentStep.getUuid(), className);
				
				
				if(!logicNode.isLoaded())
				{
					logicNode.load(currentStep);
				}
				
				logger.finest("running " + logicNode.getClass().getSimpleName() + " (" +  logicNode.getClass().getCanonicalName() + ")");
				Set<Entity> stack = getExecutionStack(logicContext);
				stack.add(currentStep);
				logicContext.put(SWFConstants.AC_CURRENT_NODE, currentStep);

				logicNode.process(logicContext);
				
			} catch (ExecutableEntityNotFoundException e1) {
				throw new FlowExecutionException(e1.getCause());
			} catch (FlowInitializationException e) {
				throw new FlowExecutionException(e.getCause());
			} 
		}
		
		
		if (null != logicContext.get(SWFConstants.AC_NEXT_NODE_UUID))
		{
			String nextStepUUID = (String) logicContext.remove(SWFConstants.AC_NEXT_NODE_UUID);
			nextStep = getEntityByUUID(nextStepUUID, network, neuroStorage);
		}

		if (null == nextStep)
			nextStep = getNext(currentStep, network, neuroStorage);
		
		return nextStep;
	}
	

	private static Set<Entity> getExecutionStack(LogicContext logicContext)
	{
		Set<Entity> stack = (Set<Entity>) logicContext.get(SWFConstants.AC_ACTION_STACK);
		if (null == stack)
		{
			stack = new LinkedHashSet<Entity>();
			logicContext.put(SWFConstants.AC_ACTION_STACK, stack);
		}
		return stack;
	}


	private static String getExecutableClass(Entity currentStep) throws FlowExecutionException
	{
		String className = currentStep.getProperty("SWF_CUSTOM_CLASS");
		
		if (className == null)
		{
			className = currentStep.getProperty(SWFConstants.SWF_BLOCK_CLASS);
		}
		if (className == null)
		{
			throw new FlowExecutionException("Executable node is unknown");
		}
		return className;
	}
	
	/**
	 * Returns next node to execute or NULL
	 * 
	 * @param currentStep
	 * @return
	 */
	private static Entity getNext(Entity currentStep, Network network, NeuroStorage neuroStorage)
	{
		Entity next = null;
		for (Relation r : currentStep.getRelations(SWFConstants.NEXT_RELATION_NAME))
		{
			if (null != r)
			{
				String nextEid = r.getProperty(DirectionRelation.TO_KEY);
				if (null == nextEid) 
					continue;

				Set<Entity> rparts = r.getAllParticipants(currentStep.getUuid());
				if (rparts.size() > 0)
				{
					Entity rp = rparts.iterator().next();
					if (nextEid.equals(rp.getUuid()))
					{
						next = rp;
						
						// check if next has futher relations -> if not -> reload it from storage
						if (0 == next.getRelations(SWFConstants.NEXT_RELATION_NAME).size())
						{
							next = getEntityByUUID(next.getUuid(), network, neuroStorage); // reload from storage to get futher relations
						}
						
						break;
					}
				} else {
					logger.finest(currentStep.getName() + " -> " + SWFConstants.NEXT_RELATION_NAME + " - > ??? (not specified)");
				}
			} else {
				logger.finest(currentStep.getName() + " has no relation " + SWFConstants.NEXT_RELATION_NAME);
			}
		} // for (Relation r : currentStep.getRelations(NEXT_RELATION_NAME))
		
		return next;
	}
	
	private static Entity getEntityByUUID(String uuid, Network network, NeuroStorage neuroStorage)
	{
		Entity e = null;
		
		// try to resolve from local network
		if (null != network)
			e = network.getEntityByUUID(uuid);
		
		if (null != e)
			return e;
		
		try {
			e = neuroStorage.getEntityByUUID(uuid);
		} catch (StorageException e1) {
			logger.fine("Can't load entity with id " + uuid + " " + e1.getMessage());
		}
		
		return e;
	}



	
}
