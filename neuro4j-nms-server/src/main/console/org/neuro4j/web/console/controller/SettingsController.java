package org.neuro4j.web.console.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.neuro4j.nms.server.NMSServerConfig;
import org.neuro4j.storage.Storage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SettingsController {


	@RequestMapping("/settings")
	public String settings(HttpServletRequest request) {
		
		NMSServerConfig nmsConfig = NMSServerConfig.getInstance();
		
		request.setAttribute("nmsConfig", nmsConfig);
		Set<String> storageNames = nmsConfig.getStorageNames();
		request.setAttribute("storageNames", storageNames);
		return "console/settings/settings";
	}
	
	@RequestMapping("/storage-settings")
	public String viewStorage(HttpServletRequest request) {
		
		NMSServerConfig nmsConfig = NMSServerConfig.getInstance();
		
		String storageName = request.getParameter("name");
		Storage storage = nmsConfig.getStorage(storageName);
		if (null == storage)
			return "redirect:settings";
		
		request.setAttribute("storageObj", storage);
		request.setAttribute("storage", storageName);

		return "console/settings/storage";
	}


}