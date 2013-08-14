package org.neuro4j.nms.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.utils.IOUtils;

// TODO: rework - use len & off (client output stream can contain just part of representation)
public class NMSReadRepresentationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Log logger = LogFactory.getLog(NMSReadRepresentationServlet.class);
	
	// This method is called by the servlet container to process a GET request.
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		resp.getOutputStream().write("<resp>GET Method is not implemented</resp>".getBytes());
		
		return;
	}


	private void read(HttpServletRequest request, HttpServletResponse resp) {
		resp.setContentType("text/xml");
		Storage storage = NMSServerConfig.getInstance().getStorage(request.getParameter("storage"));
		
		String repId = request.getParameter("repId");
		String len = request.getParameter("len");
		String off = request.getParameter("off");
		
		if (null == repId)
		{
			string2outputStream(resp, "<resp>Representation ID is not specified. </resp>");
			return;
		}
		
		if (null == storage)
		{
			string2outputStream(resp, "<resp>Storage is not specified. </resp>");
			return;
		}
		
		OutputStream clientRepOS = null;
		InputStream serverRepIS = null;
		try {
			// TODO: rework - use len & off (client output stream can contain just part of representation)
			serverRepIS = storage.getRepresentationInputStream(repId);
			clientRepOS = resp.getOutputStream();
			
			IOUtils.copyLarge(serverRepIS, clientRepOS);
			
			string2outputStream(resp, "<resp>OK</resp>");
		} catch (StorageException e1) {
			string2outputStream(resp, "<resp>Error occured during representation saving. See logs for details </resp>");
			e1.printStackTrace();
			logger.error("Can't save network", e1);
		} catch (IOException e) {
			string2outputStream(resp, "<resp>Error occured during representation saving. See logs for details </resp>");
			e.printStackTrace();
			logger.error("Can't save network", e);
		} finally {
			if (null != clientRepOS)
			{
				try {
					clientRepOS.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (null != serverRepIS)
			{
				try {
					serverRepIS.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
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
		read(req, resp);
	}
	
}
