package org.neuro4j.web.console.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.neuro4j.core.Network;
import org.neuro4j.nms.server.NMSServerConfig;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.web.console.controller.form.UploadItem;
import org.neuro4j.web.console.utils.RequestUtils;
import org.neuro4j.xml.NetworkConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ImpexController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public ImpexController() {
	}

	@RequestMapping("/export")
	public void exportNetwork(HttpServletRequest request, HttpServletResponse response) throws StorageException {

		RequestUtils.params2attributes(request, "storage");
		Storage storage = NMSServerConfig.getInstance().getStorage(request.getParameter("storage"));
		if (null == storage)
		{
			request.setAttribute("storage_error", "Storage is not specified");
			try {
				request.getRequestDispatcher("settigs").forward(request, response);
			} catch (Exception e) {
				try {
					e.printStackTrace(response.getWriter());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			return;
		}
		
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
				long end = System.currentTimeMillis();
				request.setAttribute("qtime", end - start);				
			} catch (NQLException e) {
				logger.error("Wrong NQL query " + q, e);
				net = new Network();
			}
		}
		
		String xml = NetworkConverter.network2xml(net);
//		request.setAttribute("xml", xml);
		try {
			response.setContentType("application/x-download");
			response.setHeader("Content-disposition", "attachment; filename=export_data.xml");
			response.getOutputStream().write(xml.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			try {
				e.printStackTrace(response.getWriter());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

//		return "console/impex/export";
	}

	
    @RequestMapping(value = "/import", method = RequestMethod.GET)
    public String getUploadForm(HttpServletRequest request, Model model) {
		Storage storage = NMSServerConfig.getInstance().getStorage(request.getParameter("storage"));
		if (null == storage)
		{
			request.setAttribute("storage_error", "Storage is not specified");
			return "console/settings";
		}
		
		UploadItem ui = new UploadItem();
		ui.setStorage(request.getParameter("storage"));
		model.addAttribute(ui);
    	RequestUtils.params2attributes(request, "storage");
    	return "console/impex/uploadfile";
    }

    
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public String importNetwork2storage(UploadItem uploadItem, BindingResult result,
                    HttpServletRequest request, HttpServletResponse response,
                    HttpSession session) {
            if (result.hasErrors()) {
                    for (ObjectError error : result.getAllErrors()) {
                    	logger.error("Error: " + error.getCode() + " - " + error.getDefaultMessage());    
                    }
                    return "console/impex/import_status";
            }

    		Storage storage = NMSServerConfig.getInstance().getStorage(uploadItem.getStorage());
    		if (null == storage)
    		{
    			request.setAttribute("storage_error", "Storage is not specified");
    			return "console/settings";
    		}
    		
    		try {
                    MultipartFile file = uploadItem.getFileData();
                    InputStream inputStream = null;
                    if (file.getSize() > 0) {
                            inputStream = file.getInputStream();

                            Network network = NetworkConverter.xml2network(inputStream);

                    		inputStream.close();
                    		
                    		if(storage.save(network))
                    		{
                    			// ok
                    			
                    		} else {
                    			// failed
                    		}
                    }
                    

            } catch (Exception e) {
                    e.printStackTrace();
            }
            return "console/impex/import_status";
    }


	
}