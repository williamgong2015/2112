package test.testsA5;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import game.exceptions.SyntaxError;
import servlet.ast.Command;
import servlet.ast.Condition;
import servlet.ast.Expr;
import servlet.ast.ProgramImpl;
import servlet.element.Critter;
import servlet.interpreter.InterpreterImpl;
import servlet.interpreter.Outcome;
import servlet.parser.ParserImpl;
import servlet.parser.Tokenizer;
import servlet.world.World;

/**
 * Black box test of Interpreter 
 * use AST nodes as input, check every kind of interpret method if 
 * the outcomes is as expected
 *
 * Random test of Interpreter
 * use Mutation to generate random AST nodes, check the interpreter won't crash
 */
public class InterpreterTest {

	@Test
	public void test() throws SyntaxError, IOException {
		client.console.Console cx = new client.console.Console();
		cx.loadWorld("world.txt");
		cx.loadCritters("critter1.txt", 1);
		Critter critter = cx.world.order.get(0);
		
		InterpreterImpl i = new InterpreterImpl(cx.world, critter);
		
		
		String str = "3 > 2 and 4 != 7";
		StringReader r = new StringReader(str);
		Tokenizer t = new Tokenizer(r);
		Condition c = ParserImpl.parseCondition(t);
		assertTrue(i.eval(c));
		
		str = "3 > 2 or 4 != 7 and {3 != 4}";
		r = new StringReader(str);
		t = new Tokenizer(r);
		c = ParserImpl.parseCondition(t);
		assertTrue(i.eval(c));
		
		str = "3 >= 4 or 4 != 7 and {3 = 4}";
		r = new StringReader(str);
		t = new Tokenizer(r);
		c = ParserImpl.parseCondition(t);
		assertFalse(i.eval(c));
		
		str = "(13 mod 4 + 5) * 7 /4";
		r = new StringReader(str);
		t = new Tokenizer(r);
		Expr e = ParserImpl.parseExpression(t);
		assertTrue(i.eval(e) == 10);
		
		str = "(13 mod 4 + 5) * 7 /0";
		r = new StringReader(str);
		t = new Tokenizer(r);
		e = ParserImpl.parseExpression(t);
		assertTrue(i.eval(e) == 0);
		
		str = "(13 mod 4 + 5) * 7 mod 0";
		r = new StringReader(str);
		t = new Tokenizer(r);
		e = ParserImpl.parseExpression(t);
		assertTrue(i.eval(e) == 0);
		
		str = "3 - 2 + 7 / 8";
		r = new StringReader(str);
		t = new Tokenizer(r);
		e = ParserImpl.parseExpression(t);
		assertTrue(i.eval(e) == 1);
		
		str = "mem[3] := 2 * 6 mem[2/1] := 7 attack";
		r = new StringReader(str);
		t = new Tokenizer(r);
		Command s = ParserImpl.parseCommand(t);
		assertTrue(i.eval(s).equals("u3,12 u2,7 attack"));
		
		str = "mem[3] := 2 * 6 mem[2/1] := 7 serve[8]";
		r = new StringReader(str);
		t = new Tokenizer(r);
		s = ParserImpl.parseCommand(t);
		assertTrue(i.eval(s).equals("u3,12 u2,7 s8"));
		
		str = "mem[3] := mem[7] / 5";
		r = new StringReader(str);
		t = new Tokenizer(r);
		s = ParserImpl.parseCommand(t);
		assertTrue(i.eval(s).equals("u3," + (critter.getMem(7) / 5) + " "));

	}
	
	
	/**
	 * Create a program by mutate it for a thousand times,
	 * then interpret it with interpreter
	 * @throws SyntaxError 
	 * @throws IOException 
	 */
	@Test 
	public void testMutatedThousandTimesRule() throws IOException, 
	SyntaxError {
		int session_id = 0;
		World world = new World();
		Critter.loadCrittersIntoWorld(world, "example_critter.txt", 1, 
				session_id);
		Critter critter = world.order.get(0);
		System.out.println("world and critters have been load");
		InterpreterImpl in = new InterpreterImpl(world, critter);
		ProgramImpl pro = critter.getProgram();
		for (int i = 0; i < 1000; ++i) 
			pro = (ProgramImpl) pro.mutate();
		System.out.println(pro);
		Outcome outcome = in.interpret(pro);
		for (String s : outcome)
			System.out.println(s);
	}

}
