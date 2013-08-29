package org.neuro4j.storage.qp;

import java.util.Map;
import java.util.Set;

import org.neuro4j.core.ERBase;
import org.neuro4j.core.Network;
import org.neuro4j.core.Path;
import org.neuro4j.storage.StorageException;

/**
 * 
 * TODO: move me to core
 *
 */
public interface NQLProcessor {
	public final static int FIRST_CYCLE = 1;
	
	public final static int SECOND_CYCLE = 2;

	public final int MAX_EXPAND_LEVEL = 100; 
	
	/**
	 * 
	 * Max fully loaded connections (e.g. relations per relation group name)
	 * 
	 * E.g. Imagine user uploaded 1000 photos (have 1000 relations). 
	 * Queried network will have MAX_QUERIED_CONNECTIONS_LIMIT loaded relations for user object (others - just ids)
	 * 
	 * MAX_QUERIED_CONNECTIONS_LIMIT is used for single entity / relation 
	 * 
	 */
	public final static int DEFAULT_MAX_QUERIED_CONNECTIONS_LIMIT = 40; 

	
	/**
	 * called for pipe operator
	 * e.g. select e[] | select e[]
	 */
	public void startNewCommand() throws StorageException;

	public void setParseCycle(int cycle);
	
	public Network addERAttribute(Map<String, String> params) throws StorageException;

	public void startERAttributeProcessing(String erType) throws StorageException;
	
	public void startERSubpath() throws StorageException;

	public void finishERAttributeProcessing(Network currentERNetwork, Map<String, String> techParams, boolean optional) throws StorageException;

	public Network finishERParse() throws StorageException;

	public Set<Path> getDefaultConnectedStack() throws StorageException;
	
	public void updateConnectedStack(Set<Path> connectedStack, Network filterNet) throws StorageException;

	public Network getConnected(Set<Path> connectedStack, Map<String, String> params) throws StorageException;
	
	public Network finishGetConnected(Set<Path> connectedStack, Network connectedNet) throws StorageException;

	/**
	 * call on first (
	 * 
	 * E/R[(a='1' AND b='2')]
	 */
	public void startERAttributeExpression(Map<String, String> params) throws StorageException;
	
	/**
	 * str - AND | OR
	 * 
	 * E/R[(a='1' AND b='2')]
	 */	
	public void addERAttributeExpression(String str) throws StorageException; // AND | OR

	public Network finishERAttributeExpression(Map<String, Object> params) throws StorageException;
	
	public Network doSimpleERAttributeExpression(Map<String, Object> params) throws StorageException;
	
	public Network getNetwork() throws StorageException;
	
	public void behave(Map<String, String> params) throws StorageException;
	
	public void insert(Map<String, String> params) throws StorageException;

	public void update(Network updateNet, Map<String, String> setProperties, Set<String> removeProperties, Network addConnections, Network removeConnections) throws StorageException;
	
	public void reset() throws StorageException;
	
	public void sql(Map<String, String> params) throws StorageException;
	
	public void addExpandIgnoreAttribute(int expandLevel, String attributeKey, String attributeValue) throws StorageException;
	
	public void addExpandUseOnlyAttribute(int expandLevel, String attributeKey, String attributeValue) throws StorageException;
	
	
	

	public void addFilter(String erType, String sKey, String sValue, int filterSize) throws StorageException;

	
	/**
	 * 
	 */
	public void doFilter() throws StorageException;

	
	/**
	 * 
	 */
	public void finishDelete() throws StorageException;
	

	/**
	 * 
	 */
	public void setStrict(boolean strict) throws StorageException;
	
	/**
	 * for get path between
	 * 
	 * @param id
	 * @return
	 */
	public ERBase getById(String id) throws StorageException;
	
	
	public void setMaxOutputNetworkSize(long outputNetworkLimit) throws StorageException;
	

	public void recursiveERSubpath(Map<String, String> params) throws StorageException;
//-------------------------------------	
}
