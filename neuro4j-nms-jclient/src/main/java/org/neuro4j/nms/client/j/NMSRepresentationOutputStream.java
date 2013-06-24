package org.neuro4j.nms.client.j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * 
 * TODO: this class is prototype only (it never run or tested)
 * 
 * Use this Stream with BufferedOutputStream only (because each write() call invoke HTTP call)
 *
 */
public class NMSRepresentationOutputStream extends OutputStream {

	private HttpClient httpClient = new DefaultHttpClient();
	private String nmsServerURL;
	private String representationId;
	
	/**
	 * Use with BufferedOutputStream
	 */
	public NMSRepresentationOutputStream(String nmsServerURL, String repId)
	{
		this.nmsServerURL = nmsServerURL;
		this.representationId = repId;
	}
	
	@Override
	public void write(int b) throws IOException {
		throw new RuntimeException("This method is not implemented. Use NMSRepresentationOutputStream with BufferedOutputStream only.");

	}

	@Override
    public void write(byte b[], int off, int len) throws IOException {
    	
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("repId", this.representationId));
		params.add(new BasicNameValuePair("off", "" + off));
		params.add(new BasicNameValuePair("len", "" + len));
		HttpPost httpPost = new HttpPost(nmsServerURL + "?" + URLEncodedUtils.format(params, "utf-8"));

		InputStreamEntity isre = new InputStreamEntity(new ByteArrayInputStream(b), b.length);
		httpPost.setEntity(isre);
		httpPost.setHeader("Content-Type", "application/x-java-serialized-object");

		try {
			 HttpResponse response = httpClient.execute(httpPost);

		} catch (Exception e) {
			throw new IOException("Can't write data", e);
		}
		
		return;
    }

}
