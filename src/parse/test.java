package parse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import ast.Node;
import ast.Program;

public class test {
	public static void main(String[] args) throws IOException {
		FileReader f = new FileReader("test.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		System.out.println(t.toString());
	}
}

