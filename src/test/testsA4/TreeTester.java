package test.testsA4;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import servlet.ast.Program;
import servlet.parser.ParserImpl;

public class TreeTester {

	public static void main(String[] args) throws FileNotFoundException {
		FileReader f = new FileReader("src/testsA4/mutationTest.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		System.out.println(t.size());
		//test pretty-print  and nodeAt of every node
		for (int i = 0; i < t.size(); ++i) {
			System.out.println(i + " th Node");
			System.out.println(t.nodeAt(i));
		}
		// test whether the program could be parsed again
		StringReader sr = new StringReader(t.toString());
		p.parse(sr);
	}
	
}
