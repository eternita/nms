package org.neuro4j.kms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.neuro4j.core.ERBase;
import org.neuro4j.core.Network;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;

public class Translator {

	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private Storage storage = null;
	
	public Translator(Storage storage)
	{
		this.storage = storage;		
	}	
	
	public Network getTranslationNetwork(String word, String from) {
		try {
			String query = "select (name=? and language=?)/[depth='8']";
			Network net = storage.query(query, new String[]{word, from});
			
			logger.info("Quering example : \n query='" + query + "' \n output network size: "  + net.getSize() + "\n");
			
			return net;
		} catch (NQLException e) {
			e.printStackTrace();
		} catch (StorageException e) {
			e.printStackTrace();
		}
		
		return new Network();
		
	}
	
	/**
	 * Used for direct translation. Get also entity (for extracting representations (e.g. images))
	 * Query input network
	 * 
	 * @param network
	 * @param word
	 * @param from
	 * @param to
	 * @return
	 */
	public Map<ERBase, Set<String>> translate(Network network, String word, String from, String to) {
		
		Map<ERBase, Set<String>> m = new HashMap<ERBase, Set<String>>();

		try {
			String query = "select (name=? and language=?)/()/() | select (external-experience='true')";
			Network net = network.query(query, new String[]{word, from, to, to});

			logger.info("Quering example : \n query='" + query + "' \n output network size: "  + net.getSize() + "\n");
			
			for (ERBase translationEntity : net.getERBases())
			{
				String queryTranslation = "select (id=?)/()/(language=?) | select (language=?)";
				Network translNet = network.query(queryTranslation, new String[]{translationEntity.getUuid(), to, to});
				Set<String> translations = new HashSet<String>();
				for (ERBase er : translNet.getERBases())
				{
					translations.add(er.getName());
				}
				m.put(translationEntity, translations);
			}
		} catch (NQLException e) {
			e.printStackTrace();
		}
		
		return m;
	}

	/**
	 * Get list of words for specific language
	 * Query storage
	 * 
	 * @param lang
	 * @return
	 */
	public Set<String> wordList(String lang) {
		Set<String> words = new HashSet<String>();

		try {
			String query = "select (language=?)";
			Network net = storage.query(query, new String[]{lang});
			for (ERBase er : net.getERBases())
			{
				words.add(er.getName());
			}
			
			logger.info("Quering example : \n query='" + query + "' \n output network size: "  + net.getSize() + "\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return words;
	}
	
	/**
	 * Used for reverse translation. Doesn't extract entity (for images)
	 * Query input network
	 * 
	 * @param network
	 * @param word
	 * @param from
	 * @param to
	 * @return
	 */
	public Set<String> translate4reverse(Network network, String word, String from, String to) {
		
		Set<String> translations = new HashSet<String>();
		try {
			String query = "select (name=? and language=?)/[depth='3']/(language=?) | select (language=?)";
			Network net = network.query(query, new String[]{word, from, to, to});
			
			logger.info("Quering example : \n query='" + query + "' \n output network size: "  + net.getSize() + "\n");
			
			for (ERBase er : net.getERBases())
			{
				// get translations if FROM and TO languages are different
				if (!from.equals(to))
					translations.add(er.getName());
			}
			
		} catch (NQLException e) {
			e.printStackTrace();
		}
		
		return translations;
	}

		
}
