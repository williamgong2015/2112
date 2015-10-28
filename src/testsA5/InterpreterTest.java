package testsA5;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import ast.Command;
import ast.Condition;
import ast.Expr;
import exceptions.SyntaxError;
import interpret.InterpreterImpl;
import parse.ParserImpl;
import parse.Tokenizer;
import simulate.World;

/**
 * Black box test of Interpreter 
 * use AST nodes as input, check every kind of interpret method if 
 * the outcomes is as expected
 *
 * Random test of Interpreter
 * use Mutation to generate random AST nodes, check the interpreter won't crash
 */
public class InterpreterTest {

//	@Test
//	public void test() throws SyntaxError {
//		InterpreterImpl i = new InterpreterImpl(null);
//		
//		
//		String str = "3 > 2 and 4 != 7";
//		StringReader r = new StringReader(str);
//		Tokenizer t = new Tokenizer(r);
//		Condition c = ParserImpl.parseCondition(t);
//		assertTrue(i.eval(c));
//		
//		str = "3 > 2 or 4 != 7 and {3 != 4}";
//		r = new StringReader(str);
//		t = new Tokenizer(r);
//		c = ParserImpl.parseCondition(t);
//		assertTrue(i.eval(c));
//		
//		str = "3 >= 4 or 4 != 7 and {3 = 4}";
//		r = new StringReader(str);
//		t = new Tokenizer(r);
//		c = ParserImpl.parseCondition(t);
//		assertFalse(i.eval(c));
//		
//		str = "(13 mod 4 + 5) * 7 /4";
//		r = new StringReader(str);
//		t = new Tokenizer(r);
//		Expr e = ParserImpl.parseExpression(t);
//		assertTrue(i.eval(e) == 10);
//		
//		str = "(13 mod 4 + 5) * 7 /0";
//		r = new StringReader(str);
//		t = new Tokenizer(r);
//		e = ParserImpl.parseExpression(t);
//		assertTrue(i.eval(e) == 0);
//		
//		str = "(13 mod 4 + 5) * 7 mod 0";
//		r = new StringReader(str);
//		t = new Tokenizer(r);
//		e = ParserImpl.parseExpression(t);
//		assertTrue(i.eval(e) == 0);
//		
//		str = "3 - 2 + 7 / 8";
//		r = new StringReader(str);
//		t = new Tokenizer(r);
//		e = ParserImpl.parseExpression(t);
//		assertTrue(i.eval(e) == 1);
//		
//		str = "mem[3] := 2 * 6 mem[2/1] := 7 attack";
//		r = new StringReader(str);
//		t = new Tokenizer(r);
//		Command s = ParserImpl.parseCommand(t);
//		assertTrue(i.eval(s).equals("u3,12 u2,7 attack "));
//		
//		str = "mem[3] := 2 * 6 mem[2/1] := 7 serve[8]";
//		r = new StringReader(str);
//		t = new Tokenizer(r);
//		s = ParserImpl.parseCommand(t);
//		assertTrue(i.eval(s).equals("u3,12 u2,7 s8 "));
//	}
	
	/**
	 * Test the interpreter can return proper outcome after interpret a 
	 * critter
	 * @throws SyntaxError 
	 * @throws IOException 
	 */
	@Test 
	public void testGetProperOutcome() throws IOException, SyntaxError {
		console.Console c = new console.Console();
		c.loadWorld("world.txt");
		c.loadCritters("critter1.txt", 1);
		System.out.println("world and critters have been load");
		c.worldInfo();
		c.advanceTime(1);
	}

}
