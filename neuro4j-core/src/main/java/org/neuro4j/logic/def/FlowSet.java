package org.neuro4j.logic.def;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.logic.def.node.StartBlock;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.SWFConstants;
import org.neuro4j.logic.swf.SWFParametersConstants;
import org.neuro4j.logic.swf.enums.NetworkVisibility;
import org.neuro4j.storage.NQLException;

public class FlowSet {
	
	private Network network;
	
	private NetworkVisibility visibility;
	
	private String flowPackage;
	
	public FlowSet(Network network, String flowPackage)
	{
		this.network = network;
		this.flowPackage = flowPackage;
		updateVisibility(network);
	}

	private void updateVisibility(Network network){
		Entity config =  network.getEntityByName("networkConfig");
		if (config != null)
		{
			String visibilityStr =  config.getProperty(SWFParametersConstants.NETWORK_VISIBILITY);
			if (visibilityStr != null)
			{
				visibility = NetworkVisibility.valueOf(visibilityStr);
			}
		}
		if (visibility == null)
		{
			visibility = NetworkVisibility.getDefault();
		}
	}
	
	public Network getNetwork() {
		return network;
	}

	public NetworkVisibility getVisibility() {
		return visibility;
	}
	
	public boolean isPublic()
	{
		return visibility == NetworkVisibility.Public; 
	}

	public Entity getEntityByName(String startNode) 
	{
		return network.getEntityByName(startNode);
	}

	public Network query(String q) throws NQLException {
		return network.query(q);
	}
	
	public StartNodeAdapter getStartNodeAdapter(String startNodeName) throws FlowExecutionException
	{
		Entity startNodeEntity = this.network.getEntityByName(startNodeName);
		
		if (startNodeEntity == null)
		{
			throw new FlowExecutionException("Start Node: " + startNodeName + " not found in package " + flowPackage);
				
		}
		String nodeClass = startNodeEntity.getProperty(SWFConstants.SWF_BLOCK_CLASS);
		
		if (!StartBlock.class.getName().toString().equals(nodeClass))
		{
			throw new FlowExecutionException("Node " + startNodeEntity.getName() + " in package " + flowPackage + " is not StartNode");
				
		}
		StartNodeAdapter startNode = new StartNodeAdapter(startNodeEntity, flowPackage);
		
		return startNode;
	}
	
	public String getFlowPackage()
	{
		return flowPackage;
	}
	

}
