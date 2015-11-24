package testA7;

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
	private static final int MAX_CAPACITY = 2 ^ 30;
	
	public static void main(String[] args) {
		String url = "http://localhost:8080/2112/servlet/servlet.Servlet/";
		MyClient client = new MyClient(url);
		client.logIn(ADMIN_LV, ADMIN_PW);
		
	}
}
