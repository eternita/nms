package org.neuro4j.web.console.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neuro4j.core.ERBase;
import org.neuro4j.core.Network;
import org.neuro4j.nms.server.NMSServerConfig;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.web.console.utils.RequestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class EntitiesController {
	
	public EntitiesController() {
	}

	@RequestMapping("/view")
	public String viewContext(HttpServletRequest request) {
		request.setAttribute("init_view", "true");
		return "console/e/view";
	}


	@RequestMapping("/entity-details")
	public String entityDetails(HttpServletRequest request) throws StorageException {

		String view = request.getParameter("vt");
		if (!"graph".equalsIgnoreCase(view))
			view = "list";

		request.setAttribute("view", view);
		
		RequestUtils.params2attributes(request, "vt", "view_depth", "eid", "storage");
		
		request.setAttribute("entity_view", "true");
		
		String eid = (String) request.getParameter("eid");
		if (null == eid)
			return "redirect:/entities";

		Storage storage = NMSServerConfig.getInstance().getStorage(request.getParameter("storage"));
		if (null == storage)
		{
			request.setAttribute("storage_error", "Storage is not specified");
			return "console/settings";
		}

		ERBase e = getEntity(eid, storage);

		if (null == e)
			return "redirect:/query";

		request.setAttribute("entity", e);
		int depth = 1;
		try
		{
			String depthStr = request.getParameter("view_depth");
			if (null != depthStr && depthStr.length() > 0)
				depth = Integer.parseInt(depthStr);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		String queryStr = "select (id='" + eid + "') / [depth='" + 2*depth + "'] limit " +
				NMSServerConfig.getInstance().getProperty("org.neuro4j.nms.console.max_network_size_for_graph");
		request.setAttribute("q", queryStr);
		
		if ("graph".equalsIgnoreCase(view))
		{
			request.setAttribute("include_accordion_js", "true");
			request.setAttribute("selected_tab", "graph");
		}		

		return "console/e/details";
	}
	
	@RequestMapping("/entity-details-more-data")
	public String entityGraphDetails(HttpServletRequest request, HttpServletResponse response) throws StorageException {
		String eid = (String) request.getParameter("eid");
		RequestUtils.params2attributes(request, "q", "storage");

		Storage storage = NMSServerConfig.getInstance().getStorage(request.getParameter("storage"));
		if (null == storage)
		{
			request.setAttribute("storage_error", "Storage is not specified");
			return "console/settings";
		}
		ERBase e = getEntity(eid, storage);
		
		request.setAttribute("entity", e);		

		Map<String, List<ERBase>> groupedRelationMap = e.groupConnectedByName();// NetUtils.groupRelationsByName(e.getRelations()); //  getRelationMapGroupedByType(e);
		request.setAttribute("grouped_relation_map", groupedRelationMap);
		
		response.setCharacterEncoding("UTF-8");

		return "console/e/graph-details";
	}
	
	private ERBase getEntity(String eid, Storage storage)
	{
		// for details 1 level of expand is enough
		String queryStr = "select (id='" + eid + "') / [depth='2'] limit " + 
											NMSServerConfig.getInstance().getProperty("org.neuro4j.nms.console.max_network_size_for_graph"); 
		Network net;
		try {
			net = storage.query(queryStr);
			ERBase e = net.getById(eid);
			return e;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		return null;
	}
	
}