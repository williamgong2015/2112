//package client;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//import com.google.gson.Gson;
//
///** HTTP Client demo program. Can both GET and POST to a URL and read input back from the
// * server. Designed to be used with the demoServlet servlet.
// */
//public class Client {
//	
//	private static class GetJsonBundle {
//		  private String uri;
//		  private String message;
//		  private int otherParameter;
//	}
//
//	class PostJsonBundle {
//		  private String message;
//	}
//	
//	private URL url;
//	
//	
//	/** If message is null, query the URL (GET) for the current message.
//	 * Otherwise, use a POST request to send the message.
//	 */
//	public Client(String url, String message, String newOther) {
//		Gson gson = new Gson();
//		try {
//			if (newOther == null)
//				this.url = new URL(url);
//			else
//				this.url = new URL(url + "?other_param=" + newOther);
//			
//			
//			HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
//			if (message == null) {
//				connection.connect();
//				System.out.println("Doing GET " + url);
//				BufferedReader r = new BufferedReader(new InputStreamReader(
//						connection.getInputStream()));
//				GetJsonBundle bundle = gson.fromJson(r, GetJsonBundle.class);
//				System.out.println("uri: " + bundle.uri);
//				System.out.println("message: " + bundle.message);
//				System.out.println("other: " + bundle.otherParameter);
//			} else {
//				System.out.println("Doing POST "+ url);
//				connection.setDoOutput(true); // send a POST message
//				connection.setRequestMethod("POST");
//				PrintWriter w = new PrintWriter(connection.getOutputStream());
//				PostJsonBundle bundle = new PostJsonBundle();
//				bundle.message = message;
//				String tmp = gson.toJson(bundle, PostJsonBundle.class);
//				System.out.println("Json from Client: " + tmp);
//				w.println(tmp);
//				w.flush();
//				BufferedReader r = new BufferedReader(new InputStreamReader(
//						connection.getInputStream()));
//				dumpResponse(r);
//			}
//		} catch (IOException e) {
//			System.err.println("IO exception: " + e.getMessage());
//		}
//	}
//	/** Read back output from the server. Could change to parse JSON... */
//	void dumpResponse(BufferedReader r) throws IOException {
//		for (;;) {
//			String l = r.readLine();
//			if (l == null) break;
//			System.out.println("Read: " + l);
//		}
//	}
//}
