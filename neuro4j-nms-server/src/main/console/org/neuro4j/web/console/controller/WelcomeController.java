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

}