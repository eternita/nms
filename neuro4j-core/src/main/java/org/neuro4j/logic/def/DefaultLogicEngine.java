package org.neuro4j.logic.def;

import java.util.Map;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.LogicProcessor;
import org.neuro4j.logic.LogicProcessorException;
import org.neuro4j.logic.LogicProcessorFactory;
import org.neuro4j.logic.LogicProcessorNotFoundException;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;

/**
 * 
 * Runs flows stored in single storage
 *
 */
public class DefaultLogicEngine {
	
	public static LogicProcessor logicProcessor = null;
	static {
		try {
			logicProcessor = LogicProcessorFactory.getLogicProcessor("org.neuro4j.logic.def.DefaultLogicProcessor");
		} catch (LogicProcessorNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @param flow  is EntityId or NQL to get entity with start node (name='Start')
	 * @param storage
	 * @return
	 * @throws LogicProcessorException
	 */
	public static LogicContext run(String flow, Storage storage) throws LogicProcessorException
	{
		return run(flow, null, storage, null);
	}
	
	/**
	 * 
	 * @param flow  is EntityId or NQL to get entity with start node (name='Start')
	 * @param storage
	 * @param params
	 * @return
	 * @throws LogicProcessorException
	 */
	public static LogicContext run(String flow, Storage storage, Map<String, Object> params) throws LogicProcessorException
	{
		return run(flow, null, storage, params);
	}
	
	/**
	 * 
	 * @param flow  is EntityId or NQL to get entity with start node (name='Start')
	 * @param startNode
	 * @param storage
	 * @param params
	 * @return
	 * @throws LogicProcessorException
	 */
	public static LogicContext run(String flow, String startNode, Storage storage, Map<String, Object> params) throws LogicProcessorException
	{
		Entity e = null;
		Network net = null;
		if (-1 < flow.toLowerCase().indexOf("select"))
		{
			try {
				net = storage.query(flow);
			} catch (StorageException e1) {
				throw new LogicProcessorException("Can't execute flow query " + flow + "' in storage " + storage);
			} catch (NQLException e1) {
				throw new LogicProcessorException("Can't execute flow query " + flow + "' in storage " + storage + ". Wrong NQL: " + e1.getMessage());
			}
			if (null == startNode)
				startNode = "Start";
			e = net.getEntityByName(startNode);
		} else {
			String startNodeId = flow;
			try {
				e = storage.getEntityByUUID(startNodeId);
			} catch (StorageException e1) {
				throw new LogicProcessorException("Node '" + startNodeId + "' not found in storage " + storage);
			}
		}
		
		
		LogicContext logicContext = new LogicContext();
		if (null != params)
			for (String key : params.keySet())
				logicContext.put(key, params.get(key));

		logicProcessor.action(e, net, storage, logicContext);	
		
		return logicContext;

	}


}
