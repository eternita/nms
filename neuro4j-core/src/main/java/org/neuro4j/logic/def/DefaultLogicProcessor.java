package org.neuro4j.logic.def;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.neuro4j.core.ERBase;
import org.neuro4j.core.Network;
import org.neuro4j.core.rel.DirectionRelation;
import org.neuro4j.logic.ExecutableEntityNotFoundException;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.LogicException;
import org.neuro4j.logic.LogicProcessor;
import org.neuro4j.logic.LogicProcessorException;
import org.neuro4j.logic.swf.FlowInitializationException;
import org.neuro4j.logic.swf.SWFConstants;
import org.neuro4j.logic.swf.SimpleWorkflowException;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;

public class DefaultLogicProcessor implements LogicProcessor
{

	private static final Logger logger = Logger.getLogger(DefaultLogicProcessor.class.getName());
	
	public DefaultLogicProcessor() {
		super();
	}

	public void init(Properties properties) throws LogicProcessorException {
		// TODO Auto-generated method stub		
	}
	
	/**
	 * 
	 * @param start Start node for execution
	 * @param network Network which Start Node belong to
	 * @param storage NeuroStorage which Start Node (and Network) belong to
	 * @param logicContext execution context
	 * @return
	 * @throws SimpleWorkflowException 
	 */
	public LogicContext action(ERBase start, Network network, Storage storage, LogicContext logicContext) throws SimpleWorkflowException
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
	
	private static ERBase actionImpl(ERBase currentStep, Network network, Storage storage, LogicContext logicContext) throws SimpleWorkflowException
	{
		if (null == logicContext)
			throw new RuntimeException("LogicContext must not be null");

		ERBase nextStep = null;
		String className = currentStep.getProperty(SWFConstants.SWF_BLOCK_CLASS);

		if (null != className)
		{
			// load it and execute
			LogicBlock logicNode;
			try {
				logicNode = LogicBlockLoader.getInstance().lookupBlock(currentStep, className);
				
				logger.finest("running " + logicNode.getClass().getSimpleName() + " (" +  logicNode.getClass().getCanonicalName() + ")");
				Set<ERBase> stack = getExecutionStack(logicContext);
				stack.add(currentStep);
				logicContext.put(SWFConstants.AC_CURRENT_NODE, currentStep);

				logicNode.process(logicContext);
				
			} catch (ExecutableEntityNotFoundException e1) {
				e1.printStackTrace();
			} catch (FlowInitializationException e) {
				e.printStackTrace();
				throw new SimpleWorkflowException(e.getLocalizedMessage());

			} catch (LogicException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		if (null != logicContext.get(SWFConstants.AC_NEXT_NODE_UUID))
		{
			String nextStepUUID = (String) logicContext.remove(SWFConstants.AC_NEXT_NODE_UUID);
			// TODO: in case remote storage - is loaded new entity (without relation to current network)
			nextStep = getEntityByUUID(nextStepUUID, network, storage);
		}

		if (null == nextStep)
			nextStep = getNext(currentStep, network, storage);
		
		return nextStep;
	}
	

	private static Set<ERBase> getExecutionStack(LogicContext logicContext)
	{
		Set<ERBase> stack = (Set<ERBase>) logicContext.get("ACTION_STACK");
		if (null == stack)
		{
			stack = new LinkedHashSet<ERBase>();
			logicContext.put("ACTION_STACK", stack);
		}
		return stack;
	}

	private static Set<Exception> getExeptionStack(LogicContext logicContext)
	{
		Set<Exception> stack = (Set<Exception>) logicContext.get(SWFConstants.AC_EXCEPTION_STACK);
		if (null == stack)
		{
			stack = new LinkedHashSet<Exception>();
			logicContext.put(SWFConstants.AC_EXCEPTION_STACK, stack);
		}
		return stack;
	}

	/**
	 * get params from properties
	 * 
	 * @param e
	 * @return
	 */
	private static Map<String, String> getSWFParameters(ERBase e)
	{
		Map<String, String> params = new HashMap<String, String>();
		for (String key : e.getPropertyKeys())
		{
			if (key.startsWith(SWFConstants.SWF_PARAM_PREFIX))
			{
				String paramName = key.substring(SWFConstants.SWF_PARAM_PREFIX.length());
				params.put(paramName, e.getProperty(key));
			}
			
		}
		return params;
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
		for (ERBase r : currentStep.getRelations(SWFConstants.NEXT_RELATION_NAME))
		{
			if (null != r)
			{
				String nextEid = r.getProperty(DirectionRelation.TO_KEY);
				if (null == nextEid) 
					continue;

				Set<ERBase> rparts = r.getAllParticipants(currentStep.getUuid());
				if (rparts.size() > 0)
				{
					ERBase rp = rparts.iterator().next();
					if (nextEid.equals(rp.getUuid()))
					{
						next = rp;
						
						// check if next has futher relations -> if not -> reload it from storage
						if (0 == next.getRelations(SWFConstants.NEXT_RELATION_NAME).size())
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
