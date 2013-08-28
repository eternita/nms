package org.neuro4j.logic.def;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.neuro4j.core.ERBase;
import org.neuro4j.core.Network;
import org.neuro4j.core.rel.DirectionRelation;
import org.neuro4j.logic.ExecutableEntityNotFoundException;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.LogicProcessor;
import org.neuro4j.logic.LogicProcessorException;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.FlowInitializationException;
import org.neuro4j.logic.swf.SWFConstants;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;

public class WorkFlowProcessor implements LogicProcessor
{

	private static final Logger logger = Logger.getLogger(WorkFlowProcessor.class.getName());
	
	public WorkFlowProcessor() {
		super();
	}

	public void init(Properties properties) throws LogicProcessorException {
		
	}
	

	public LogicContext action(ERBase start, Network network, Storage storage, LogicContext logicContext) throws FlowExecutionException
	{
		if (null == logicContext)
			throw new RuntimeException("LogicContext must not be null");
		
		ERBase nextStep = start; 
		ERBase currentStep = start; 
		while (null != nextStep)
		{
			currentStep = nextStep; 

			// reload from storage to have fresh 'relations' in case of remote storage (TODO: rework for performance)
			currentStep = getEntityByUUID(currentStep.getUuid(), network, storage);
			
			nextStep = actionImpl(currentStep, network, storage, logicContext); 

		} // while (null != nextStep)
		
		
		return logicContext;
	}
	
	private static ERBase actionImpl(ERBase currentStep, Network network, Storage storage, LogicContext logicContext) throws FlowExecutionException
	{
		if (null == logicContext)
			throw new FlowExecutionException("LogicContext must not be null");

		ERBase nextStep = null;
		String className = getExecutableClass(currentStep);

		if (null != className)
		{
			
			LogicBlock logicNode;
			try {
				logicNode = LogicBlockLoader.getInstance().lookupBlock(currentStep, className);
				
				logger.finest("running " + logicNode.getClass().getSimpleName() + " (" +  logicNode.getClass().getCanonicalName() + ")");
				Set<ERBase> stack = getExecutionStack(logicContext);
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
			nextStep = getEntityByUUID(nextStepUUID, network, storage);
		}

		if (null == nextStep)
			nextStep = getNext(currentStep, network, storage);
		
		return nextStep;
	}
	

	private static Set<ERBase> getExecutionStack(LogicContext logicContext)
	{
		Set<ERBase> stack = (Set<ERBase>) logicContext.get(SWFConstants.AC_ACTION_STACK);
		if (null == stack)
		{
			stack = new LinkedHashSet<ERBase>();
			logicContext.put(SWFConstants.AC_ACTION_STACK, stack);
		}
		return stack;
	}


	private static String getExecutableClass(ERBase currentStep) throws FlowExecutionException
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
	private static ERBase getNext(ERBase currentStep, Network network, Storage storage)
	{
		ERBase next = null;
//		for (ERBase r : currentStep.getRelations(SWFConstants.NEXT_RELATION_NAME))
		for (ERBase r : currentStep.getConnected("name", SWFConstants.NEXT_RELATION_NAME))
		{
			if (null != r)
			{
				String nextEid = r.getProperty(DirectionRelation.TO_KEY);
				if (null == nextEid) 
					continue;

				Set<ERBase> rparts = r.getAllConnectedFiltered(currentStep.getUuid());
				if (rparts.size() > 0)
				{
					ERBase rp = rparts.iterator().next();
					if (nextEid.equals(rp.getUuid()))
					{
						next = rp;
						
						// check if next has futher relations -> if not -> reload it from storage
						if (0 == next.getConnected("name", SWFConstants.NEXT_RELATION_NAME).size())
						{
							next = getEntityByUUID(next.getUuid(), network, storage); // reload from storage to get futher relations
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
	
	private static ERBase getEntityByUUID(String uuid, Network network, Storage storage)
	{
		ERBase e = null;
		
		// try to resolve from local network
		if (null != network)
			e = network.getById(uuid);
		
		if (null != e)
			return e;
		
		try {
			e = storage.getById(uuid);
		} catch (StorageException e1) {
			logger.fine("Can't load entity with id " + uuid + " " + e1.getMessage());
		}
		
		return e;
	}



	
}
