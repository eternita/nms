package org.neuro4j.web.console.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neuro4j.nms.server.NMSServerConfig;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.utils.IOUtils;
import org.neuro4j.web.console.utils.RequestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * returns binary stream for representation
 * 
 *
 */
@Controller
public class RepresentationController {
	
	public RepresentationController() {
	}
	
	@RequestMapping("/representation-details")
	public void entityGraphDetails(HttpServletRequest request, HttpServletResponse response) throws StorageException {
		String id = (String) request.getParameter("id");
		RequestUtils.params2attributes(request, "q", "storage");

		Storage storage = NMSServerConfig.getInstance().getStorage(request.getParameter("storage"));
		
		if (null == storage)
			return;

        InputStream repis = storage.getRepresentationInputStream(id);
        if (null == repis)
        	return;
        
        response.setHeader("Content-Disposition", "inline; filename=\"representation-" + id + "\"");
		try {
	        OutputStream out = response.getOutputStream();
	        IOUtils.copyLarge(repis, out);
	        out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}