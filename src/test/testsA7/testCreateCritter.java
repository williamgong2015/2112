package test.testsA7;


import java.io.File;

import org.junit.Test;

import client.connection.MyClient;

/**
 * Test create critter at both client and servlet side
 */
public class testCreateCritter {

	
	/**
	 * Test on all level with correct passwords
	 * @throws Exception 
	 */
	@Test
	public void testCorrect() throws Exception {
		String url = "http://localhost:8080/2112/servlet/servlet.Servlet/";
		MyClient client = new MyClient(url);
//		assertTrue("ADMIN_LV can't be verified", 
//				client.logIn(ADMIN_LV, ADMIN_PW) == 200);
//		client.setSessionID(id);
		File file = new File("critter1.txt");
		client.createCritter(file, null, 3);
	}

}
