package org.neuro4j.web.console.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class RelationController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public RelationController() {
	}
/*	
	@RequestMapping("/relation-details")
	public String relationDetails(HttpServletRequest request) throws StorageException {
		RequestUtils.params2attributes(request, "storage");
		String rUUID = (String) request.getParameter("uuid");
		Storage storage = NMSServerConfig.getInstance().getStorage(request.getParameter("storage"));
		if (null == storage)
		{
			request.setAttribute("storage_error", "Storage is not specified");
			return "console/settings";
		}
		
		if (null != rUUID)
		{
			Network net;
			try {
				net = storage.query("select r(id='" + rUUID + "') / [depth='2'] limit " + 
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
	}*/
}