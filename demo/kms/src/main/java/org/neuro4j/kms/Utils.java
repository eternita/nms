package org.neuro4j.kms;

import javax.servlet.http.HttpServletRequest;

import org.neuro4j.NetworkUtils;
import org.neuro4j.core.Network;
import org.neuro4j.kms.d.ExternalExperience;
import org.neuro4j.kms.d.Word;

public class Utils {


	public static void connect(Network net, ExternalExperience obj, Word word)
	{
//		net.add(obj, word);
//		obj.addConnected(word);
//		NetworkUtils.addRelation(net, obj, word, word.getProperty("language"));
		NetworkUtils.addRelation(net, obj, word, "noun");
		return;
	}

	/**
	 * request parameters save as request attributes with the same names
	 * 
	 * @param request
	 * @param params
	 */
	public static void params2attributes(HttpServletRequest request, String...params)
    {
		for (String param : params)
		{
    		request.setAttribute(param, request.getParameter(param));
		}
		return;
    }	
}
