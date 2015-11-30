package test.testsA7;

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
	private static final String ADMIN_LV = "admin";
	private static final String WRITER_LV = "write";
	private static final String READER_LV = "read";
	
	/**
	 * Test on all level with correct passwords
	 */
	@Test
	public void testCorrect() {
		String url = "http://localhost:8080/2112/servlet/servlet.Servlet/";
		MyClient client = new MyClient(url);
		assertTrue("ADMIN_LV can't be verified", 
									client.logIn(ADMIN_LV, ADMIN_PW) == 200);
		assertTrue("ADMIN_LV can't be verified",
									client.getSessionID() != 0);
		
		assertTrue("WRITER_LV can't be verified", 
									client.logIn(WRITER_LV, WRITER_PW) == 200);
		assertTrue("WRITER_LV can't be verified", 
									client.getSessionID() != 0);
		
		assertTrue("READER_LV can't be verified", 
									client.logIn(READER_LV, READER_PW) == 200);
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
		assertTrue("ADMIN_LV shouldn't be verified", 
								client.logIn(ADMIN_LV, WRITER_PW) == 401);
		assertTrue("ADMIN_LV can't be verified", 
								client.getSessionID() == 0);
		
		assertTrue("WRITER_LV shouldn't be verified", 
								client.logIn(WRITER_LV, "wrongPassword") == 401);
		assertTrue("WRITER_LV can't be verified", 
								client.getSessionID() == 0);
		
		assertTrue("READER_LV shouldn't be verified", 
								client.logIn(READER_LV, null) == 401);
		assertTrue("READER_LV can't be verified", 
								client.getSessionID() == 0);
	}
}
