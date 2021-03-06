package org.neuro4j.nms.demo.roger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.neuro4j.NetworkUtils;
import org.neuro4j.core.Connected;
import org.neuro4j.core.Network;
import org.neuro4j.core.Representation;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.xml.ConvertationException;
import org.neuro4j.xml.NetworkConverter;

public class StorageBuilder {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private Storage storage = null;
	
	public StorageBuilder(Storage storage)
	{
		this.storage = storage;		
	}
	
	public Network query(String query) 
	{
		try {
			Network net = storage.query(query);
			
			logger.info("Quering example : \n query='" + query + "' \n output network size: "  + net.getSize() + "\n");
			
			return net;
		} catch (NQLException e) {
			e.printStackTrace();
		} catch (StorageException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public void importNetwork(File dump)
	{
		InputStream is;
		try {
			is = new FileInputStream(dump);
			
	        Network network = NetworkConverter.xml2network(is);
	        if (null == network)
	        	return;
	        
	        storage.save(network);
	        
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ConvertationException e) {
			e.printStackTrace();
		} catch (StorageException e) {
			e.printStackTrace();
		}
		
	}
	
	public void postDataFromJava()
	{
		Network net = new Network();
		Connected roger = new Connected("Dog Roger");
		roger.setProperty("age", "3");
		roger.setProperty("sex", "male");
		roger.setProperty("color", "brown");
		roger.setProperty("breed", "terrier");
		net.add(roger);
		// post dog Roger's photo
		Representation r1 = uploadRepresentation("./data/demo-files/dog_roger.jpg");
		roger.addRepresentation(r1);

		Connected house = new Connected("House");
		house.setProperty("address", "...");
		house.setProperty("size", "...");

		Connected john = new Connected("John");
		john.setProperty("first_name", "John");
		john.setProperty("last_name", "Smith");
		john.setProperty("age", "27");
		// post John's photo
		Representation r2 = uploadRepresentation("./data/demo-files/john.jpg");
		john.addRepresentation(r2);
		
		Connected marry = new Connected("Marry");
		marry.setProperty("first_name", "Marry");
		marry.setProperty("last_name", "Smith");
		marry.setProperty("age", "25");

		Connected jane = new Connected("Jane");
		jane.setProperty("first_name", "Jane");
		jane.setProperty("last_name", "Smith");
		jane.setProperty("age", "23");
		
		Connected mike = new Connected("Mike");
		mike.setProperty("first_name", "Mike");
		mike.setProperty("last_name", "Johnson");

		Connected brad = new Connected("Brad");
		brad.setProperty("first_name", "Brad");
		brad.setProperty("last_name", "Perterson");

		Connected jasica = new Connected("Jasica");
		jasica.setProperty("first_name", "Jasica");

		
		Connected hp = new Connected("HP Company");
		hp.setProperty("address", "USA, Idaho, Boise");

		Connected walmart = new Connected("Walmart");
		walmart.setProperty("address", "USA, Idaho, Boise");

		Connected micron = new Connected("Micron Company");
		micron.setProperty("address", "USA, Idaho, Boise");

		Connected school = new Connected("High School");
		school.setProperty("address", "USA, Idaho, Boise, XYZ");
		
		Connected coins = new Connected("Coins");

		
		NetworkUtils.addRelation(net, roger, house, "live at");
		NetworkUtils.addRelation(net, john, house, "live at");
		NetworkUtils.addRelation(net, jane, house, "live at");
		NetworkUtils.addRelation(net, john, jane, "married");
		NetworkUtils.addRelation(net, john, marry, "brother-sister");
		
		NetworkUtils.addRelation(net, john, mike, "friends");
		NetworkUtils.addRelation(net, john, brad, "friends");
		NetworkUtils.addRelation(net, john, jasica, "friends");
		NetworkUtils.addRelation(net, brad, jasica, "friends");
		
		NetworkUtils.addRelation(net, john, hp, "work at");
		NetworkUtils.addRelation(net, jane, walmart, "work at");
		NetworkUtils.addRelation(net, brad, hp, "work at");
		NetworkUtils.addRelation(net, jasica, micron, "work at");
		NetworkUtils.addRelation(net, marry, school, "study at");
		NetworkUtils.addRelation(net, mike, hp, "work at");
		
		NetworkUtils.addRelation(net, john, coins, "interested in");
		
		
		try {
			storage.save(net);
		} catch (StorageException e) {
			e.printStackTrace();
		}
		return;
	}
	
	public void updateDataFromJava()
	{
		try {
			Network net = storage.query("INSERT (desc='coins John interested in')");
			String id = net.getIds()[0];
			
			storage.query("UPDATE " +
					"SET " +
					" (name='John' OR " +
					"  name='Coins' OR " +
					"  CHN_TYPE='COIN_INSTANCE' OR CHN_TYPE='COIN_GROUP') " +
					
					"WHERE (id=?)", new String[]{id});
			
		} catch (NQLException e) {
			e.printStackTrace();
		} catch (StorageException e) {
			e.printStackTrace();
		}
		
	}

	
	/**
	 * Read a file and upload it to storage as representation
	 * 
	 * @param fileName
	 * @return
	 */
	public Representation uploadRepresentation(String fileName)
	{
		Representation representation = new Representation();
		try {
			byte[] fileBytes = getFileData(fileName);
			if (null == fileBytes)
				logger.severe("Can't read " + fileName);
			
			representation.setData(storage, fileBytes);
			
			return representation;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * example of reading binary data from Network using representations 
	 */
	public void readBinaryDataFromJava()
	{
		try {
			Network net = storage.query("SELECT (name='John')");
			
			Connected john = net.getFirst();

			logger.info("Example of reading binary data from Network using representations. readBinaryDataFromJava(). ");
			for (Representation representation : john.getRepresentations())
			{
				// read meta-data
				for (String key : representation.getPropertyKeys())
					System.out.println("key : " + key + " value: " + representation.getProperty(key));
				
				// load binary data
				byte[] data = representation.getDataAsBytes(storage);
				
				// binary data can be loaded using streams (for large data)
				InputStream is = representation.getData(storage);
			}
			System.out.println("");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return;
	}

    private byte[] getFileData(String fileName) throws Exception {
        byte[] data = null;
        
        File file = new File(fileName);
        data = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(data);
        fis.close();

        return data;
    }
    
}
