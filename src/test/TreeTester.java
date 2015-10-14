package test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import ast.Program;
import parse.ParserImpl;

public class TreeTester {

	public static void main(String[] args) throws FileNotFoundException {
		FileReader f = new FileReader("test.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		System.out.println(t.size());
		for (int i = 0; i < t.size(); ++i) {
			System.out.println(i + " th Node");
			System.out.println(t.nodeAt(i));
		}
	}
	
	//test 
	String s = "{{{ahead[1] != 1 or ahead[1] != 2}} and ahead[1] != 0-1 --> attack;";
	StringReader sr = new StringReader(s);

}
