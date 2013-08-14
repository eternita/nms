package org.neuro4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neuro4j.core.ERBase;
import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.core.Path;
import org.neuro4j.core.Relation;
import org.neuro4j.storage.Storage;

public class NetworkUtils {

	/**
	 * Merge entities & relations from net2 to net1
	 * 
	 * If net1 & net2 have entity or relation with the same uuid (but different objects) will survive (will be returned) which is in net1 
	 * 
	 * @param net1
	 * @param net2
	 * @return
	 */
	public static Network sumNetworks(Network net1, Network net2)
	{
		for (String eid : net2.getEntities())
		{
			Entity e2 = net2.getEntityByUUID(eid);
			net1.add(e2);
		}
		
		for (String rid : net2.getRelations())
		{
			Relation r2 = net2.getRelationByUUID(rid);
			net1.add(r2);
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
	
	public static boolean loadConnected(ERBase er, Storage storage)
	{
		if (er instanceof Entity)
			loadConnected((Entity) er, storage); 
		
		else if (er instanceof Relation)
			loadConnected((Relation) er, storage); 
		
		return false;
	}
	
	private static boolean loadConnected(Entity e, Storage storage)
	{
		try
		{
			for (String rid : e.getRelationsKeys())
			{
				if (null == e.getRelation(rid))
				{
					Relation r = storage.getRelationByUUID(rid); 
					if (null != r)
						e.addRelation(r);
				}
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private static boolean loadConnected(Relation r, Storage storage)
	{
		try
		{
			if (!r.isCompleteLoaded())
			{
				for (String reid : r.getParticipantsKeys())
				{
					if (null == r.getParticipant(reid))
					{
						Entity re = storage.getEntityByUUID(reid); 
						if (null != re)
							r.addParticipant(re);
					}
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
	public static boolean loadConnected(ERBase er, Network network, Storage storage)
	{
		if (er instanceof Entity)
			loadConnected((Entity)er, network, storage, Integer.MAX_VALUE);
		
		else if (er instanceof Relation)
			loadConnected((Relation)er, network, storage, Integer.MAX_VALUE);
		
		return false;
	}
	
	public static boolean loadConnected(ERBase er, Network network, Storage storage, int connectedCountLimit)
	{
		if (er instanceof Entity)
			loadConnected((Entity)er, network, storage, connectedCountLimit);
		
		else if (er instanceof Relation)
			loadConnected((Relation)er, network, storage, connectedCountLimit);
		
		return false;
	}

	private static boolean loadConnected(Entity e, Network network, Storage storage, int connectedCountLimit)
	{
		try
		{
			int counter = 0;
			for (String rid : e.getRelationsKeys())
			{
				counter++;
				if (counter >= connectedCountLimit)
					break;
				
				if (null == e.getRelation(rid))
				{
					Relation r = storage.getRelationByUUID(rid); 
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
	
	private static boolean loadConnected(Relation r, Network network, Storage storage, int connectedCountLimit)
	{
		try
		{
			if (!r.isCompleteLoaded())
			{
				int counter = 0;
				for (String reid : r.getParticipantsKeys())
				{
					counter++;
					if (counter >= connectedCountLimit)
						break;
					
					if (null == r.getParticipant(reid))
					{
						Entity re = storage.getEntityByUUID(reid); 
						if (null != re)
							network.add(re);
					}
				}
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static Map<String, List<Relation>> groupRelationsByName(Set<Relation> relations)
	{
		Map<String, List<Relation>> groupMap = new HashMap<String, List<Relation>>();
		for (Relation r : relations)
		{
			String rName = r.getName();
			List<Relation> rList = groupMap.get(rName);
			if (null == rList)
			{
				rList = new ArrayList<Relation>();
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
	public static boolean isConnected(Entity e1, Entity e2)
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
	

	
	public static void addRelation(Network net, Entity b1, Entity b2, String relation)
	{
		net.add(b1);
		net.add(b2);
		
		Relation r = new Relation(
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
	public static Path getPath(Network net, ERBase e1, ERBase e2, int maxDepth)
	{
		List<Path> allPaths = new ArrayList<Path>();
		allPaths.add(new Path(e1.getUuid()));
		
		Set<Path> matchPaths = new LinkedHashSet<Path>();
		
		getPaths(allPaths, matchPaths, e2.getUuid(), net, maxDepth, true);
		if (matchPaths.size() > 0)
			return matchPaths.iterator().next();

		
		return null;
	}
	
	public static Set<Path> getPaths(Network net, ERBase e1, ERBase e2, int maxDepth)
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
			ERBase lastItem = net.getById(lastItemId);
			
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
