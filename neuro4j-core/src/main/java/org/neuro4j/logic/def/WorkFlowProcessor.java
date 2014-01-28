package org.neuro4j.logic.def;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;


import org.neuro4j.core.Connected;
import org.neuro4j.core.Network;
import org.neuro4j.core.log.Logger;
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

	
	public WorkFlowProcessor() {
		super();
	}

	public void init(Properties properties) throws LogicProcessorException {
		
	}
	

	public LogicContext action(Connected step, Network network, Storage storage, LogicContext logicContext) throws FlowExecutionException
	{
		if (null == logicContext)
			throw new RuntimeException("LogicContext must not be null");
		
		
		while (null != step)
		{

			// reload from storage to have fresh 'relations' in case of remote storage (TODO: rework for performance)
			step = getEntityByUUID(step.getUuid(), network, storage);
			
			step = actionImpl(step, network, storage, logicContext); 

		} // while (null != nextStep)
		
		
		return logicContext;
	}
	
	private static Connected actionImpl(Connected currentStep, Network network, Storage storage, LogicContext logicContext) throws FlowExecutionException
	{
		if (null == logicContext)
			
			throw new FlowExecutionException("LogicContext must not be null");

		Connected nextStep = null;
		String className = getExecutableClass(currentStep);

		if (null != className)
		{
			
			LogicBlock logicNode;
			try {
				logicNode = LogicBlockLoader.getInstance().lookupBlock(currentStep, className);
				
				
				
				Set<Connected> stack = getExecutionStack(logicContext);
				stack.add(currentStep);
				logicContext.put(SWFConstants.AC_CURRENT_NODE, currentStep);
				
				long startTime = System.currentTimeMillis();
				
				Logger.debug(WorkFlowProcessor.class, "		Running: {} ({})", logicNode.getClass().getSimpleName(), logicNode.getClass().getCanonicalName());
				
				logicNode.process(logicContext);
				
				Logger.debug(WorkFlowProcessor.class, "		Finished: ({}ms) {}", System.currentTimeMillis() - startTime, logicNode.getClass().getSimpleName());
				
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
		
		if (nextStep != null)
		{
			Logger.debug(WorkFlowProcessor.class, "Next step: {} ({})", nextStep.getName(), nextStep.getUuid());	
		}
		
		
		return nextStep;
	}
	

	private static Set<Connected> getExecutionStack(LogicContext logicContext)
	{
		Set<Connected> stack = (Set<Connected>) logicContext.get(SWFConstants.AC_ACTION_STACK);
		if (null == stack)
		{
			stack = new LinkedHashSet<Connected>();
			logicContext.put(SWFConstants.AC_ACTION_STACK, stack);
		}
		return stack;
	}


	private static String getExecutableClass(Connected currentStep) throws FlowExecutionException
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
	private static Connected getNext(Connected currentStep, Network network, Storage storage)
	{
		Connected next = null;
		
		for (Connected r : currentStep.getConnected("name", SWFConstants.NEXT_RELATION_NAME))
		{
			if (null != r)
			{
				String nextEid = r.getProperty(DirectionRelation.TO_KEY);
				if (null == nextEid) 
					continue;

				Set<Connected> rparts = r.getAllConnectedFiltered(currentStep.getUuid());
				if (rparts.size() > 0)
				{
					Connected rp = rparts.iterator().next();
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
					Logger.debug(WorkFlowProcessor.class, "{} -> {} - > ??? (not specified)", currentStep.getName(), SWFConstants.NEXT_RELATION_NAME);
				}
			} else {
				Logger.debug(WorkFlowProcessor.class, " {} has no relation {}",currentStep.getName(), SWFConstants.NEXT_RELATION_NAME);
			}
		} // for (Relation r : currentStep.getRelations(NEXT_RELATION_NAME))
		
		return next;
	}
	
	private static Connected getEntityByUUID(String uuid, Network network, Storage storage)
	{
		Connected e = null;
		
		// try to resolve from local network
		if (null != network)
			e = network.getById(uuid);
		
		if (null != e)
			return e;
		
		try {
			e = storage.getById(uuid);
		} catch (StorageException e1) {
			Logger.error(WorkFlowProcessor.class, "Can't load entity with id " + uuid, e1);
		}
		
		return e;
	}



	
}
