package org.neuro4j.core;


import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import org.junit.Assert;
import org.neuro4j.NeuroManager;
import org.neuro4j.storage.Storage;
import org.neuro4j.storage.StorageException;

/**
 * TODO: move to demo storage project
 *
 */
public class DemoPhotosLoader {
	
	static private String TEST_DATA_BASE_DIR = "C:/Develop/src/neuro4j/nms/neuro4j-nms-tests/test_data/"; 
	
	static private String STORAGE_DIR = "C:/Develop/src/neuro4j/nms/neuro4j-nms-server/conf/neuro4j-home/data-demo-xml"; 
	

	public static void main(String[] args) throws Exception {
		
		Storage storage = NeuroManager.newInstance().getStorage(STORAGE_DIR, "storage.properties");
		
		Network net = storage.query("select e(name='John')");
		ERBase entity = net.getFirst("name", "John");
		
		Representation r1 = new Representation();
		byte[] file1 = getFileData(TEST_DATA_BASE_DIR + "files/john.jpg");
		try {
			r1.setData(storage, file1);
			
			byte[] data1 = r1.getDataAsBytes(storage);
			Assert.assertEquals(file1.length, data1.length);
		} catch (StorageException e1) {
			e1.printStackTrace();
		}

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
		net = storage.query("select e(id=?)", new String[]{eid});
		
		ERBase entity2 = net.getById(eid);
		
		Set<Representation> reps = entity2.getRepresentations();
		
		Representation rep2 = reps.iterator().next();
		
		byte[] ba2 = rep2.getDataAsBytes(storage);
		
		Assert.assertEquals(file1.length, ba2.length);

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
