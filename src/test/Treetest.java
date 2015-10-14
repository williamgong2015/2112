package test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import org.junit.Test;

import ast.Program;
import exceptions.SyntaxError;
import parse.ParserImpl;

public class Treetest {

	@Test
	public void test() throws FileNotFoundException {
		FileReader f = new FileReader("test.txt");
		ParserImpl p = new ParserImpl();
		//test mismatched bracket
		String s = "{{{ahead[1] != 1 or ahead[1] != 2}} and ahead[1] != 0-1 --> attack;";
		StringReader sr = new StringReader(s);
		System.out.println("This is expexted to fail");
		Program t = p.parse(sr);
		assertTrue(t == null);
		// test syntax sugar
		s = "POSTURE != 17 --> attack;";
		sr = new StringReader(s);
		t = p.parse(sr);
		assertTrue(t != null);
		System.out.println("This is expexted to succeed");
		// test missing semicolon
		s = "POSTURE != 17 --> attack" + "\n" + "POSTURE != 17 --> attack";
		sr = new StringReader(s);
		System.out.println("This is expexted to fail");
		t = p.parse(sr);
		assertTrue(t == null);
		// test comment
		s = "POSTURE != 17 --> attack;";
		s +="\\fghj";
		sr = new StringReader(s);
		t = p.parse(sr);
		System.out.println(t);
	}

}
