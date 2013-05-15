package org.neuro4j.web.console.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.neuro4j.nms.server.NMSServerConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WelcomeController {

	@RequestMapping("/welcome")
	public String welcomeAnonymous(HttpServletRequest request) {
		return "welcome";
	}

	@RequestMapping("/settings")
	public String settings(HttpServletRequest request) {
		
		NMSServerConfig nmsConfig = NMSServerConfig.getInstance();
		
		request.setAttribute("nmsConfig", nmsConfig);
		Set<String> storageNames = nmsConfig.getStorageNames();
		request.setAttribute("storageNames", storageNames);
		return "console/settings";
	}

}