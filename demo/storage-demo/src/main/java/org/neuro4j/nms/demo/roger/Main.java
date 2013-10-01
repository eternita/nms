package org.neuro4j.nms.demo.roger;

import java.io.File;

import org.neuro4j.NeuroManager;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;

public class Main {

	private static final String STORAGE_HOME_DIR = "./data/demo-storage"; 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Storage storage;
		try {
			storage = NeuroManager.newInstance().getStorage(STORAGE_HOME_DIR, "storage.properties");
		} catch (StorageException e) {
			e.printStackTrace();
			return;
		}

		StorageBuilder stroageBuilder = new StorageBuilder(storage);

		// example of simple query
		stroageBuilder.query("select () limit 3"); 
						
		// cleanup
		// call flow inside NQL
		// flow can be found at /storage-demo/src/main/flows/Utils.n4j
		stroageBuilder.query("behave(flow='Utils-Cleanup')");


		// query after cleanup -> network should be empty
		stroageBuilder.query("select () limit 3");
		
		// example how to post data with Java client
		// post some data using Java client
		stroageBuilder.postDataFromJava();

		stroageBuilder.query("select ()");
		
		// import network from file example
		// import some data about a Russian Empire's Ruble exported from http://coinshome.net
		// http://www.coinshome.net/en/neuro4j/net-browser.htm?eid=B_Z_AAEBWpoAAAEj8a5ucewv
		// http://www.coinshome.net/coin_details.htm?id=B_Z_AAEBWpoAAAEj8a5ucewv
		stroageBuilder.importNetwork(new File("./data/demo-files/1_rub_chn_import.xml"));
		
		stroageBuilder.query("select ()");
		// import example
		// import some data about a Roman Respublic's Denarius exported from http://coinshome.net
		stroageBuilder.importNetwork(new File("./data/demo-files/1_denarius_chn_import.xml"));
		
		// import countries (empires) and relations between them exported from http://coinshome.net
		stroageBuilder.importNetwork(new File("./data/demo-files/chn_countries.xml"));

		stroageBuilder.query("select ()");
		// network modification example
		// bind some entities in relations
		stroageBuilder.updateDataFromJava();
		
		stroageBuilder.query("select ()");
		// example of reading binary data from Network using representations
		// query entity John and download his photo
		stroageBuilder.readBinaryDataFromJava();
	}

}
