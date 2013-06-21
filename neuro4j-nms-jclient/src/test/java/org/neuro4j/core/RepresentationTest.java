package org.neuro4j.core;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neuro4j.core.rep.RepresentationProxyException;
import org.neuro4j.xml.NetworkConverter;

public class RepresentationTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
//	public void test() {
//		fail("Not yet implemented");
//	}
	
	@Test
	public void testRepsNMSProxy() throws RepresentationProxyException {
		
	}

	@Test
	public void testReps() throws RepresentationProxyException {
//		String proxyImpl = "org.neuro4j.core.rep.proxy.FileSystemByteArrayReprecentationProxy";
		
		
//		RepresentationProxy proxyImpl = new FileSystemByteArrayRepresentationProxy();
		Representation r1 = new Representation();
		r1.setProperty("proxy.base_dir", "c:/data/temp");
		byte[] file1 = getFileData("c:/data/temp/slide1.png");
		r1.setData(file1);
		byte[] data1 = (byte[]) r1.getData();
		assertEquals(file1.length, data1.length);
		
		Entity e = new Entity("test entity");
		e.addRepresentation(r1);

		Representation r2 = new Representation();
		r2.setProperty("proxy.base_dir", "c:/data/temp");
		byte[] file2 = getFileData("c:/data/temp/slide2.png");
		r2.setData(file2);
		byte[] data2 = (byte[]) r2.getData();
		assertEquals(file2.length, data2.length);
		e.addRepresentation(r2);
		
		
		for (Representation r : e.getRepresentations())
		{
			System.out.println(r);
		}
		
		Network net = new Network();
		net.add(e);
		String netXML = NetworkConverter.network2xml(net);
		System.out.println(netXML);
		
		e.removeRepresentation(r1);

		for (Representation r : e.getRepresentations())
		{
			System.out.println(r);
		}

		netXML = NetworkConverter.network2xml(net);
		System.out.println(netXML);

		e.addRepresentation(r1);
		
		netXML = NetworkConverter.network2xml(net);
		System.out.println(netXML);

//*/
//		fail("Not yet implemented");
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
