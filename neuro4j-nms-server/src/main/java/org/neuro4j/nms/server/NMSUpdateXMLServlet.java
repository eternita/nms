package org.neuro4j.nms.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neuro4j.core.Network;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.xml.ConvertationException;
import org.neuro4j.xml.NetworkConverter;

public class NMSUpdateXMLServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Log logger = LogFactory.getLog(NMSUpdateXMLServlet.class);
	
	// This method is called by the servlet container to process a GET request.
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		string2outputStream(resp, "<resp>GET Method is not implemented</resp>");
		return;
	}
	
	private void string2outputStream(HttpServletResponse resp, String msg)
	{
		try {
			resp.getOutputStream().write(msg.getBytes());
		} catch (IOException e) {
			logger.error(e, e);
		}
	}
	
	private void save(HttpServletRequest req, HttpServletResponse resp) {
		resp.setContentType("text/xml");
		String netXML = req.getParameter("network");
		Storage storage = NMSServerConfig.getInstance().getStorage(req.getParameter("storage"));
		if (null == storage)
		{
			string2outputStream(resp, "<resp>Storage is not specified</resp>");
			return;
		}

		Network n;
		
		try {
			n = NetworkConverter.xml2network(netXML);
			
			boolean isOK = storage.save(n);
			if (isOK)
			{
				string2outputStream(resp, "<resp>OK</resp>");
			} else {
				string2outputStream(resp, "<resp>Can't save network. See logs for details</resp>");
			}
		} catch (StorageException e) {
			logger.error("Can't save network", e);
			string2outputStream(resp, "<resp>Can't save network. See logs for details</resp>");
		} catch (ConvertationException e1) {
			logger.error("Can't convert xml to network", e1);
			string2outputStream(resp, "<resp>Can't convert xml to network. See logs for details</resp>");
		}
		
		
		return;
	}
	
	// This method is called by the servlet container to process a GET request.
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		save(req, resp);
	}
	
	
}
