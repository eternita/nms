package org.neuro4j.core;


import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neuro4j.NeuroManager;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;

/**
 * 
 * Recommendation System Client
 *
 */
public class RepresentationsXMLStorageTest {
	
	private String TEST_DATA_BASE_DIR = "C:/Develop/src/neuro4j/nms/neuro4j-nms-tests/test_data/"; 
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testBinaryRepresentationsInXMLStorage() throws Exception {
		
		Storage storage = NeuroManager.newInstance().getStorage(
				TEST_DATA_BASE_DIR + "storages/demo-xml-rw", "storage.properties");
		
		Network net = new Network();
		Representation r1 = new Representation();
		byte[] file1 = getFileData(TEST_DATA_BASE_DIR + "files/1.png");
		try {
			r1.setData(storage, file1);
			
			byte[] data1 = r1.getDataAsBytes(storage);
			Assert.assertEquals(file1.length, data1.length);
		} catch (StorageException e1) {
			e1.printStackTrace();
		}

		ERBase entity = new ERBase("test entity with bin representation");
		entity.addRepresentation(r1);
		net.add(entity);

		String eid = entity.getUuid();
		try {
			storage.save(net);
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// read reps from storage
		net = storage.query("select (id=?)", new String[]{eid});
		
		ERBase entity2 = net.getById(eid);
		
		Set<Representation> reps = entity2.getRepresentations();
		
		Representation rep2 = reps.iterator().next();
		
		byte[] ba2 = rep2.getDataAsBytes(storage);
		
		Assert.assertEquals(file1.length, ba2.length);

		return;
	}	

	
	@Test
	public void testNetworkRepresentationsInXMLStorage() throws Exception {
		
		Storage storage = NeuroManager.newInstance().getStorage(
				TEST_DATA_BASE_DIR + "storages/demo-xml-rw", "storage.properties");
		
		

		Network representationNetwork = null;
		Network net = new Network();
		Representation representation = new Representation();
		
		representationNetwork = storage.query("select ()");

		representation.setData(storage, representationNetwork);


		ERBase entity = new ERBase("test entity with net representation");
		entity.addRepresentation(representation);
		net.add(entity);

		String eid = entity.getUuid();
		
		storage.save(net);
		
		// read reps from storage
		net = storage.query("select (id=?)", new String[]{eid});
		
		ERBase entity2 = net.getById(eid);
		
		Set<Representation> reps = entity2.getRepresentations();
		
		Representation rep2 = reps.iterator().next();
		
		Network net2 = rep2.getDataAsNetwork(storage);
		
		System.out.println(net2);
		
		return;
	}	
	
    private static byte[] getFileData(String fileName) throws Exception {
        byte[] data = null;
        
        File file = new File(fileName);
        data = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(data);
        fis.close();

        return data;
    }	
}
