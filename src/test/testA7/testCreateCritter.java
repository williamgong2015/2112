package test.testA7;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import client.MyClient;
import game.exceptions.SyntaxError;
import servlet.element.Critter;
import test.testsA5.critterTest;

/**
 * Test create critter at both client and servlet side
 */
public class testCreateCritter {

	private static final String ADMIN_PW = "admin";
	private static final String WRITER_PW = "writer";
	private static final String READER_PW = "reader";
	private static final int ADMIN_LV = 1;
	private static final int WRITER_LV = 2;
	private static final int READER_LV = 3;
	
	/**
	 * Test on all level with correct passwords
	 * @throws SyntaxError 
	 * @throws IOException 
	 */
	@Test
	public void testCorrect() throws IOException, SyntaxError {
		String url = "http://localhost:8080/2112/servlet/servlet.Servlet/";
		MyClient client = new MyClient(url);
		assertTrue("ADMIN_LV can't be verified", 
				client.logIn(ADMIN_LV, ADMIN_PW) == 200);
		
		String file = critterTest.class.getResource("critter1.txt").getPath();
		Critter c = new Critter(file);
		client.createCritter(c, null, 3);
	}
	
	/**
	 * Test on all level with incorrect passwords
	 */
	@Test
	public void testIncorrect() {
		String url = "http://localhost:8080/2112/servlet/servlet.Servlet/";
		MyClient client = new MyClient(url);

	}
}
