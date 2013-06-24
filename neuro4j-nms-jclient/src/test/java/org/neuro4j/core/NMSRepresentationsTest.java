package org.neuro4j.core;


import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neuro4j.NeuroManager;
import org.neuro4j.storage.NQLException;
import org.neuro4j.storage.NeuroStorage;
import org.neuro4j.storage.StorageException;

/**
 * 
 * Recommendation System Client
 *
 */
public class NMSRepresentationsTest {
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testNMSReps() {
		
		NeuroStorage neuroStorage = NeuroManager.newInstance().getNeuroStorage("expsys-client.properties");

//		NeuroStorage neuroStorage = NeuroManager.newInstance().getNeuroStorage(
//				"C:/Develop/src/neuro4j/nms/neuro4j-nms-jclient/test_data/data-demo-xml", "storage.properties");
		
		

		Network net = new Network();
//		String proxyImpl = "org.neuro4j.core.rep.proxy.FileSystemByteArrayReprecentationProxy";
		Representation r1 = new Representation();
//		r1.setProperty("proxy.base_dir", "c:/data/temp");
		byte[] file1 = getFileData("c:/data/temp/slide1.png");
		try {
			r1.setData(neuroStorage, file1);
			
			byte[] data1 = r1.getDataAsBytes(neuroStorage);
			Assert.assertEquals(file1.length, data1.length);
		} catch (StorageException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Entity entity = new Entity("test entity");
		entity.addRepresentation(r1);
		net.add(entity);

		String eid = entity.getUuid();
		try {
			neuroStorage.save(net);
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// read reps from storage
		
		try {
			net = neuroStorage.query("select e(id=?)", new String[]{eid});
			
			Entity entity2 = net.getEntityByUUID(eid);
			
			Set<Representation> reps = entity2.getRepresentations();
			
			Representation rep2 = reps.iterator().next();
			
			byte[] ba2 = rep2.getDataAsBytes(neuroStorage);
			
			Assert.assertEquals(file1.length, ba2.length);
			
			
		} catch (NQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}	

	
	@Test
	public void testXMLReps() {
		
//		NeuroStorage neuroStorage = NeuroManager.newInstance().getNeuroStorage("expsys-client.properties");
//		C:\Develop\src\neuro4j\nms\neuro4j-nms-jclient\test_data\data-demo-xml\storage.properties
		NeuroStorage neuroStorage = NeuroManager.newInstance().getNeuroStorage(
				"C:/Develop/src/neuro4j/nms/neuro4j-nms-jclient/test_data/data-demo-xml", "storage.properties");
		
		

		Network net = new Network();
//		String proxyImpl = "org.neuro4j.core.rep.proxy.FileSystemByteArrayReprecentationProxy";
		Representation r1 = new Representation();
//		r1.setProperty("proxy.base_dir", "c:/data/temp");
		byte[] file1 = getFileData("c:/data/temp/slide1.png");
		try {
			r1.setData(neuroStorage, file1);
			
			byte[] data1 = r1.getDataAsBytes(neuroStorage);
			Assert.assertEquals(file1.length, data1.length);
		} catch (StorageException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Entity entity = new Entity("test entity");
		entity.addRepresentation(r1);
		net.add(entity);

		String eid = entity.getUuid();
		try {
			neuroStorage.save(net);
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// read reps from storage
		
		try {
			net = neuroStorage.query("select e(id=?)", new String[]{eid});
			
			Entity entity2 = net.getEntityByUUID(eid);
			
			Set<Representation> reps = entity2.getRepresentations();
			
			Representation rep2 = reps.iterator().next();
			
			byte[] ba2 = rep2.getDataAsBytes(neuroStorage);
			
			Assert.assertEquals(file1.length, ba2.length);
			
			
		} catch (NQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}	

	
	@Test
	public void testXMLRepsNetwork() {
		
		NeuroStorage neuroStorage = NeuroManager.newInstance().getNeuroStorage(
				"C:/Develop/src/neuro4j/nms/neuro4j-nms-jclient/test_data/data-demo-xml", "storage.properties");
		
		

		Network repsNet = null;
		Network net = new Network();
		Representation r1 = new Representation();
		try {
			repsNet = neuroStorage.query("select e()");
		} catch (NQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			r1.setData(neuroStorage, repsNet);
			
//			byte[] data1 = r1.getDataAsBytes(neuroStorage);
//			Assert.assertEquals(file1.length, data1.length);
		} catch (StorageException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Entity entity = new Entity("test entity");
		entity.addRepresentation(r1);
		net.add(entity);

		String eid = entity.getUuid();
		try {
			neuroStorage.save(net);
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// read reps from storage
		try {
			net = neuroStorage.query("select e(id=?)", new String[]{eid});
			
			Entity entity2 = net.getEntityByUUID(eid);
			
			Set<Representation> reps = entity2.getRepresentations();
			
			Representation rep2 = reps.iterator().next();
			
			Network net2 = rep2.getDataAsNetwork(neuroStorage);
			
			System.out.println(net2);
			
			
		} catch (NQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}	
	
    private static byte[] getFileData(String fileName) throws RuntimeException {
        byte[] data = null;
        try {
            File file = new File(fileName);
            data = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(data);
            fis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
        return data;
    }	
}
