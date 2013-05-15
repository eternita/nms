package org.neuro4j.nms.server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neuro4j.core.Network;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.NeuroStorage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.xml.NetworkConverter;

public class NMSQueryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Log logger = LogFactory.getLog(NMSQueryServlet.class);
	

	// This method is called by the servlet container to process a GET request.
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doPost(req, resp);
	}



	// This method is called by the servlet container to process a GET request.
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String cmd = (null != req.getParameter("cmd")) ? req.getParameter("cmd") : "help" ;
		if ("query".equalsIgnoreCase(cmd))
		{
			String output = (null != req.getParameter("output")) ? req.getParameter("output") : "xml";
			
			if ("bin".equals(output))
				queryBin(req, resp);
			else
				queryXML(req, resp);
			
		} else {
			help(req, resp);
		}
		return;
	}
	
	private void queryXML(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		resp.setContentType("text/xml");
		String query = req.getParameter("q");
		
		NeuroStorage neuroStorage = NMSServerConfig.getInstance().getStorage(req.getParameter("storage"));
		if (null == neuroStorage)
		{
			OutputStream os = resp.getOutputStream();
			os.write("Storage is not specified.".getBytes("UTF-8"));
			logger.info("Storage is not specified.");
			return;
		}
		
		if (null == query)
		{
			OutputStream os = resp.getOutputStream();
			os.write("No NQL query. Set 'q' parameter with NQL query.".getBytes("UTF-8"));
			
			logger.info("No NQL query");
			return;
		}

		Network n;
		try {
			n = neuroStorage.query(query);
			String xmlstr = NetworkConverter.network2xml(n);
			// debug only
//			dump2file(query, xmlstr);
			
			OutputStream os = resp.getOutputStream();
			os.write(xmlstr.getBytes("UTF-8"));
			// TODO: rework with streams
//			NetworkConverter.network2xmlstream(n, os);
			
		} catch (NQLException e) {
			logger.error("Wrong NQL query " + query, e);
			resp.getOutputStream().write(("Can't execute query " + query + " " + e.getMessage()).getBytes("UTF-8"));
		} catch (StorageException e) {
			logger.error("Can't execute query " + query, e);
			resp.getOutputStream().write(("Can't execute query " + query + " " + e.getMessage()).getBytes("UTF-8"));
		}

		return;
	}
	
	private void dump2file(String query, String xmlresp)
	{
		try {
			org.apache.commons.io.FileUtils.writeStringToFile(new File("C:/temp/net_nms_q_" + System.currentTimeMillis() + ".xml"), query + "\n\n" + xmlresp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	private void queryBin(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		String query = req.getParameter("q");
		if (null == query)
		{
			
			OutputStream os = resp.getOutputStream();
			os.write("No NQL query. Set 'q' parameter with NQL query.".getBytes("UTF-8"));
			logger.info("No NQL query");
			return;
		}
		NeuroStorage neuroStorage = NMSServerConfig.getInstance().getStorage(req.getParameter("storage"));
		if (null == neuroStorage)
		{
			OutputStream os = resp.getOutputStream();
			os.write("Storage is not specified.".getBytes("UTF-8"));
			logger.info("Storage is not specified.");
			return;
		}
		
		Network n;
		try {
			n = neuroStorage.query(query);
			ObjectOutputStream oos = new ObjectOutputStream(resp.getOutputStream());
			oos.writeObject(n);
			oos.flush();
			
			// TODO: rework with streams
//			NetworkConverter.network2xmlstream(n, os);
		} catch (NQLException e) {
			logger.error("Wrong NQL query " + query, e);
			resp.getOutputStream().write(("Can't execute query " + query + " " + e.getMessage()).getBytes("UTF-8"));

		} catch (StorageException e) {
			logger.error("Can't execute query " + query, e);
			resp.getOutputStream().write(("Can't execute query " + query + " " + e.getMessage()).getBytes("UTF-8"));
		}
		

		return;
	}
	
	private void help(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		
		String netXML =  "Hi Mister! Help. Supported commands";
		OutputStream os = null;
		try
		{
			os = resp.getOutputStream();
			os.write(netXML.getBytes("UTF-8"));
		} catch (Exception ex) {
			logger.error("", ex);
		} finally {
			try {
		        os.close();
			} catch (Exception ex2){}
		}
		
		return;
	}
	
}
