package test.testsA4;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import org.junit.Test;

import game.exceptions.SyntaxError;
import servlet.ast.*;
import servlet.parser.ParserImpl;
import servlet.parser.Tokenizer;

/**
 * JUnit Test for MutationRemove
 * Testing remove method on all the AST nodes.
 *
 */
public class TestMutationRemove {
	
	/**
	 * ProgramImpl shound't be remove
	 * @throws FileNotFoundException
	 * @throws SyntaxError 
	 */
	@Test
	public void testRemoveProgramImpl() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/testsA4/mutationTest.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		String oldTree = t.toString();
		AbstractMutation m = (AbstractMutation) MutationFactory.getRemove();
		assertTrue("ProgramImpl got removed", 
				((ProgramImpl)t).beMutated(m) == false);
		assertTrue("Tree got changed", oldTree.equals(t.toString()));
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = servlet.parser.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testRemoveProgramImpl Succeed");
	}
	
	/**
	 * Rule can be removed, If Rule 1 got removed, old Rule 2 become Rule 1
	 * @throws FileNotFoundException
	 * @throws SyntaxError 
	 */
	@Test
	public void testRemoveRule() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/testsA4/mutationTest.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		Rule r1 = (Rule) t.nodeAt(1);
		Rule r2 = (Rule) t.nodeAt(14);
		AbstractMutation m = (AbstractMutation) MutationFactory.getRemove();
		assertTrue("ProgramImpl got removed", r1.beMutated(m) == true);
		assertTrue("Rule 2 doesn't become Rule 1", t.nodeAt(1).equals(r2));
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = servlet.parser.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testRemoveRule Succeed");
	}
	
	/**
	 * Command can be removed if and only if it has more than 1 fellow 
	 * @throws FileNotFoundException
	 * @throws SyntaxError 
	 */
	@Test
	public void testRemoveCommand() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/testsA4/mutationTest.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		Command c1 = (Command) t.nodeAt(11);
		Command c2 = (Command) t.nodeAt(7);

		AbstractMutation m = (AbstractMutation) MutationFactory.getRemove();

		assertTrue("Command can't be removed when there are more than 1", 
				c1.beMutated(m) == true);
		assertTrue("Rule 1 does not come out as expected", 
				t.nodeAt(1).toString().trim()
				.equals("mem[5] = 1 --> mem[6] := mem[4];"));
		assertTrue("The only command got removed", c2.beMutated(m) == false);

		assertTrue("Rule 1 does not come out as expected", 
				t.nodeAt(1).toString()
				.equals("mem[5] = 1 --> mem[6] := mem[4];\n"));
		
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = servlet.parser.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));

		System.out.println("testRemoveCommand Succeed");
	}
	
	/**
	 * Condition can be removed if and only if it is not Relation
	 * @throws FileNotFoundException
	 * @throws SyntaxError 
	 */
	@Test
	public void testRemoveCondition() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/testsA4/mutationTest.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		// mem[5] = 1, a relation
		Condition c1 = (Condition) t.nodeAt(2);  
		// { ahead[1] != 1 or ahead[1] != 2 }, it is a binaryCondition
		Condition c2 = (Condition) t.nodeAt(56); 
		AbstractMutation m = (AbstractMutation) MutationFactory.getRemove();
		String oldTree = t.toString();
		assertTrue("Relation got removed", c1.beMutated(m) == false);
		assertTrue("Tree got changed", oldTree.equals(t.toString()));
		assertTrue("BinaryCondition doesn't got removed", c2.beMutated(m) == true);
		assertTrue("New Condition doesn't turn out as exprected", 
				t.nodeAt(56).toString().equals("ahead[1] != 1"));
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = servlet.parser.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testRemoveCondition Succeed");
	}
	
	/**
	 * Expression can be removed if and only if it is a UnaryExpr
	 * or a BinaryExpr  
	 * @throws FileNotFoundException
	 * @throws SyntaxError 
	 */
	@Test
	public void testRemoveExpr() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/testsA4/mutationTest.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		// 5, an Number, can't be removed
		Expr e1 = (Expr) t.nodeAt(4);
		// mem[5], an UnaryExpr, can be removed and become 5
		Expr e2 = (Expr) t.nodeAt(3);  
		// mem[8] mod 6, it is a BinaryExpr, can be removed
		Expr e3 = (Expr) t.nodeAt(40); 
		AbstractMutation m = (AbstractMutation) MutationFactory.getRemove();
		String oldTree = t.toString();
		assertTrue("Number got removed", e1.beMutated(m) == false);
		assertTrue("Tree got changed", oldTree.equals(t.toString()));
		assertTrue("BinaryExpr doesn't got removed", e3.beMutated(m) == true);
		assertTrue("New Expr doesn't turn out as exprected", 
				t.nodeAt(40).toString().equals("mem[8]") || 
				t.nodeAt(40).toString().equals("6"));
		assertTrue("UnaryExpr doesn't got removed", e2.beMutated(m) == true);
		assertTrue("New Expr doesn't turn out as exprected", 
				t.nodeAt(3).toString().equals("5"));
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = servlet.parser.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testRemoveExpr Succeed");
	}

}
