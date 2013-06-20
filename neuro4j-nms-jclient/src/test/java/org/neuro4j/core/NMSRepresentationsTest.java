package org.neuro4j.core;


import java.io.File;
import java.io.FileInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neuro4j.NeuroManager;
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
//		ExpsysClient client = new ExpsysClient("expsys-client.properties");
		
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("url", "http://test.com");
		

		Network net = new Network();
/*		String proxyImpl = "org.neuro4j.core.rep.proxy.FileSystemByteArrayReprecentationProxy";
		Representation r1 = new Representation(proxyImpl);
		r1.setProperty("proxy.base_dir", "c:/data/temp");
		byte[] file1 = getFileData("c:/data/temp/slide1.png");
		try {
			r1.setData(file1);
			byte[] data1 = (byte[]) r1.getData();
			assertEquals(file1.length, data1.length);
		} catch (RepresentationProxyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

*/		
		Entity entity = new Entity("test entity");
//		entity.addRepresentation(r1);
		net.add(entity);

		try {
			neuroStorage.save(net);
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
