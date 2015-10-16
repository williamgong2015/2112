package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import org.junit.Test;

import ast.Program;
import parse.Parser;
import parse.ParserFactory;

public class MutationTest {

	@Test
	public void test() throws FileNotFoundException {
		FileReader f = new FileReader(new File("src/test/mutationTest.txt"));
		Parser p = ParserFactory.getParser();
		Program pro = p.parse(f);
		for(int i = 0;i < 10000;i++) {
//			System.out.println(i);
			System.out.println(pro);
			Program after = pro.mutate();
			pro = after;
		}
		String s = pro.toString();
		StringReader sr = new StringReader(s);
		pro = null;
		pro = p.parse(sr);
		assertTrue(pro != null);
	}

}
