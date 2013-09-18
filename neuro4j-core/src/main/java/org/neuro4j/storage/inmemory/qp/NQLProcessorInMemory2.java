package org.neuro4j.storage.inmemory.qp;

import java.util.Map;

import org.neuro4j.NetworkUtils;
import org.neuro4j.core.Connected;
import org.neuro4j.core.Network;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.storage.inmemory.InMemoryUtils;
import org.neuro4j.storage.qp.ERExpression;
import org.neuro4j.storage.qp.NQLProcessorBase;
public class NQLProcessorInMemory2 extends NQLProcessorBase {	

	public NQLProcessorInMemory2(Network inputNetwork, Storage baseStorage) {
		super();
		this.baseStorage = baseStorage;
		this.storageNet = inputNetwork;
		this.pipeNet = inputNetwork;
	}

	
	public Network addERAttribute(Map<String, String> params) throws StorageException {

		return addERAttributeInMemoryImpl(params);
	}
	

	public void finishERAttributeProcessing(Network currentERNetwork, Map<String, String> techParams, boolean optional) throws StorageException
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		finishERAttributeProcessingInMemoryImpl(currentERNetwork, techParams, optional);
		
		return;
	}

	/**
	 * call on first (
	 * 
	 * E/R[(a='1' AND b='2')]
	 */
	public void startERAttributeExpression(Map<String, String> params) throws StorageException {
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		// is not used for InMemoryStorage
	}

	/**
	 * str - AND | OR
	 * 
	 * E/R[(a='1' AND b='2')]
	 */
	public void addERAttributeExpression(String str) throws StorageException {
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		// is not used for InMemoryStorage
		// passed to finishERAttributeExpression() as parameter
		// String operandStr = (String) params.get("operand");
	}

	public Network finishERAttributeExpression(Map<String, Object> params) throws StorageException {
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return null;
		
		return null;
	}
	
	/**
	 * 
	 * 
	 */
	public Network doSimpleERAttributeExpression(Map<String, Object> params) throws StorageException {
		
		Network net1 = (Network) params.get("net1");
		Network net2 = (Network) params.get("net2");
		String operandStr = (String) params.get("operand");
		ERExpression operand = ERExpression.valueOf(operandStr);

		Network net = null;
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return null;
		
		if (null == net2)
			net = net1;

		switch (operand) {
		case AND:
			net = InMemoryUtils.andNetworks(net1, net2);
			break;

		case OR:
			net = NetworkUtils.sumNetworks(net1, net2); 
			break;

		default:
			throw new RuntimeException("Wring ERExpression " + operand);
		}
		
		return net;
	}

	
	public Connected getById(String id) throws StorageException
	{
		Connected er = this.pipeNet.getById(id);
		if (null != er)
			return er.cloneWithConnectedKeys();
		
		return null;
	}

	public void finishDelete() throws StorageException {
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		if (READ_ONLY_QUERIES)
			throw new StorageException("Storage is run in read only mode");
		

		
		for (String id : outputNet.getIds())
		{
			Connected er = pipeNet.getById(id);
			if (!er.isCompleteLoaded())
				NetworkUtils.loadConnected(er, this.pipeNet, baseStorage);
			this.pipeNet.remove(er);
		}
		
		return;
	}

	public void recursiveERSubpath(Map<String, String> params) throws StorageException
	{
		recursiveERSubpathInMemoryImpl(params);
		
		return;
	}
	
	public Network finishERParse() throws StorageException
	{
		return finishERParseInMemoryImpl();
	}
	
	

}
