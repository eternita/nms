package org.neuro4j.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public abstract class FileUtils {
		
		public static byte[] getResource(String path)
		{
	        byte[] data = null;
	        
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        InputStream is = null;
	        try 
	        {
	            is = FileUtils.class.getClassLoader().getResourceAsStream(path);
	            if (null != is)
	            {
	                int bytesRead = 0;
	                int bufferSize = 4000;
	                 byte[] byteBuffer = new byte[bufferSize];              
	                 while ((bytesRead = is.read(byteBuffer)) != -1) {
	                     baos.write(byteBuffer, 0, bytesRead);
	                 }
	                 data = baos.toByteArray();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (null != is)
	            {
	                try {
	                    is.close();
	                } catch (IOException e) { }
	            }
	        }
	        return data;
		}
		
		
		
		public static String getResourceAsString(String path)
		{

			byte[] ba = getResource(path);
			
			String s = null;
			try {
				s = new String(ba, 0, ba.length, "UTF8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return s;
		}

		
}
