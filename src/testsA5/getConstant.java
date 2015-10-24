package testsA5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class getConstant {

	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		Scanner in = new Scanner(new File("example/constant.txt")); 
		PrintWriter writer = new PrintWriter("example/the-file-name.txt", "UTF-8");

		while (in.hasNextLine()) {
			String newConstant = in.nextLine();
			String[] token = newConstant.split(" ", 3);
//			writer.println("// " + token[2].substring(1, token[2].length()-1));
//			writer.print("public static int ");
//			writer.println(token[0] + ";");
//			writer.println();
			writer.print("case \"" + token[0] + "\": ");
			writer.println(token[0] + " = " + "Integer.parseInt(token[1]);");
			writer.println("break;");
			writer.println();
			
		}
		writer.close();
	}
}
