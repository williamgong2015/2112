package test.testsA4;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import org.junit.Test;

import servlet.ast.Program;
import servlet.parser.ParserImpl;

public class TestTreeParsing {

	/**
	 * Parse and build a AST tree, pretty print it and use the printed 
	 * string to build another AST tree, check both tree's pretty print 
	 * output match
	 * @throws FileNotFoundException
	 * 
	 * If pass this test, parse and pretty print doesn't add or left out 
	 * information
	 */
	@Test
	public void testParseTree() throws FileNotFoundException {
		FileReader f = new FileReader("src/testsA4/mutationTest.txt");
		ParserImpl p = new ParserImpl();
		Program t1 = p.parse(f);
		String parsedOutput = t1.toString();
		StringReader s = new StringReader(parsedOutput);
		Program t2 = p.parse(s);
		assertTrue("Two Tree's pretty print output does not match", 
				parsedOutput.equals(t2.toString()));
		System.out.println("testParseTree Succeed, your AST tree: ");
		System.out.println();
		System.out.println(parsedOutput);
	}

}
