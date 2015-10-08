package parse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

public class test {
	public static void main(String[] args) throws IOException {
	String s = "a";
	s+="\n";
	s+="b";
	FileWriter f  = new FileWriter ("test.txt");
	f.write(s);
	f.flush();
	System.out.println(s);
	}
}

