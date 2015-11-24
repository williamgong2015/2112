package testA7;

import static org.junit.Assert.*;

import org.junit.Test;

import client.MyClient;

/**
 * Test client and servlet on Login
 *
 */

public class testLogin {

	private static final String ADMIN_PW = "admin";
	private static final String WRITER_PW = "writer";
	private static final String READER_PW = "reader";
	private static final int ADMIN_LV = 1;
	private static final int WRITER_LV = 2;
	private static final int READER_LV = 3;
	
	/**
	 * Test on all level with correct passwords
	 */
	@Test
	public void testCorrect() {
		String url = "http://localhost:8080/2112/servlet/servlet.Servlet/";
		MyClient client = new MyClient(url);
		assertTrue("ADMIN_LV can't be verified", 
									client.logIn(ADMIN_LV, ADMIN_PW));
		assertTrue("ADMIN_LV can't be verified",
									client.getSessionID() != 0);
		
		assertTrue("WRITER_LV can't be verified", 
									client.logIn(WRITER_LV, WRITER_PW));
		assertTrue("WRITER_LV can't be verified", 
									client.getSessionID() != 0);
		
		assertTrue("READER_LV can't be verified", 
									client.logIn(READER_LV, READER_PW));
		assertTrue("READER_LV can't be verified", 
									client.getSessionID() != 0);
	}
	
	/**
	 * Test on all level with incorrect passwords
	 */
	@Test
	public void testIncorrect() {
		String url = "http://localhost:8080/2112/servlet/servlet.Servlet/";
		MyClient client = new MyClient(url);
		assertFalse("ADMIN_LV shouldn't be verified", 
									client.logIn(ADMIN_LV, WRITER_PW));
		assertTrue("ADMIN_LV can't be verified", 
									client.getSessionID() == -1);
		
		assertFalse("WRITER_LV shouldn't be verified", 
									client.logIn(WRITER_LV, "wrongPassword"));
		assertTrue("WRITER_LV can't be verified", 
									client.getSessionID() == -1);
		
		assertFalse("READER_LV shouldn't be verified", 
									client.logIn(READER_LV, null));
		assertTrue("READER_LV can't be verified", 
									client.getSessionID() == -1);
	}
}
