package org.neuro4j.nms.demo.roger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.neuro4j.NetworkUtils;
import org.neuro4j.core.Entity;
import org.neuro4j.core.Network;
import org.neuro4j.core.Representation;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.NeuroStorage;
import org.neuro4j.storage.StorageException;
import org.neuro4j.xml.ConvertationException;
import org.neuro4j.xml.NetworkConverter;

public class StorageBuilder {

	private NeuroStorage storage = null;
	
	public StorageBuilder(NeuroStorage storage)
	{
		this.storage = storage;		
	}
	
	public Network query(String query) 
	{
		try {
			Network net = storage.query(query);
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
			if (null == is)
				return;
			
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
		Entity roger = new Entity("Dog Roger");
		roger.setProperty("age", "3");
		roger.setProperty("sex", "male");
		roger.setProperty("color", "brown");
		roger.setProperty("breed", "terrier");
		net.add(roger);
		// post dog Roger's photo
		Representation r1 = uploadRepresentation("./data/demo-files/dog_roger.jpg");
		roger.addRepresentation(r1);

		Entity house = new Entity("House");
		house.setProperty("address", "...");
		house.setProperty("size", "...");

		Entity john = new Entity("John");
		john.setProperty("first_name", "John");
		john.setProperty("last_name", "Smith");
		john.setProperty("age", "27");
		// post John's photo
		Representation r2 = uploadRepresentation("./data/demo-files/john.jpg");
		john.addRepresentation(r2);
		
		Entity marry = new Entity("Marry");
		marry.setProperty("first_name", "Marry");
		marry.setProperty("last_name", "Smith");
		marry.setProperty("age", "25");

		Entity jane = new Entity("Jane");
		jane.setProperty("first_name", "Jane");
		jane.setProperty("last_name", "Smith");
		jane.setProperty("age", "23");
		
		Entity mike = new Entity("Mike");
		mike.setProperty("first_name", "Mike");
		mike.setProperty("last_name", "Johnson");

		Entity brad = new Entity("Brad");
		brad.setProperty("first_name", "Brad");
		brad.setProperty("last_name", "Perterson");

		Entity jasica = new Entity("Jasica");
		jasica.setProperty("first_name", "Jasica");

		
		Entity hp = new Entity("HP Company");
		hp.setProperty("address", "USA, Idaho, Boise");

		Entity walmart = new Entity("Walmart");
		walmart.setProperty("address", "USA, Idaho, Boise");

		Entity micron = new Entity("Micron Company");
		micron.setProperty("address", "USA, Idaho, Boise");

		Entity school = new Entity("High School");
		school.setProperty("address", "USA, Idaho, Boise, XYZ");
		
		Entity coins = new Entity("Coins");

		
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
				System.err.println("Can't read " + fileName);
			
			representation.setData(storage, fileBytes);
			
			return representation;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return null;
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
