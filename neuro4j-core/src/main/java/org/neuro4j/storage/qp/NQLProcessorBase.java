package org.neuro4j.storage.qp;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.neuro4j.NetworkUtils;
import org.neuro4j.core.ERBase;
import org.neuro4j.core.Network;
import org.neuro4j.core.Path;
import org.neuro4j.logic.LogicContext;
import org.neuro4j.logic.swf.FlowExecutionException;
import org.neuro4j.logic.swf.SimpleWorkflowEngine;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.storage.inmemory.qp.InMemoryNQLProcessorStreamQuery;
import org.neuro4j.storage.inmemory.qp.N4JSQLUtils;
import org.neuro4j.utils.N4JConfig;


public abstract class NQLProcessorBase implements NQLProcessor {

	protected static final int OUTPUT_NET_REQUEST_IDS_LIMIT = 500; // when building output net by matched paths / for pageable request from solr by ids

	protected Storage baseStorage = null; 

	// different behavior for queries like: 
	// select e[e_type='request'] / r[name='session-request'] / e[e_type='session'] / r[name='session-request'] / e[e_type='request']
	// TODO: allow to set it in query
	private static boolean ALLOW_REENTRANCE = false; 

	protected long outputNetworkLimit = -1;
	
	protected static final String CURRENT_MATCHED_PATHS = "CURRENT_MATCHED_PATHS"; 
	protected static final String FILTER_CLASS = "class"; 
	protected static final String FLOW = "flow"; 
	protected static final String TYPE = "type"; 
	protected static final String VIRTUAL = "virtual"; 
	protected static final String STORAGE = "storage";
	protected static final String CURRENT_ER_NETWORK = "CURRENT_ER_NETWORK"; 

	protected transient Logger logger = Logger.getLogger(getClass().getName());

	protected static final DateFormat TEMP_TABLE_DF = new SimpleDateFormat("yyyyMMdd_HHmmss");

	protected int parseCycle = FIRST_CYCLE;
	
	/**
	 * if NQL query strict or not
	 * 
	 * strict - retrieve all entities and relations defined in the query
	 * 
	 * not strict - do some generalization (MAX_CONNECTION_PER_EXPAND_LEVEL, MAX_QUERIED_CONNECTIONS_LIMIT, ...)
	 * some entities and relations may be omitted 
	 * 
	 */
	protected boolean strictQuery = false;

	protected Network outputNet = null;
	
	protected boolean READ_ONLY_QUERIES = false; 
	
	/**
	 * max connections each er(entity or relation) for expand operation 
	 * 
	 * it's for 1st, 2nd - div by 10 - 3rd - div by 100, ...
	 * 
	 */
	protected int MAX_CONNECTION_PER_EXPAND_LEVEL = 1000; 
	
	/**
	 * max connections each er(entity or relation) for expand operation
	 * Useful with big expand levels - because connections per expand level are decrease depends on expand level
	 */
	protected int MIN_CONNECTION_PER_EXPAND_LEVEL = 10; 
	
	/**
	 * int maxConnectionPerExpandLevelCount = MAX_CONNECTION_PER_EXPAND_LEVEL / (int) Math.pow(CONNECTION_PER_EXPAND_DECREASE_SPEAD, expandLevel - 1);
	 */
	protected int CONNECTION_PER_EXPAND_DECREASE_SPEAD = 10;	

	
	//  expand 1 ignore (1 name='country-previous-next'; 2 name='country-parent-child', name='b', chn_type=''; 3 test='')
	protected Map<String, Set<String>> ignoreAttrMap = new HashMap<String, Set<String>>();
	
	protected Map<String, Set<String>> useOnlyAttrMap = new HashMap<String, Set<String>>();

	protected ERType currentERType = null; // E or R
	protected boolean doSubpath = false; // e[]/r[]/...
	
	/**
	 * for serving filter clause e.g. filter(r[name='coin-belong-to-coin-definition'] 5, r[name='user-contributed-to-coin-definition'] 3)
	 */
	protected Set<Filter> filterSet = new LinkedHashSet<Filter>();
	

	// is used for path calculation e.g.
	// SELECT PATH E[first_name='John'] / R[name='brother-system'] / E[name='Marry'] / R[name='work at'] / E[]
	protected Network erSubPathPreviousNetwork = null;  

	
	// is used for queries with subpaths e.g. e[]/r[]/.....
	protected Set<Path> currentMatchedPaths = new HashSet<Path>();

	protected Network storageNet = null; // isn't changeable - can be used for querying from virtual entities/relations 
	protected Network pipeNet = null; // can change between pipes 

	protected NQLProcessorStream qpStream = null;

	
	public NQLProcessorBase() {
		super();
		String outputNetworkLimitStr = N4JConfig.getProperty("n4j.quering.nql.default_output_network_size_limit");
		if (null != outputNetworkLimitStr)
		{
			try {
				outputNetworkLimit = Long.parseLong(outputNetworkLimitStr);
			} catch (Exception e) {
				logger.severe("Can't get n4j.quering.nql.default_output_network_size_limit. Should be long");
			}
		}
	}

	public Network getNetwork() {
		
		if (null != outputNet)
		{
			for (ERBase er : outputNet.getERBases())
				er.setModified(false);
		}
			
		return outputNet;
	}

	public void setParseCycle(int cycle) {
		parseCycle = cycle;
	}

	/**
	 * called for pipe operator
	 * e.g. select e[] | select e[]
	 */
	public void startNewCommand()
	{
		if (FIRST_CYCLE == parseCycle)
			return;
		
		this.pipeNet = this.outputNet;
		
		// reset variables used by previous command
		resetVariables();
	}
	
	public void reset()
	{
		if (FIRST_CYCLE == parseCycle)
			return;
		
//		this.pipeNet = null;
		
		resetVariables();
	}
	
	/**
	 * reset variables used by previous command
	 */
	protected void resetVariables()
	{
		this.outputNet = null;
		this.filterSet.clear();
		this.ignoreAttrMap.clear();
		this.useOnlyAttrMap.clear();
		this.erSubPathPreviousNetwork = null;
		this.currentERType = null;
		this.doSubpath = false;
		this.currentMatchedPaths = new HashSet<Path>();
		this.qpStream = null;
		return;
	}
	
	
	/**
	 * SELECT E/R[...]
	 */
	public void startERAttributeProcessing(String erType) {
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		this.currentERType = ERType.valueOf(erType);
	}

	public void setStrict(boolean strict) {
		// function for second parse cycle only
		if (SECOND_CYCLE == parseCycle)
			return;
		
		this.strictQuery = strict;
		return;
	}

	
	public void setMaxOutputNetworkSize(long outputNetworkLimit) throws StorageException
	{
		// function for first parse cycle only
		if (SECOND_CYCLE == parseCycle)
			return;
		
		this.outputNetworkLimit = outputNetworkLimit;
	}
	


	public void addExpandIgnoreAttribute(int expandLevel, String attributeKey, String attributeValue)
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		Set<String> values = ignoreAttrMap.get(attributeKey);
		if (null == values)
		{
			values = new HashSet<String>();
			ignoreAttrMap.put(attributeKey, values);
		}
		values.add(attributeValue);
		return;
	}
	
	public void addExpandUseOnlyAttribute(int expandLevel, String attributeKey, String attributeValue)
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		Set<String> values = useOnlyAttrMap.get(attributeKey);
		if (null == values)
		{
			values = new HashSet<String>();
			useOnlyAttrMap.put(attributeKey, values);
		}
		values.add(attributeValue);
		return;
	}
	
	public void startERSubpath()
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		doSubpath = true;
	}	
	

	public void addFilter(String erType, String sKey, String sValue, int filterSize)
	{
		// function for first parse cycle only
		if (SECOND_CYCLE == parseCycle)
			return;
		
		Filter filter = new Filter();
		filter.erType = ERType.valueOf(erType);
		filter.propertyName = sKey;
		filter.propertyValue = sValue;
		filter.filterAmount = filterSize;
		filterSet.add(filter);
	}
	
	/**
	 * 
	 */
	public void doFilter()
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		Set<ERBase> toRemove = new HashSet<ERBase>();
		Set<ERBase> checkForRedundancy = new HashSet<ERBase>();
		
		for (Filter filter : filterSet)
		{
			String[] ids = null;
			
			switch (filter.erType) {
//			case relation:
//				ids = outputNet.getEntities();
//				break;

			case entity:
//				ids = outputNet.getRelations();
				ids = outputNet.getIds();
				break;

			default:
				break;
			}
			
			
			for (String erid : ids)
			{
				ERBase e = outputNet.getById(erid);
				int connectionCount = 0;
				Set<ERBase> connected = e.getConnected(filter.propertyName, filter.propertyValue);

				// sort by loaded connections from both sides desc
				Comparator<ERBase> comp = new Comparator<ERBase>() {	
					
					public int compare(ERBase er1, ERBase er2) {
						Integer size1 = 0, size2 = 0;
						
						for (ERBase er : er1.getConnected())
							size1 += er.getConnected().size();
						
						for (ERBase er : er2.getConnected())
							size2 += er.getConnected().size();
						
						return size2.compareTo(size1);
					};
				};
				List<ERBase> connectedSorted = new ArrayList<ERBase>(connected);
				Collections.sort(connectedSorted, comp);
				
				for (ERBase r : connectedSorted)
				{
					if (connectionCount >= filter.filterAmount)
					{
						toRemove.add(r);
						for (String id : r.getConnectedKeys(e.getUuid()))
							checkForRedundancy.add(outputNet.getById(id)); // er which has all deleted connections should be deleted 
					}
						
					connectionCount++;
				}
			}
		} // for (Filter filter : fSet)
		
		
		for(ERBase er : toRemove)
			outputNet.remove(er);
		
		toRemove.clear();
		
		
		for(ERBase er : checkForRedundancy)
		{
			if (null != er && er.getConnected().size() == 0)
				toRemove.add(er);
		}
		
		for(ERBase er : toRemove)
			outputNet.remove(er, true);
		
		return; 
	}
	

	public void sql(Map<String, String> params) throws StorageException
	{
		if (FIRST_CYCLE == parseCycle)
			return;

		logger.finest("-- DO SQL() " + params);
		
		Network net = null;
		
		Set<String> headers = N4JSQLUtils.getSQLTableHeaders(this.pipeNet);
		long currentTime = System.currentTimeMillis();
		String tableName = "T" + TEMP_TABLE_DF.format(new Date(currentTime)) + "_" + currentTime;
		
		Connection conn = null;
	    try {
			conn = N4JSQLUtils.getConnection(params);

			// create temp table
			N4JSQLUtils.createTempTable(conn, tableName, headers);
			
			// populate temp table
			for (String id : this.pipeNet.getIds())
			{
				ERBase er = this.pipeNet.getById(id);
				N4JSQLUtils.addER2Table(conn, er, tableName);
			}

			// query temp table
			String sqlQuery = params.get("q");
			if (null == sqlQuery)
			{
				sqlQuery = "SELECT * FROM " + tableName;
			} else {
				sqlQuery = sqlQuery.replaceAll("temp_table", tableName);
			}
			
			// query temp table
			net = N4JSQLUtils.queryTempTable(conn, tableName, sqlQuery);
			
			String jdbcURL = N4JConfig.getProperty("n4j.quering.sql.jdbc.url");
			if (null != params.get("jdbc"))
				jdbcURL = params.get("jdbc");
			
			// drop temp table (TODO: always drop for HQDB inmemory)
			String dropTable = params.get("drop_table");
			if (!"false".equalsIgnoreCase(dropTable) 
					|| -1 < jdbcURL.indexOf("jdbc:hsqldb:mem")) // always drop for in-memory hsqldb
			{
				// drop table - default behaviour
				N4JSQLUtils.dropTempTable(conn, tableName);
			} else {
				// leave table 
			}
			
		} catch (SQLException e) {
			throw new StorageException("Can't run SQL command with params: " + params + "; " + e.getMessage());
		} finally {
			if (null != conn)
			{
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	
		// move input net to output
		this.outputNet = net;
	}
	
	protected void virtualERProcessing(Network currentERNetwork, Map<String, String> techParams) throws StorageException
	{
	
		techParams.remove(TYPE);
		String flow = (String) techParams.remove(FLOW);
				
		Map<String, Object> ctxParams = new HashMap<String, Object>();
		ctxParams.putAll(techParams);

		ctxParams.put(CURRENT_MATCHED_PATHS, currentMatchedPaths);
		ctxParams.put(STORAGE, storageNet);
		ctxParams.put(CURRENT_ER_NETWORK, currentERNetwork);
		
		try {
			LogicContext logicContext = SimpleWorkflowEngine.run(flow, ctxParams);
		} catch (FlowExecutionException e) {
			throw new StorageException("Exception during call " + flow + "; " + e.getMessage(), e);
		}
		
		return;
	}
	
	public void behave(Map<String, String> params) throws StorageException
	{
		if (FIRST_CYCLE == parseCycle)
			return;

		String flow = (String) params.get("flow");// "org.neuro4j.data.mining.demo.WebMining-Start"; 
		if (null == flow)
		{
			//TODO: log ?
			return;
		}
		params.remove("flow");

		
		logger.fine("Running flow " + flow);

		Map<String, Object> ctxParams = new HashMap<String, Object>();
		ctxParams.putAll(params);
		ctxParams.put("INPUT_NETWORK", pipeNet);
		ctxParams.put("CURRENT_STORAGE", baseStorage);
		
		
		try {
			LogicContext logicContext = SimpleWorkflowEngine.run(flow, ctxParams);

		} catch (FlowExecutionException e) {
			logger.warning("Error during execution flow " + flow);
			throw new StorageException("Error during execution flow " + flow, e);
		} catch (Exception e) {
			logger.warning("Error during execution flow " + flow);
			throw new StorageException("Error during execution flow " + flow, e);
		}
				
		// move input net to output
		this.outputNet = this.pipeNet;
		
		// if behave in the first pipe - outputNet and pipeNet is Null
		if (null == this.outputNet)
			this.outputNet = new Network();
		
		return;
	}

	/**
	 * select e[a='b' AND CONNECTED(name='friends' and connected(name='Brad' and connected(...)))] 
	 * get source ids for first CONNECTED in er[...]
	 * 
	 * TODO: can be moved to base
	 * 
	 * @return
	 * @throws StorageException
	 */
	public Set<Path> getDefaultConnectedStack() throws StorageException {
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return null;
		
		Set<Path> connectedStack = new HashSet<Path>();

		if (null == outputNet) /* check if first expression*/
		{
			// if first expression -> copy all entities or relations
//			srcids = new HashSet<String>(); 
			switch (currentERType)
			{
			case entity:
//				for (String id : storageNet.getEntities())
//				{
//					connectedStack.add(new Path(id));
//				}
//				break;
				
//			case relation:
//				for (String id : storageNet.getRelations())
//				{
//					connectedStack.add(new Path(id));
//				}
				
				for (String id : storageNet.getIds())
				{
					connectedStack.add(new Path(id));
				}

				break;
				
			default:
					throw new RuntimeException("Wring ERType " + currentERType);
			}
		} else {
			for (String id : NetworkUtils.getPathsEnds(currentMatchedPaths))
				connectedStack.add(new Path(id));

		}
		
		return connectedStack;
	}
	
	/**
	 * connectedStack 
	 *  - empty - 1st call (before 1st connected())
	 *  - 
	 * filterNet is optional - for performance optimization - in case AND with for previous parameters - e.g. n4j_demo='true' AND connected(...)
	 * 
	 * @param connectedStack
	 * 
	 * @return
	 * @throws StorageException
	 */
	public void updateConnectedStack(Set<Path> connectedStack, Network filterNet) throws StorageException {
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;

		Set<Path> newPaths = new HashSet<Path>();
		Set<Path> paths2delete = new HashSet<Path>();
		
		for (Path p : connectedStack)
		{
			String pathEndId = p.getLast();
			
			ERBase pathEnd = storageNet.getById(pathEndId);
			
			if (null == pathEnd)
				continue;
			
			if (pathEnd.getConnectedKeys().size() == 0)
			{
				paths2delete.add(p);
				continue;
			}
			
			for (String connectedId : pathEnd.getConnectedKeys())
			{
//				if (!ALLOW_REENTRANCE)
//				{   // check for re-entrance
//					if (p.contains(connectedId))
//						continue;
//				}
				if (!storageNet.contains(connectedId))
					continue;
				
				if (null != filterNet && !filterNet.contains(connectedId))
					continue;

					
				newPaths.add(p.clone().add(connectedId));

			} // for (String connectedId : pathEnd.getConnectedKeys())
				
		} // for (Path p : connectedStack)
		
		connectedStack.clear();
		connectedStack.addAll(newPaths);

		return;
	}

	public Network finishGetConnected(Set<Path> connectedStack, Network connectedNet) throws StorageException
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return null;
		
		Network outnet = new Network(); 
//		Set<String> srcids = NetworkUtils.getPathsEnds(connectedStack);
		// TODO: should be used recursion-deep
		Set<String> srcids = new HashSet<String>();
		for (Path p : connectedStack)
			srcids.addAll(p.getItems());
		
		for (String eid : srcids)
		{
			ERBase src = storageNet.getById(eid);
			if (null == src)
				continue;
			
			for (String cid : connectedNet.getIds())
			{
				if (src.isConnectedTo(cid))
				{
					outnet.add(src);
				}
			}
		}
		
		return outnet;
	}
	
	public Network getConnected(Set<Path> connectedStack, Map<String, String> params) throws StorageException {
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return null;
		
		Network outnet = new Network(); // or just set ?
		
		String key = (String) params.get("key");

		if ("id".equalsIgnoreCase(key))
			key = "UUID";

		String comparator = (String) params.get("comparator");
		String value = (String) params.get("value");

			
		Set<String> srcids = NetworkUtils.getPathsEnds(connectedStack);
		
		for (String eid : srcids)
		{
			ERBase src = storageNet.getById(eid);
			if (null == src)
				continue;
			
			for (String connectedId : src.getConnectedKeys())
			{
				ERBase connected = storageNet.getById(connectedId);
				if (null != connected && value.equals(connected.getProperty(key)))
				{
//					outnet.add(src.cloneWithConnectedKeys());
					outnet.add(connected.cloneWithConnectedKeys());
				}
			} // for (ERBase connected : src.getConnected())
		} // for (ERBase src : src4connected)

//		System.out.println("processor -> getConnected() ");
		return outnet;
	}
	

	public Set<Path> checkMatchedPaths(Set<Path> matchedPaths, String[] newERIds)
	{
		Set<Path> newMatchedPaths = new HashSet<Path>();
		
		// check / update Paths
		for (String newERId : newERIds)
		{
			ERBase newSubnetERBase = storageNet.getById(newERId);
			for (Path p : matchedPaths)
			{
				if (newMatchedPaths.contains(p)) // one update for path only
				{
					 p = p.clone();
					 p.removeLast();
				}
				
				String lastId = p.getLast();
				ERBase lastER = outputNet.getById(lastId); // last er in the path can be virtual 
				if (newSubnetERBase.isConnectedTo(lastId)
						|| (lastER.isVirtual() && lastER.isConnectedTo(newERId))) // 
				{
					if (!ALLOW_REENTRANCE)
					{   // check for re-entrance
						if (p.contains(newERId))
							continue;
					}

					p.add(newERId);
					newMatchedPaths.add(p);
				}
			}
		}
		
		return newMatchedPaths;
	}
	
	/**
	 * in-memory implementation. is used by other implementations (NQLProcessorSolr in case of pipe functionality )
	 * 
	 * @param currentERNetwork
	 * @param techParams
	 * @throws StorageException
	 */
	protected void finishERAttributeProcessingInMemoryImpl(Network currentERNetwork, Map<String, String> techParams, boolean optional) throws StorageException
	{
		
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		Set<ERBase> currentERs = new HashSet<ERBase>();
		if (null == currentERNetwork)
		{
			currentERNetwork = new Network();
			for (ERBase er : this.pipeNet.getERBases())
			{
/*				switch (currentERType)
				{
				case entity:
					if (er instanceof Entity)
						currentERs.add(er);
					break;
				case relation:
					if (er instanceof Relation)
						currentERs.add(er);
					break;
				}				
*/
				currentERs.add(er);
			}
		} else {
			for (ERBase er : currentERNetwork.getERBases())
				currentERs.add(er);
		}
		
		
		if (null == qpStream)
		{
			qpStream = new InMemoryNQLProcessorStreamQuery(currentERs, this.pipeNet, this.filterSet, currentERType, currentMatchedPaths, qpStream, optional, outputNetworkLimit);
		} else {
			qpStream = new InMemoryNQLProcessorStreamQuery(currentERs, this.pipeNet, this.filterSet, qpStream, optional, this.useOnlyAttrMap, this.ignoreAttrMap, outputNetworkLimit);
		}

		/*
		// check for technical parameters e.g. [type='virtual' flow='']
		if (null != techParams && techParams.size() > 0)
		{

			if (VIRTUAL.equalsIgnoreCase((String) techParams.get(TYPE))
					&& null != techParams.get(FLOW))
			{
				if (null == currentERNetwork)
					currentERNetwork = new Network();
				
				virtualERProcessing(currentERNetwork, techParams);
			}
		}
		
//		ERType previousERType = null;
		String[] preriousIds = null;
		if (doSubpath) // e[]/r[]/.....
		{
			switch (currentERType)
			{
			case entity:
//				previousERType = ERType.relation;
				if (null != erSubPathPreviousNetwork)
					preriousIds = erSubPathPreviousNetwork.getRelations();
				break;
			case relation:
//				previousERType = ERType.entity;
				if (null != erSubPathPreviousNetwork)
					preriousIds = erSubPathPreviousNetwork.getEntities();
				break;
				default:
					throw new RuntimeException("Wring ERType " + currentERType);
			}
			
		}

		if (null != preriousIds && 0 == preriousIds.length) {
			// we had previous query and it returned empty result set - no reason to run another query
			currentERNetwork = new Network();
		} else {
			// do nothing
			// live currentERNetwork as it is
		}

	    //  empty e[] or r[] means get all
		if (null == currentERNetwork)
		{
//			currentERNetwork = pipeNet.copy();
			// TODO: ! DO NOT MODIFIY currentERNetwork til end of this method
			// if need to modify -> use pipeNet.copy();
			currentERNetwork = pipeNet;
		}

		// for previousIds in subpath calculation
		erSubPathPreviousNetwork = currentERNetwork; 

		if (null == outputNet)
		{
			outputNet = currentERNetwork; // first expression
			// create Paths
			for (String erid : currentERNetwork.getERBaseIds())
			{
				currentMatchedPaths.add(new Path(erid));
			}
		} else {
			currentMatchedPaths = checkMatchedPaths(currentMatchedPaths, currentERNetwork.getERBaseIds());
			
			if (0 == currentERNetwork.getSize())  // check if nothing found in subpath
			{
				outputNet = currentERNetwork; // nothing found - reset output net
			} else {
//				outputNet = NetworkUtils.sumNetworks(outputNet, currentERNetwork);
				// filter out by paths match
				// eg select e[e_type='request'] / r[name='session-request'] / e[ uuid='3137AE49981EA7CB0BF4320FC8CC9408.lb1']
//				outputNet = NetworkUtils.filterByPathList(outputNet, currentMatchedPaths);
				outputNet = NetworkUtils.filterByPathList(storageNet, currentMatchedPaths);
			}
		}
//*/		
		return;
	}
	
	
	protected void recursiveERSubpathInMemoryImpl(Map<String, String> params) throws StorageException
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return;
		
		String depthStr = null;
		int depth = 0;
		
		try {
			depthStr = params.get("depth");
			depth = Integer.parseInt(depthStr);

			
			Set<ERBase> currentERs = new HashSet<ERBase>();
			for (ERBase er : this.pipeNet.getERBases())
				currentERs.add(er);
			
			for (int i = 0; i < depth; i++)
				qpStream = new InMemoryNQLProcessorStreamQuery(currentERs, this.pipeNet, this.filterSet, qpStream, true, this.useOnlyAttrMap, this.ignoreAttrMap, outputNetworkLimit);
						
		} catch (Exception ex) {
			logger.severe("Wrong depth");
			throw new StorageException("Wrong depth " + depthStr);
		}
		
		return;
	}	
	
	protected Network finishERParseInMemoryImpl() throws StorageException
	{
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return null;
		
		outputNet = new Network();
		
		long netSizeCont = 0;
		// check for output limit
		while (qpStream.hasNext())
		{
			qpStream.next();
			netSizeCont++;
			
			if (-1 < outputNetworkLimit && netSizeCont >= outputNetworkLimit)
				break;
			
		}
		
		currentMatchedPaths = qpStream.getCurrentMatchedPaths(); 
		Set<String> netIds = new HashSet<String>();
		// filter out duplicates
		for (Path p : currentMatchedPaths)
		{
			// if (p.getSize() == qpStream.getDepthLevel() + 1) // check current depth level // no longer - we support optional elements in query
			{
				for (String s : p.getItems())
				{
					netIds.add(s);
					if (-1 < outputNetworkLimit && netIds.size() >= outputNetworkLimit)
						break;
				}
			}
			
			if (-1 < outputNetworkLimit && netIds.size() >= outputNetworkLimit)
				break;
		}
		

		// create net from matched paths
		for (String id : netIds)
		{
			ERBase er = this.storageNet.getById(id);
			outputNet.add(er.cloneWithConnectedKeys());

		}
		
		return outputNet;
	}

	
	protected Network addERAttributeInMemoryImpl(Map<String, String> params) throws StorageException {
		// function for second parse cycle only
		if (FIRST_CYCLE == parseCycle)
			return null;
		
		Network currentERNetwork = new Network(); 
				
		String key = (String) params.get("key");

		if ("id".equalsIgnoreCase(key))
			key = "UUID";

		String comparator = (String) params.get("comparator");
		String value = (String) params.get("value");
		
//		logger.info("-- " + key + comparator + value);
		if ("=".equals(comparator))
		{
			switch (currentERType) 
			{
			case entity:
//				for (Entity e : pipeNet.getEntities(key, value)) {
//					currentERNetwork.add(e.cloneWithConnectedKeys());
//				}
//				break;
				
//			case relation:
//				for (Relation r : pipeNet.getRelations(key, value)) {
//					currentERNetwork.add(r.cloneWithConnectedKeys());
//				}

				for (ERBase r : pipeNet.get(key, value)) {
				currentERNetwork.add(r.cloneWithConnectedKeys());
				}
				break;
				
			default:
				throw new RuntimeException("Wring ERType " + currentERType);
			}			
		      
		} else if ("like".equalsIgnoreCase(comparator)) {
			// process like
			switch (currentERType) 
			{
			case entity:
//				for (Entity e : pipeNet.getEntitiesByRegexp(key, value)) {
//					currentERNetwork.add(e.cloneWithConnectedKeys());
//				}
//
//				break;
//			case relation:
//				for (Relation r : pipeNet.getRelationsByRegexp(key, value)) {
//					currentERNetwork.add(r.cloneWithConnectedKeys());
//				}

				for (ERBase r : pipeNet.getByRegexp(key, value)) {
					currentERNetwork.add(r.cloneWithConnectedKeys());
				}
				break;
			default:
				throw new RuntimeException("Wring ERType " + currentERType);
			}			
			
		} else {
			throw new RuntimeException("Wrong comparator " + comparator);
		}
		
		return currentERNetwork;
	}


//	protected ERType getOpositeER(ERType in)
//	{
//		switch (in)
//		{
//		case entity:
//			return ERType.relation;
//		case relation:
//			return ERType.entity;
//		}
//		
//		return in;
//	}

	public void insert(Map<String, String> params) throws StorageException
	{
		if (FIRST_CYCLE == parseCycle)
			return;
		
		if (READ_ONLY_QUERIES)
			throw new StorageException("Storage is run in read only mode");
		
		ERBase er = new ERBase();

/*		ERType erType = ERType.valueOf(ertype);
		switch (erType) {
//		case relation:
//			er = new Relation();
//			break;

		case entity:
//			er = new Entity();
			er = new ERBase();
			break;

		default:
			throw new StorageException("Wrong ER type " + ertype);
		}
*/		
		// set properties
		for (String key : params.keySet())
			er.setProperty(key, params.get(key));
		
		pipeNet = new Network();
		pipeNet.add(er);
		outputNet = pipeNet;
		
		// save to persistence storage (if any)
		if (null != baseStorage)
			baseStorage.save(pipeNet);

		return;
	}	
	
	public void update(Network updateNet, 
			Map<String, String> setProperties, Set<String> removeProperties, 
			Network addConnections, Network removeConnections) throws StorageException
	{
		if (FIRST_CYCLE == parseCycle)
			return;
		
		if (READ_ONLY_QUERIES)
			throw new StorageException("Storage is run in read only mode");

		if (null == updateNet)
			return;
		
		// during update connected should be in the same net
		// after update connected are removed from output net (so user will see list in output)
		Network net4update = new Network();
		
		for (ERBase er : updateNet.getERBases())
		{
			net4update.add(er);
			for (String key : setProperties.keySet())
				er.setProperty(key, setProperties.get(key));
			
			
			for (String key : removeProperties)
				er.removeProperty(key);
			
			if (null != addConnections)
			{
				for (ERBase connected : addConnections.getERBases())
				{
					net4update.add(connected);
//					if (er.getClass().equals(connected.getClass()))
//						throw new StorageException("Wrong UPDATE clause (add connected)");
					
/*					if (er instanceof Entity)
						((Entity) er).addRelation((Relation) connected);
					
					else if (er instanceof Relation)
						((Relation) er).addParticipant((Entity) connected);
*/					
					er.addConnected(connected);

				}
			} // if (null != addConnections)
			
			if (null != removeConnections)
			{
				for (ERBase connected : removeConnections.getERBases())
				{
					net4update.add(connected);
					if (er.isConnectedTo(connected.getUuid()))
					{
/*						if (er instanceof Entity)
							((Entity) er).removeRelation(connected.getUuid());
						
						else if (er instanceof Relation)
							((Relation) er).removeParticipant(connected.getUuid());
*/						
						er.removeConnected(connected.getUuid());
					}
				}
			} // if (null != removeConnections)
			
		}

		pipeNet = updateNet;
		outputNet = pipeNet;

		// save to persistence storage (if any)
		if (null != baseStorage)
			baseStorage.save(net4update);

			
		return;
	}	
	
}