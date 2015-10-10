package parse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import ast.Node;

public class test {
	public static void main(String[] args) throws IOException {
	FileReader f = new FileReader("test.txt");
	ParserImpl p = new ParserImpl();
	Node t =p.parse(f);
	System.out.println(t.toString());
//	Tokenizer t = new Tokenizer(f);
//	while(t.hasNext()) {
//		Token temp = t.next();
//		System.out.println(temp);
//	}
	}
}

