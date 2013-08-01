package org.neuro4j.web.console.controller;

import javax.servlet.http.HttpServletRequest;

import org.neuro4j.core.Network;
import org.neuro4j.core.Relation;
import org.neuro4j.nms.server.NMSServerConfig;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.NeuroStorage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.web.console.utils.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RelationController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public RelationController() {
	}
	
	@RequestMapping("/relation-details")
	public String relationDetails(HttpServletRequest request) throws StorageException {
		RequestUtils.params2attributes(request, "storage");
		String rUUID = (String) request.getParameter("uuid");
		NeuroStorage neuroStorage = NMSServerConfig.getInstance().getStorage(request.getParameter("storage"));
		if (null == neuroStorage)
		{
			request.setAttribute("storage_error", "Storage is not specified");
			return "console/settings";
		}
		
		if (null != rUUID)
		{
			Network net;
			try {
				net = neuroStorage.query("select r(id='" + rUUID + "') / [depth='2'] limit " + 
															NMSServerConfig.getInstance().getProperty("org.neuro4j.nms.console.max_network_size_for_graph"));
				Relation r = net.getRelationByUUID(rUUID);
				if (null != r)
				{
					request.setAttribute("r", r);
				}
			} catch (NQLException e) {
				logger.error("Wrong NQL query ", e);
			}			
		}
		return "console/r/details";
	}
}