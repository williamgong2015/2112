package parse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ast.Program;

public class Main {
	public static void main(String[] args) throws IOException  {
		String current = new java.io.File( "." ).getCanonicalPath();
        System.out.println("Current dir:"+current);
 String currentDir = System.getProperty("user.dir");
        System.out.println("Current dir using System:" +currentDir);
		if(args.length == 3 ) {
			if(args[0].equals("--mutate")) {
				try {
					int n = Integer.parseInt(args[1]);
					FileReader f = new FileReader(new File(args[2]));
					Parser p = ParserFactory.getParser();
					Program pro = p.parse(f);
					for(int i = 0;pro != null && i < n;i++) {
						System.out.println(pro.mutate());
					}
				} catch (FileNotFoundException e) {
					System.err.println("No such file...");
				}
				catch(ArrayIndexOutOfBoundsException NumberFormatException) {
					System.err.println("Wrong operation");
				}
			}
			else
				System.err.println("Wrong operation");
		}
		else if(args.length == 1) try {
			FileReader f = new FileReader(new File(args[0]));
			Parser p = ParserFactory.getParser();
			Program pro = p.parse(f);
			System.out.println(pro);
		} catch (FileNotFoundException e) {
			System.err.println("No such file...");
		}
		else {
			System.err.println("Wrong operation");
		}
	}
}
