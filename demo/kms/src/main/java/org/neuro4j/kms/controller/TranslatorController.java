package org.neuro4j.kms.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.neuro4j.core.Connected;
import org.neuro4j.core.Network;
import org.neuro4j.kms.Config;
import org.neuro4j.kms.Translator;
import org.neuro4j.kms.Utils;
import org.neuro4j.storage.StorageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TranslatorController {
	
	private Translator translator = null; 
	
	public TranslatorController()
	{
		translator = new Translator(Config.storage);
	}
	
	@RequestMapping("/translate.htm")
	public String translate(HttpServletRequest request) throws StorageException {
		String from = request.getParameter("from");
		String to = request.getParameter("to");
		String q = request.getParameter("q");
		Utils.params2attributes(request, "q", "from", "to");
		
		if (null == q || q.trim().length() == 0)
			return "translator";		
		
		Network net = translator.getTranslationNetwork(q, from);
		
		Map<Connected, Set<String>> translations = translator.translate(net, q, from, to);
		
		request.setAttribute("translations", translations);
		
		Map<String, Set<String>> reverseTranslations = new HashMap<String, Set<String>>();
		for (Set<String> translationSet : translations.values())
		{
			for (String translation : translationSet)
			{
				reverseTranslations.put(translation, translator.translate4reverse(net, translation, to, from));
			}					
		}
		request.setAttribute("reverseTranslations", reverseTranslations);
		
		return "translator";		
	}


	@RequestMapping("/dictionary.htm")
	public String dictionary(HttpServletRequest request) throws StorageException {
		String lang = request.getParameter("lang");
		
		Utils.params2attributes(request, "lang");
		
		Set<String> words = translator.wordList(lang);
		
		request.setAttribute("words", words);
				
		return "dictionary";		
	}
}