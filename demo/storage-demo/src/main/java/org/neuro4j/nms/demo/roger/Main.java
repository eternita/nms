package org.neuro4j.nms.demo.roger;

import java.io.File;

import org.neuro4j.NeuroManager;
import org.neuro4j.core.Network;
import org.neuro4j.storage.NeuroStorage;

public class Main {

	private static final String STORAGE_HOME_DIR = "./data/demo-storage"; 
	private static NeuroStorage storage = NeuroManager.newInstance().getNeuroStorage(STORAGE_HOME_DIR, "storage.properties");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// example of simple query
		try {
			Network net = storage.query("select e() limit 3");
			System.out.println("output network: " + net);
		} catch (Exception e) {
			e.printStackTrace();
		}

		StorageBuilder stroageBuilder = new StorageBuilder(storage);
		
		// cleanup
		// call flow inside NQL
		stroageBuilder.query("behave(flow='Utils-Cleanup')");

		// example how to post data with Java client
		// post some data using Java client
		stroageBuilder.postDataFromJava();
		
		// import network from file example
		// import some data about a Russian Empire's Ruble exported from http://coinshome.net
		// http://www.coinshome.net/en/neuro4j/net-browser.htm?eid=B_Z_AAEBWpoAAAEj8a5ucewv
		// http://www.coinshome.net/coin_details.htm?id=B_Z_AAEBWpoAAAEj8a5ucewv
		stroageBuilder.importNetwork(new File("./data/demo-files/1_rub_chn_import.xml"));

		// import example
		// import some data about a Roman Respublic's Denarius exported from http://coinshome.net
		stroageBuilder.importNetwork(new File("./data/demo-files/1_denarius_chn_import.xml"));

	}

}
