package org.neuro4j.nms.server;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neuro4j.core.Network;
import org.neuro4j.storage.NeuroStorage;
import org.neuro4j.storage.StorageException;

public class NMSUpdateBinServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Log logger = LogFactory.getLog(NMSUpdateBinServlet.class);
	
	// This method is called by the servlet container to process a GET request.
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		resp.getOutputStream().write("<resp>GET Method is not implemented</resp>".getBytes());
		
		return;
	}


	private void save(HttpServletRequest request, HttpServletResponse resp) {
		
		ObjectInputStream inputFromApplet = null;

		Network n = null;
		try {
			inputFromApplet = new ObjectInputStream(request.getInputStream());
		} catch (IOException e) {
			logger.error(e, e);
			return;
		}

		try {
			n = (Network) inputFromApplet.readObject();
		} catch (IOException e) {
			logger.error(e, e);
			return;
		} catch (ClassNotFoundException e) {
			logger.error(e, e);
			return;
		}

		resp.setContentType("text/xml");
		NeuroStorage neuroStorage = NMSServerConfig.getInstance().getStorage(request.getParameter("storage"));
		if (null == neuroStorage)
		{
			string2outputStream(resp, "<resp>Storage is not specified. </resp>");
		} else {
			boolean isOK;
			try {
				isOK = neuroStorage.save(n);
				if (isOK)
				{
					string2outputStream(resp, "<resp>OK</resp>");
				} else {
					string2outputStream(resp, "<resp>Error occured during network saving. See logs for details </resp>");
				}
			} catch (StorageException e) {
				logger.error("Can't save network", e);
				string2outputStream(resp, "<resp>Error occured during network saving. See logs for details </resp>");
			}
		}
		
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
	
	// This method is called by the servlet container to process a GET request.
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		save(req, resp);
	}
	
}
