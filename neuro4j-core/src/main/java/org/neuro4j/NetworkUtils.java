package org.neuro4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neuro4j.core.Connected;
import org.neuro4j.core.Network;
import org.neuro4j.core.Path;
import org.neuro4j.storage.Storage;

public class NetworkUtils {

	/**
	 * Merge entities from net2 to net1
	 * 
	 * If net1 & net2 have entity with the same uuid (but different objects) will survive (will be returned) which is in net1 
	 * 
	 * @param net1
	 * @param net2
	 * @return
	 */
	public static Network sumNetworks(Network net1, Network net2)
	{
		for (String eid : net2.getIds())
		{
			Connected e2 = net2.getById(eid);
			net1.add(e2);
		}

		return net1;
	}

	/**
	 * Copy from net1 network ERs defined in paths
	 * 
	 * @param net1
	 * @param paths
	 * @return
	 */
	public static Network filterByPathList(Network net1, Set<Path> paths)
	{
		Network net2 = new Network();
		for (Path p : paths)
		{
			for (String id : p.getItems())
			{
				if (null == net2.getById(id))
					net2.add(false, net1.getById(id));
			}
		}

		return net2;
	}
	
	public static boolean loadConnected(Connected er, Storage storage)
	{
		try
		{
			for (String rid : er.getConnectedKeys())
			{
				if (null == er.getConnected(rid))
				{
					Connected r = storage.getById(rid); 
					if (null != r)
						er.addConnected(r);
				}
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	

	/**
	 * load all connected
	 * 
	 * @param e
	 * @param network
	 * @param storage
	 * @return
	 */
	public static boolean loadConnected(Connected er, Network network, Storage storage)
	{
		return loadConnected(er, network, storage, Integer.MAX_VALUE);
	}
	
	public static boolean loadConnected(Connected e, Network network, Storage storage, int connectedCountLimit)
	{
		try
		{
			int counter = 0;
			for (String rid : e.getConnectedKeys())
			{
				counter++;
				if (counter >= connectedCountLimit)
					break;
				
				if (null == e.getConnected(rid))
				{
					Connected r = storage.getById(rid); 
					if (null != r)
						network.add(r);
				}
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static Map<String, List<Connected>> groupConnectedByName(Set<Connected> relations)
	{
		Map<String, List<Connected>> groupMap = new HashMap<String, List<Connected>>();
		for (Connected r : relations)
		{
			String rName = r.getName();
			List<Connected> rList = groupMap.get(rName);
			if (null == rList)
			{
				rList = new ArrayList<Connected>();
				groupMap.put(rName, rList);
			}
			
			rList.add(r);
		} // for (Relation r : relations)
		
		return groupMap;
	}

	/**
	 * Return last element ids from the path set
	 * 
	 * @param paths
	 * @return
	 */
	public static Set<String> getPathsEnds(Set<Path> paths)
	{
		Set<String> endsIds = new HashSet<String>();
		for (Path p : paths)
			endsIds.add(p.getLast());
		
		return endsIds;
	}

	/**
	 * Check if 2 entities are connected through a relation
	 * 
	 * @param e1
	 * @param e2
	 * @return
	 */
	public static boolean isConnectedThroughMediator(Connected e1, Connected e2)
	{
		if (null == e1 || null == e2)
			return false;
		
		// check if they have the same relation id in connected keys 
		
		for (String rid1 : e1.getConnectedKeys())
		{
			for (String rid2 : e2.getConnectedKeys())
				if (rid1.equals(rid2))
					return true;
		}
		
		return false;
	}
	

	
	public static void addRelation(Network net, Connected b1, Connected b2, String relation)
	{
		net.add(b1);
		net.add(b2);
		
		Connected r = new Connected(
				relation, 
				b1, 
				b2);
		net.add(r);		

		return;
	}
	
	/**
	 * 
	 * @param net
	 * @param e1
	 * @param e2
	 * @param maxDepth
	 * @return
	 */
	public static Path getPath(Network net, Connected e1, Connected e2, int maxDepth)
	{
		List<Path> allPaths = new ArrayList<Path>();
		allPaths.add(new Path(e1.getUuid()));
		
		Set<Path> matchPaths = new LinkedHashSet<Path>();
		
		getPaths(allPaths, matchPaths, e2.getUuid(), net, maxDepth, true);
		if (matchPaths.size() > 0)
			return matchPaths.iterator().next();

		
		return null;
	}
	
	public static Set<Path> getPaths(Network net, Connected e1, Connected e2, int maxDepth)
	{
		List<Path> allPaths = new ArrayList<Path>();
		allPaths.add(new Path(e1.getUuid()));
		
		Set<Path> matchPaths = new LinkedHashSet<Path>();
		
		getPaths(allPaths, matchPaths, e2.getUuid(), net, maxDepth, false);

		return matchPaths;
	}
	
	private static void getPaths(List<Path> allPaths, Set<Path> matchPaths, String matchId, Network net, int depth, boolean stopIfFound)
	{
		if (depth < 1)
			return;
		
		List<Path> allPathsNext = new ArrayList<Path>(2 * allPaths.size());
		
		for (Path p : allPaths)
		{
			String lastItemId = p.getLast();
			Connected lastItem = net.getById(lastItemId);
			
			if (null == lastItem)
				continue;
			
			for (String connectedId : lastItem.getConnectedKeys())
			{
				Path newPath = new Path(p, connectedId);
				
				if (connectedId.equals(matchId))
				{
					matchPaths.add(newPath);
					if (stopIfFound)
						break;
				} else {
					// for further processing put paths without match only 
					allPathsNext.add(newPath);
				}
				
			}
			
			if (stopIfFound && matchPaths.size() > 0)
				break;
		}
		
		if (!(stopIfFound && matchPaths.size() > 0))
			// next iteration recursion
			getPaths(allPathsNext, matchPaths, matchId, net, depth - 1, stopIfFound);

		return;
	}
	
	
}
