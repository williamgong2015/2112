package test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.StringReader;

import org.junit.Test;

import ast.Program;
import parse.ParserImpl;

public class Parertester {

	@Test
	public void test() throws FileNotFoundException {
		ParserImpl p = new ParserImpl();
		System.out.println("A total of 7 test case,all are expected to fail");
		//test mismatched bracket
		String s = "{{{ahead[1] != 1 or ahead[1] != 2}} and ahead[1] != 0-1 --> attack;";
		StringReader sr = new StringReader(s);
		Program t = p.parse(sr);
		assertTrue(t == null);
		// test missing semicolon
		s = "POSTURE != 17 --> attack" + "\n" + "POSTURE != 17 --> attack";
		sr = new StringReader(s);
		t = p.parse(sr);
		assertTrue(t == null);
		//test mismatched parentheses
		s = "POSTURE != (((17)) --> attack" + "\n" + "POSTURE != 17 --> attack";
		sr = new StringReader(s);
		t = p.parse(sr);
		assertTrue(t == null);
		//test syntax error(condition is replaced by expression)
		s = "POSTURE * 17 --> attack" + "\n" + "POSTURE != 17 --> attack";
		sr = new StringReader(s);
		t = p.parse(sr);
		assertTrue(t == null);
		// test syntax error(action comes before update)
		s = "POSTURE != 17 --> attack mem[7] := 7;" ;
		sr = new StringReader(s);
		t = p.parse(sr);
		assertTrue(t == null);
		// test syntax error(multiple action)
		s = "POSTURE != 17 --> attack wait;" ;
		sr = new StringReader(s);
		t = p.parse(sr);
		assertTrue(t == null);
		//test syntax error(expression is replaced by relation)
		s = "POSTURE != (17 < 5) --> attack;" ;
		sr = new StringReader(s);
		t = p.parse(sr);
		assertTrue(t == null);
	}

}
