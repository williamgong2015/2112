package client;

/**
 * Temporary command line interface for client (before incorporate GUI)
 *
 * Run as method url 
 * 
 */
public class Console {

	// version number of the current display
	private int version_num;
	
	public static void main(String[] args) {
		if (args.length < 2 || args.length > 3) {
			usage();
		}
		new Client(args[0], args.length >= 2 ? args[1] : null,  args.length == 3 ? args[2] : null);
	}
	
	private static void usage() {
		System.err.println("Usage: <Method> <URL> [<other>]");
		System.exit(1);
	}
	
	
}
