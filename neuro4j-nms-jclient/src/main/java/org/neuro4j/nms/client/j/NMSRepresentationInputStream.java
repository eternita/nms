package org.neuro4j.nms.client.j;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


/**
 * 
 * TODO: this class is prototype only (it never run or tested)
 * 
 * Use this Stream with BufferedInputStream only (because each read() call invoke HTTP call)
 *
 */
public class NMSRepresentationInputStream extends InputStream {

	private HttpClient httpClient = new DefaultHttpClient();
	private String nmsServerURL;
	private String representationId;
	
	private InputStream instream;
	
	/**
	 * Use with BufferedOutputStream
	 */
	public NMSRepresentationInputStream(String nmsServerURL, String repId)
	{
		this.nmsServerURL = nmsServerURL;
		this.representationId = repId;
	}
	
	@Override
	public int read() throws IOException {
		throw new RuntimeException("This method is not implemented. Use NMSRepresentationInputStream with BufferedInputStream only.");
	}
	
	private InputStream getRemoteInputStream() throws IOException
	{
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("repId", this.representationId));

		HttpGet httpGet = new HttpGet(nmsServerURL + "?" + URLEncodedUtils.format(params, "utf-8"));
		
		InputStream instream = null;
		try 
		{
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			
		    instream = entity.getContent();
		    
		} catch (Exception e) {
			throw new IOException("Can't write data", e);
		}
		
		return instream;
		
	}
	
	@Override
    public synchronized int read(byte b[], int off, int len) throws IOException
    {
		if (null == instream)
			instream = getRemoteInputStream();
			
	    return instream.read(b, off, len);
    }

	@Override
	public void close() throws IOException {
		if (null != instream)
			instream.close();
	}

	@Override
	public synchronized void reset() throws IOException {
		if (null != instream)
			instream.close();
		
		instream = getRemoteInputStream();
		
		return;
	}

	
}
