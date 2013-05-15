package org.neuro4j.core.rep;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.neuro4j.core.Representation;

public class RepresentationProxyFactory {
	
	
	private final static Logger logger = Logger.getLogger(RepresentationProxyFactory.class.getName());
	
	private static Map<String, RepresentationProxy> decoders = new HashMap<String, RepresentationProxy>();

	private static RepresentationProxy createRepresentationDecoder(String name) throws RepresentationProxyException
	{
		try {
			Class clazz = Class.forName(name);
			Object fObj = clazz.newInstance();
			if (fObj instanceof RepresentationProxy)
			{
				RepresentationProxy rd = (RepresentationProxy) fObj;
				rd.init();
				return rd;
			}
				
		} catch (ClassNotFoundException e) {
			logger.severe("Can't create RepresentationDecoder " + name + " " + e);
		} catch (InstantiationException e) {
			logger.severe("Can't create RepresentationDecoder " + name + " " + e);
		} catch (IllegalAccessException e) {
			logger.severe("Can't create RepresentationDecoder " + name + " " + e);
		} catch (Exception e) {
			logger.severe("Can't create RepresentationDecoder " + name + " " + e);
		}
		throw new RepresentationProxyException("RepresentationDecoder " + name + " not found");
	}
	
	private static RepresentationProxy getRepresentationDecoder(String decoderImpl) throws RepresentationProxyException
	{
		RepresentationProxy rd = decoders.get(decoderImpl);
		
		if (null == rd)
		{
			rd = createRepresentationDecoder(decoderImpl);
			decoders.put(decoderImpl, rd);
		}
		
		return rd;
	}
	
	public static void put(String decoderImpl, Object data, Representation representation) throws RepresentationProxyException
	{
		RepresentationProxy rd = getRepresentationDecoder(decoderImpl);
		rd.put(data, representation);
		return;
	}
	
	public static Object get(String decoderImpl, Representation representation) throws RepresentationProxyException
	{
		RepresentationProxy rd = getRepresentationDecoder(decoderImpl);
		return rd.get(representation);
	}

}
