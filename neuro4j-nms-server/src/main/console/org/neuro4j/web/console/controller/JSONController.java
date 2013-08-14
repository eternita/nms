package org.neuro4j.web.console.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.core.Relation;
import org.neuro4j.nms.server.NMSServerConfig;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Controller
public class JSONController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public JSONController() {
	}
	
	@RequestMapping("/view-data")
	public void viewJSON(HttpServletRequest request, HttpServletResponse res) {
		
		String query = request.getParameter("q");
		if (null == query || query.trim().length() == 0)
			query = null;
		String depthStr = request.getParameter("d");
		int depth = 1;
		try 
		{
			depth = Integer.parseInt(depthStr);
			// check boundaries [1, 5]
			if (depth < 1 || depth > 5)
				depth = 1;
			
			
		} catch (Exception ex) {
			
		}
		
		Set<Entity> eList = new HashSet<Entity>();
		Network net = new Network();
		if (null != query)
		{ 
			try {
				Storage storage = NMSServerConfig.getInstance().getStorage(request.getParameter("storage"));
				if (null == storage)
				{
					logger.error("storage is not defined");
					return;
				}				
				
				
				net = (Network) request.getSession().getAttribute(query);
				if (null == net)
				{
					net = storage.query(query);
				} else {
					request.getSession().removeAttribute(query);
				}
				
				String[] entities = net.getEntities();
				
				for (String eid : entities)
				{
					doDepthView(net.getEntityByUUID(eid), eList, depth);
				}
			} catch (NQLException e) {
				logger.error("Wrong NQL query " + query, e);
			} catch (StorageException e) {
				logger.error("Can't execute query " + query, e);
			}
		} else {
			logger.error("No NQL query");
		}
		
		List<Node> nodes = adaptNodes(eList, net);

		GsonBuilder gb = new GsonBuilder();		
		Gson gson = gb.setPrettyPrinting().create();

		String jsonStr = gson.toJson(nodes);
		res.setContentType("application/json");
		
		logger.debug("JSON: " + jsonStr);
//		System.out.println(jsonStr);
		try {
			res.getWriter().write(jsonStr);
		} catch (IOException e) {
			logger.error("Can't write JSON string to output stream", e);
		}
		return;
	}
	
	/*
	 * does recursive call depends on depth
	 */
	private void doDepthView(Entity e, Collection<Entity> eList, int depth)
	{
		eList.add(e);
		if (depth == 1)
			return;

		for (Relation r : e.getRelations())
		{
			for (Entity rpe : r.getParticipants(e.getUuid()))
			{
				eList.add(rpe);

				doDepthView(rpe, eList, depth - 1);
			}
		}
		
		return;
	}
	
	private String getNodeType(Entity e)
	{
		return e.getProperty("N4J_CONSOLE_NODE_TYPE");
	}
	
	private List<Node> adaptNodes(Collection<Entity> eList, Network net)
	{
		List<Node> nodes = new ArrayList<Node>(eList.size());
		List<Entity> dependentEntities = new ArrayList<Entity>();
		for (Entity e : eList)
		{
			Node n = new Node();
			if (null != e.getUuid())
			{
				n.id = e.getUuid();
			} else {
				n.id = e.getName();
			}
			n.name = e.getName();
			n.data.put("$color", "#83548B");
			String nodeType = getNodeType(e);
			if (null != nodeType)
			{
				n.data.put("$type", nodeType);
//				n.data.put("$type", "circle");
			} else {
				n.data.put("$type", "circle");
			}
			n.data.put("$dim", 10);
			
			for (Relation r : e.getRelations())
			{
				if (null == net.getById(r.getUuid())) // show relations in network only
					continue;
				
				for (Entity rp : r.getParticipants(e.getUuid()))
				{
					Adjacency a = new Adjacency();
					a.nodeFrom = e.getUuid();
					a.nodeTo = rp.getUuid();
					dependentEntities.add(rp);
//					a.nodeFrom = e.getName();
//					a.nodeTo = rp.getEntity().getName();
					a.data.put("$name", r.getName());
					a.data.put("$color", "#557EAA");
					// TODO: how to add label to relation?
//					a.data.put("$text", "test");
//					a.data.put("$label", "test");
					n.adjacencies.add(a);
				}
			}
			nodes.add(n);
		}
		
		// update names for denepndent nodes (add depenent nodes without relations)
		for (Entity e : dependentEntities)
		{
			if (!eList.contains(e))
			{
				Node n = new Node();
				if (null != e.getUuid())
				{
					n.id = e.getUuid();
				} else {
					n.id = e.getName();
				}
				n.name = e.getName();
				n.data.put("$color", "#83548B");
				n.data.put("$type", "circle");
				n.data.put("$dim", 10);

				nodes.add(n);
			} 
		}
		
		return nodes;		
	}

}

class Node{
	List<Object> adjacencies = new ArrayList<Object>();
	Map<String, Object> data = new LinkedHashMap<String, Object>();
	String id;
	String name;
	
}

class Adjacency {
	String nodeTo;	
	String nodeFrom;
	Map<String, String> data = new HashMap<String, String>();
}
