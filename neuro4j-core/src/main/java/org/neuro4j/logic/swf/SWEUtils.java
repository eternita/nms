package org.neuro4j.logic.swf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.neuro4j.core.Connected;
import org.neuro4j.core.Network;
import org.neuro4j.core.rel.DirectionRelation;
import org.neuro4j.logic.def.FlowSet;
import org.neuro4j.storage.NQLException;

public class SWEUtils {
	
	private static final String SELECT_START_NODE_QUERY = "SELECT [SWF_BLOCK_CLASS='org.neuro4j.logic.def.node.StartBlock']";

	/**
	 * Returns list with start node ids for the specified flow.
	 * 
	 * @param flowName (e.g. org.neuro4j.logic.swf.demo.lesson2.Flow1)
	 * @return
	 */
	public static List<String> getStartNodeList(String flowName)
	{
		List<String> ids = new ArrayList<String>();
		
		// replace '.' -> '/'
		if (-1 < flowName.indexOf("."))
			flowName = flowName.replace('.', '/');

		FlowSet flowNet = SimpleWorkflowEngine.loadFlow(flowName);
		if (null != flowNet)
		{
			try {
				Network net = flowNet.query(SELECT_START_NODE_QUERY);
				
				for (String eid : net.getIds())
					ids.add(eid);
				
			} catch (NQLException e) {
				e.printStackTrace();
			}
		}
		
		return ids;
	}
	
	/**
	 * Returns list with start node name for the specified flow.
	 * 
	 * @param is input stream with flow's network
	 * @return
	 */
	public static List<String> getStartNodeList(InputStream is)
	{
		List<String> startNodeList = new ArrayList<String>();
		Network flowNet = SimpleWorkflowEngine.loadFlowFromFS(is);
		if (null != flowNet)
		{
			try {
				Network net = flowNet.query(SELECT_START_NODE_QUERY);
				
				for (String eid : net.getIds())
					startNodeList.add(net.getById(eid).getName());
				
			} catch (NQLException e) {
				e.printStackTrace();
			}
		}
		
		return startNodeList;
	}
	
	
	
	/**
	 * Get outgoing relations
	 * 
	 * @param block
	 * @return
	 */
	public static List<Connected> getOutgoingRelations(Connected block)
	{
		List<Connected> outRelations = new ArrayList<Connected>();
		
		for (Connected r : block.getConnected()) // TODO: rework with (type='relations') ..
		{
			if (block.getUuid().equals(r.getProperty(DirectionRelation.FROM_KEY)))
				outRelations.add(r);
		}
		
		return outRelations;
	}
	
	public static String[] getMappedParameters(String value)
	{
		String[] splitted = new String[2];
		
        if (value != null)
        {
        	String[] s = value.trim().split(SWFConstants.PARAMETER_DELIMITER);
        	if (s != null && s.length > 1)
        	{
        		splitted[0] = s[0];
        		splitted[1] = s[1];
        	} else if (s != null && s.length == 1)
        	{
        		splitted[0] = s[0];
        		splitted[1] = s[0];
        	}
        }
		
		return splitted;
	}
	

}
