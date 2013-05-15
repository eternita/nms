package org.neuro4j.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.neuro4j.xml.internal.PropertyListXML;
import org.neuro4j.xml.internal.PropertyXML;


public class RepresentationListConverter {
	
	public static String rep2xml(Map<String, String> properties)
	{
		if (null == properties)
			return null;

		PropertyListXML prList = new PropertyListXML();
		for (String key : properties.keySet())
		{
			PropertyXML p = new PropertyXML(key, properties.get(key));

			prList.getProperties().add(p);
		}
				
		StringWriter writer = new StringWriter();
		try {
			JAXBContext ctx = JAXBContext.newInstance(PropertyListXML.class);
			
			Marshaller m = ctx.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(prList, writer);
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}		
		return writer.toString();
	}

	/**
	 * 
	 * @param propertiesStr
	 * @return
	 */
	public static Map<String, String> xml2rep(String propertiesStr)
	{
		if (null == propertiesStr)
			return new HashMap<String, String>();

		PropertyListXML pl = null;
		try {
			JAXBContext ctx = JAXBContext.newInstance(PropertyListXML.class);
			
			Unmarshaller um = ctx.createUnmarshaller();
			pl = (PropertyListXML) um.unmarshal(new StringReader(propertiesStr));
			if (null == pl)
				return null;

			Map<String, String> properties = new HashMap<String, String>();
			for (PropertyXML p : pl.getProperties())
			{
				properties.put(p.getKey(), p.getValue());
			}

			return properties;
		} catch (JAXBException e) {
			e.printStackTrace();
			return new HashMap<String, String>();
		}		
	}

	

}
