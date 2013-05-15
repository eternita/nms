package org.neuro4j.logic.swf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.LogicProcessor;
import org.neuro4j.logic.LogicProcessorException;
import org.neuro4j.logic.LogicProcessorFactory;
import org.neuro4j.logic.LogicProcessorNotFoundException;
import org.neuro4j.logic.def.FlowSet;
import org.neuro4j.logic.def.StartNodeAdapter;
import org.neuro4j.xml.NetworkConverter;

/**
 * 
 * Runs flows stored on file system in XML files (.n4j extension)
 *
 */
public class SimpleWorkflowEngine {
	
	private static final String FLOW_FILE_EXTENSION = ".n4j";
	
	// flow cache
	private static Map<String, FlowSet> flowCache = Collections.synchronizedMap(new HashMap<String,FlowSet>());


	private static LogicProcessor logicProcessor = null;
	static {
		try {
			logicProcessor = LogicProcessorFactory.getLogicProcessor("org.neuro4j.logic.def.WorkFlowProcessor");
		} catch (LogicProcessorNotFoundException e) {
			e.printStackTrace();
		}		
	}
	
	public static LogicContext run(String flow) throws FlowExecutionException
	{
		return run(flow, null);
	}

	/**
	 * 
	 * @param flow - should be like package.name.FlowName-StartNode
	 * @param params
	 * @return
	 * @throws SimpleWorkflowException - if can't load flow, can't find start node, etc
	 */
	public static LogicContext run(String flow, Map<String, Object> params) throws FlowExecutionException
	{
		String[] fArr = parseFlowName(flow);
		String flowName = fArr[0];
		String startNode = fArr[1];
		
		FlowSet net = loadFlow(flowName); 
		if (null == net)
			throw new FlowExecutionException("Flow '" + flowName + "' can't be loaded");
		
		StartNodeAdapter startNodeAdapter = net.getStartNodeAdapter(startNode);
		if (null == startNodeAdapter)
			throw new FlowExecutionException("StartNode '" + startNode + "' not found in flow " + flowName);

		if (!net.isPublic()){
			throw new FlowExecutionException("Flow '" + flow + "' is not public");
		}

		if (!startNodeAdapter.isPublic()){
			throw new FlowExecutionException("Node '" + startNodeAdapter.getName() + "' is not public");
		}

		LogicContext logicContext = new LogicContext();
		if (null != params)
		{
			for (String key : params.keySet())
				logicContext.put(key, params.get(key));
		}
		
		logicContext.pushPackage(net.getFlowPackage());
		
		try {
			logicProcessor.action(startNodeAdapter.getEntity(), net.getNetwork(), null, logicContext);
			
			logicContext.popPackage();
			
		} catch (LogicProcessorException e1) {
			throw new FlowExecutionException(e1.getMessage());
		}	
		
		return logicContext;
	}
	

	
	public static String[] parseFlowName(String flow) throws FlowExecutionException
	{
		
		int separatorIdx = flow.indexOf("-"); 
		if (-1 == separatorIdx)
			throw new FlowExecutionException("Incorrect flow name. Must be package.name.FlowName-StartNode");
		// check > 1 "-"
		if (separatorIdx != flow.lastIndexOf("-"))
			throw new FlowExecutionException("Incorrect flow name. Must be package.name.FlowName-StartNode");
		
		String flowName = flow.substring(0, separatorIdx);
		// replace '.' -> '/'
		if (-1 < flowName.indexOf("."))
			flowName = flowName.replace('.', '/');

		String startNode = flow.substring(separatorIdx + 1);

		return new String[]{flowName, startNode};
	}
	
	/**
	 * Get flow by name. Should be without .n4j extension. E.g. package.name.FlowName
	 * 
	 * @param flowName
	 * @return
	 */
	public static FlowSet loadFlow(String flowName)
	{
		FlowSet net = null;
		synchronized (flowCache)
		{
			net = flowCache.get(flowName);
		}
		if (null != net)
			return net;
		
		net = loadFlowFromFS(flowName);
		
		if (null != net)
		{
			synchronized (flowCache)
			{
				flowCache.put(flowName, net);
			}
		}
				
		return net;
	}
	
	/**
	 * 
	 * @param fileName - eg "org/neuro4j/logic/swf/demo/lesson1/Hello.n4j"
	 * @return
	 */
	static FlowSet loadFlowFromFS(String flowName) {
		FlowSet flows = null;
		Network net = null;
		InputStream fis = SimpleWorkflowEngine.class.getClassLoader()
				.getResourceAsStream(flowName + FLOW_FILE_EXTENSION);
		;
		if (null != fis) {
			net = loadFlowFromFS(fis);
			if (net != null) {
				flows = new FlowSet(net, flowName);
			}
		}

		return flows;
	}
	
	public static Network loadFlowFromFS(InputStream is)
	{
		Network net = null;
		try {
			if (null != is)
				net = NetworkConverter.xml2network(is); 
		} finally {
			try {
				if (null != is)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return net;
	}	
	
	public static LogicProcessor getProcessor()
	{
		return logicProcessor;
	}

}
