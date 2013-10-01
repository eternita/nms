package org.neuro4j.web.console.controller;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.neuro4j.core.Connected;
import org.neuro4j.core.Network;
import org.neuro4j.nms.server.NMSServerConfig;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.utils.KVUtils;
import org.neuro4j.web.console.GeneralHelper;
import org.neuro4j.web.console.controller.view.ViewComponent;
import org.neuro4j.web.console.controller.view.ViewJSPlumbProcessor;
import org.neuro4j.web.console.resolver.ERBaseResolver;
import org.neuro4j.web.console.utils.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class QueryController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private int MAX_NETWORK_SIZE_FOR_GRAPH = 300;
	
	private  final static int LIST_PAGE_SIZE = 25;
	private  final static int DISPLAYED_CONNECTED_LIMIT = 10; // in list view if ER has more connected -> ... is displayed

	
	public QueryController() {
		
		MAX_NETWORK_SIZE_FOR_GRAPH = KVUtils.getIntProperty(
				NMSServerConfig.getInstance().getProperties(), 
				"org.neuro4j.nms.console.max_network_size_for_graph", 
				MAX_NETWORK_SIZE_FOR_GRAPH);
	}


	@RequestMapping("/query")
	public String query(HttpServletRequest request) throws StorageException {

		RequestUtils.params2attributes(request, "storage");
		String view = request.getParameter("vt");
		if (!"graph".equalsIgnoreCase(view) && !"jsplumb".equalsIgnoreCase(view))
			view = "list";
		
		Storage storage = NMSServerConfig.getInstance().getStorage(request.getParameter("storage"));
		if (null == storage)
		{
			request.setAttribute("storage_error", "Storage is not specified");
			return "console/settings/settings";
		}
			
		RequestUtils.params2attributes(request, "vt");
		Network net = null;
		String q = request.getParameter("q");
		if (null == q || q.trim().length() == 0)
		{
			logger.info("No NQL query");
			net = new Network();
		} else {
			RequestUtils.params2attributes(request, "q");
			try {
				long start = System.currentTimeMillis();
				net = storage.query(q);
				if (null == net) // can be in case of behave queries.  behave(flow="n4j_site.flows.ExpsysPostHook-TestQuery") 
					net = new Network();
				
				long end = System.currentTimeMillis();
				request.setAttribute("qtime", end - start);				
			} catch (NQLException e) {
				String msg = e.getMessage();
				if (null != e.getCause())
					msg += " : " +  e.getCause().getMessage();
				
				request.setAttribute("nql_error", msg);
				request.setAttribute("qtime", "0");				
				logger.error(e.getLocalizedMessage());
				net = new Network();
			} catch (Exception e) {
				String msg = e.getMessage();
				if (null != e.getCause())
					msg += " : " +  e.getCause().getMessage();
				
				request.setAttribute("nql_error", msg);
				request.setAttribute("qtime", "0");				
				logger.error("Wrong NQL query " + q, e);
				net = new Network();
			}
		}
			
		Connected header = getSQLTableHeader(net);

		String[] entities = net.getIds();
//		String[] relations = net.getRelations();
		request.setAttribute("e_size", entities.length);
//		request.setAttribute("r_size", relations.length);
		

		if (null != header)
		{
			Set<String> headers = new LinkedHashSet<String>();
			
			for (String key : header.getPropertyKeys())
			{
				if (key.startsWith("c."))
					headers.add(header.getProperty(key));
			}
			
			request.setAttribute("header_columns", headers);

			GeneralHelper.createVHLList(request, entities, "en", new ERBaseResolver(storage, net, DISPLAYED_CONNECTED_LIMIT), "e_list", "_el", LIST_PAGE_SIZE);
			view = "table"; // SQL table
		} else {
			
			if (net.getSize() > MAX_NETWORK_SIZE_FOR_GRAPH)
				view = null; // list view only

			if (null == view || "list".equalsIgnoreCase(view))
				GeneralHelper.createVHLList(request, entities, "en", new ERBaseResolver(storage, net, DISPLAYED_CONNECTED_LIMIT), "e_list", "_el", LIST_PAGE_SIZE);
				
		}
			
		request.setAttribute("view", view);

		if ("graph".equalsIgnoreCase(view))
		{
			request.setAttribute("include_accordion_js", "true");
			request.setAttribute("selected_tab", "graph");
			request.setAttribute("query_view", "true");
			request.setAttribute("view_depth", 1); // for graph

			// cache in request (because of the same query run from graph JSON in JSONController) - will be removed in JSONController
			if (null != q) // q can be null if just open query form
				request.getSession().setAttribute(q, net);
			
		}else if ("jsplumb".equalsIgnoreCase(view))
		{
			request.setAttribute("selected_tab", "graph");
			ViewJSPlumbProcessor processor = new ViewJSPlumbProcessor(net);
			String startNodeId = request.getParameter("startNodeId");
			if (null == startNodeId)
			{
				Connected start = net.getFirst("name", "Start");
				if (null != start)
					startNodeId = start.getUuid();
			}
			
			Set<ViewComponent> componentsList = processor.process(startNodeId);
			request.setAttribute("components_list", componentsList);
		} else {
			
		}
			
		return "console/e/query";
	}


	private Connected getSQLTableHeader(Network net) {
		if (null == net)
			return null;
		
		Connected header = net.getFirst("name", "n4j_sql_table_header"); //query("select e[name='n4j_sql_table_header']");

		if (null != header)
		{
			net.remove(header); // do not display header in table (rows only)
			return header;
		}
		
		return null;
	}
}