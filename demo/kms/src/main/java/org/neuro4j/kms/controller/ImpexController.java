package org.neuro4j.kms.controller;

import javax.servlet.http.HttpServletRequest;

import org.neuro4j.kms.Config;
import org.neuro4j.kms.impex.Importer;
import org.neuro4j.storage.StorageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ImpexController {
	
	
	public ImpexController()
	{
	}
	
	@RequestMapping("/init.htm")
	public String translate(HttpServletRequest request) throws StorageException {
		
		Importer importer = new Importer(Config.storage);

		importer.query("delete () ");
		
		importer.query("select () limit 3");
		
		importer.initialImport();
		
		importer.query("select () ");
		
		return "redirect:/";		
	}

}