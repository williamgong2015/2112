package test.testsA4;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import org.junit.Test;

import game.exceptions.SyntaxError;
import servlet.ast.*;
import servlet.ast.Number;
import servlet.parser.ParserImpl;
import servlet.parser.Tokenizer;

/**
 * JUnit Test for MutationSwap
 * Testing swap method on all the AST nodes.
 *
 */
public class TestMutationSwap {

	/**
	 * ProgramImpl can be swapped if and only if it has more than 1 child
	 * @throws FileNotFoundException
	 * @throws SyntaxError 
	 */
	@Test
	public void testSwapProgramImpl() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/test/testsA4/twoRules.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getSwap();
		String oldTree = t.toString();
		String[] oldRules = t.toString().split("\\r?\\n");
		assertTrue("2 Rules does not got swaped", 
				((ProgramImpl)t).beMutated(m) == true);
		String[] newRules = t.toString().split("\\r?\\n");
		assertTrue("Swap does not match expectation", 
				oldRules[0].equals(newRules[1]) && 
				oldRules[1].equals(newRules[0]));
		((ProgramImpl)t).beMutated(m);
		assertTrue("Swap the ProgramImpl again does not become the origin",
				oldTree.equals(t.toString()));
		
		// delete one rule
		AbstractMutation rm = (AbstractMutation) MutationFactory.getRemove();
		((Rule) ((ProgramImpl) t).getChild(0)).beMutated(rm);
		assertTrue("Delete does not match expectation",
				oldRules[1].trim().equals(t.toString().trim()));
		oldTree = t.toString();
		assertTrue("1 Rules does got swaped", 
				((ProgramImpl)t).beMutated(m) == false);
		assertTrue("The tree got changed",
				oldTree.equals(t.toString()));
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = servlet.parser.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testSwapProgramImpl Succeed");
	}
	
	/**
	 * Commands can be swapped if and only if it has more than 1 child
	 * @throws FileNotFoundException
	 * @throws SyntaxError 
	 */
	@Test
	public void testSwapCommands() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/test/testsA4/twoCommands.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getSwap();
		Commands c = (Commands) t.nodeAt(6);
		String oldTree = t.toString();
		String[] oldCommands = {"mem[6] := mem[4]", "mem[5] := 2"};
		assertTrue("2 Commands does not got swaped", c.beMutated(m) == true);
		String[] newCommands = {t.nodeAt(7).toString(), t.nodeAt(10).toString()};
		assertTrue("Swap does not match expectation", 
				oldCommands[0].trim().equals(newCommands[1].trim()) && 
				oldCommands[1].trim().equals(newCommands[0].trim()));
		c.beMutated(m);
		assertTrue("Swap the Commands again does not become the origin",
				oldTree.equals(t.toString()));
		
		// delete one command
		AbstractMutation rm = (AbstractMutation) MutationFactory.getRemove();
		((Command) c.getChild(0)).beMutated(rm);
		assertTrue("Delete does not match expectation",
				oldCommands[1].trim().equals(c.toString().trim()));
		oldTree = t.toString();
		assertTrue("1 Command does got swaped", 
				c.beMutated(m) == false);
		assertTrue("The tree got changed",
				oldTree.equals(t.toString()));
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = servlet.parser.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testSwapCommands Succeed");
	}
	
	/**
	 * Test some nodes that should not support swap
	 * @throws SyntaxError 
	 */
	@Test
	public void testSwapUnsupport() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/test/testsA4/mutationTest.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getSwap();
		String oldTree = t.toString();
		// Rule
		assertTrue("Rule does got swaped", ((Rule) t.nodeAt(1)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// UnaryExpr
		assertTrue("Rule does got swaped", ((UnaryExpr) t.nodeAt(109)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// Number 
		assertTrue("Rule does got swaped", ((Number) t.nodeAt(110)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// UnaryCommand
		assertTrue("Rule does got swaped", ((UnaryCommand) t.nodeAt(79)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// NullaryCommand
		assertTrue("Rule does got swaped", ((NullaryCommand) t.nodeAt(37)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = servlet.parser.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testSwapUnsupport Succeed");
	}
	
	/**
	 * Test Binary Operation can be swap 
	 * These Binary Operation includes 
	 *     BinaryExpr, BinaryCommand, BinaryCondition, Relation
	 * @throws SyntaxError 
	 */
	@Test
	public void testSwapBinaryOperation() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/test/testsA4/mutationTest.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getSwap();
		
		// BinaryExpr
		String[] oldExprs = t.nodeAt(108).toString().split("\\s+");
		assertTrue("BinaryExpr doesn't got swaped", 
				((BinaryExpr) t.nodeAt(108)).beMutated(m) == true);
		String[] newExprs = t.nodeAt(108).toString().split("\\s+");
		assertTrue("Swapped node does not turn out as expected", 
				oldExprs[0].trim().equals(newExprs[2].trim()));
		
		// BinaryCommand
		String[] oldCommands = 
			{((BinaryCommand) t.nodeAt(7)).getFirChild().toString(),
				((BinaryCommand) t.nodeAt(7)).getSecChild().toString()};
		assertTrue("BinaryCommand doesn't got swaped", 
				((BinaryCommand) t.nodeAt(7)).beMutated(m) == true);
		String[] newCommands = 
			{((BinaryCommand) t.nodeAt(7)).getFirChild().toString(),
					((BinaryCommand) t.nodeAt(7)).getSecChild().toString()};
		assertTrue("Swapped node does not turn out as expected", 
				oldCommands[0].trim().equals(newCommands[1].trim()));
		
		// BinaryCondition 
		String[] oldCondition = 
			{((BinaryCondition) t.nodeAt(55)).getFirChild().toString(),
				((BinaryCondition) t.nodeAt(55)).getSecChild().toString()};
		assertTrue("BinaryCommand doesn't got swaped", 
				((BinaryCondition) t.nodeAt(55)).beMutated(m) == true);
		String[] newCondition = 
			{((BinaryCondition) t.nodeAt(55)).getFirChild().toString(),
					((BinaryCondition) t.nodeAt(55)).getSecChild().toString()};
		assertTrue("Swapped node does not turn out as expected", 
				oldCondition[0].trim().equals(newCondition[1].trim()));
		
		// Relation
		String[] oldRelation = 
			{((Relation) t.nodeAt(74)).getFirChild().toString(),
				((Relation) t.nodeAt(74)).getSecChild().toString()};
		assertTrue("BinaryCommand doesn't got swaped", 
				((Relation) t.nodeAt(74)).beMutated(m) == true);
		String[] newRelation = 
			{((Relation) t.nodeAt(74)).getFirChild().toString(),
					((Relation) t.nodeAt(74)).getSecChild().toString()};
		assertTrue("Swapped node does not turn out as expected", 
				oldRelation[0].trim().equals(newRelation[1].trim()));
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = servlet.parser.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testSwapBinaryOperation Succeed");
	}
	
}
