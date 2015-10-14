package parse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import ast.Program;

public class Main {
	public static void main(String[] args)  {
		if(args.length > 1 ) {
			if(args[0].equals("--mutate")) {
				int n = Integer.parseInt(args[1]);
				try {
					FileReader f = new FileReader(new File(args[2]));
					Parser p = ParserFactory.getParser();
					Program pro = p.parse(f);
				} catch (FileNotFoundException e) {
					System.err.println("No such file...");
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.err.println("Wrong operation");
				}
			}
		}
		else try {
			FileReader f = new FileReader(new File(args[0]));
			Parser p = ParserFactory.getParser();
			Program pro = p.parse(f);
			System.out.println(pro);
		} catch (FileNotFoundException e) {
			System.err.println("No such file...");
		}
	}
}
