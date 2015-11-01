package testsA4;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import org.junit.Test;

import ast.*;
import exceptions.SyntaxError;
import parse.ParserImpl;
import parse.Tokenizer;

/**
 * JUnit Test for MutationReplace
 * Testing replace method on all the AST nodes.
 * 
 * Nodes that shouldn't support replace
 *     ProgramImpl, Commands
 *     
 * Nodes that should support replace
 *     Rule, BinaryCommand, UnaryCommand, NullaryCommand, 
 *     BinaryCondition, Relation, 
 *
 */
public class TestMutationReplace {
	
	/**
	 * Test some nodes that should not support replace
	 * @throws SyntaxError 
	 */
	@Test
	public void testReplaceUnsupport() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/testsA4/mutationTest.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getReplace();
		String oldTree = t.toString();
		// ProgramImpl
		assertTrue("ProgramImpl does got replaced", ((ProgramImpl) t.nodeAt(0)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// Commands
		assertTrue("Commands does got replaced", ((Commands) t.nodeAt(6)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = parse.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testReplaceUnsupport Succeed");
	}
	
	/**
	 * Test Two Same Rules can't finish replace mutation
	 */
	@Test
	public void testReplace2SameRules() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/testsA4/twoSameRules.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getReplace();
		assertTrue("Rules got replaced", 
				((Rule)t.nodeAt(1)).beMutated(m) == false);
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = parse.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testReplace2SameRules Succeed");

	}
	
	/**
	 * Test Three Rules can be replaced (test random selecting)
	 * @throws SyntaxError 
	 */
	@Test
	public void testReplace3Rules() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/testsA4/threeRules.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getReplace();
		String[] oldRules = t.toString().split("\\r?\\n");
		assertTrue("Rules 1 does not got replaced", 
				((Rule)t.nodeAt(1)).beMutated(m) == true);
		String[] newRules = t.toString().split("\\r?\\n");
		assertTrue("Replace does not match expectation", 
				newRules[0].equals(oldRules[1]) || 
				newRules[0].equals(oldRules[2]));
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = parse.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testReplace3Rules Succeed");
	}
	
	/**
	 * Test BinaryCommand, UnaryCommand, NullaryCommand being replaced
	 * @throws SyntaxError 
	 */
	@Test
	public void testReplaceCommand() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/testsA4/twoCommands.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);

		AbstractMutation m = (AbstractMutation) MutationFactory.getReplace();
		String[] oldCommands = {"men[6] := mem[4]", "mem[5] := 2"};
		String[] oldCommands2 = {"mem[5] := 1", "wait"};
		
		assertTrue("Command 1 does not got replaced", 
				((Command)t.nodeAt(7)).beMutated(m) == true);
		String newCommands = t.nodeAt(7).toString();
		assertTrue("Replace does not match expectation", 
				newCommands.equals(oldCommands[1]) || 
						newCommands.equals(oldCommands2[0]) ||
						newCommands.equals(oldCommands2[1]));
		// delete one command
		AbstractMutation rm = (AbstractMutation) MutationFactory.getRemove();
		Commands c = (Commands) t.nodeAt(6);
		((Command) t.nodeAt(7)).beMutated(rm);

		assertTrue("Delete does not match expectation",
				oldCommands[1].trim().equals(c.toString().trim()));
		String oldTree = t.toString();
		assertTrue("1 Command does got replaced", 
				c.beMutated(m) == false);
		assertTrue("The tree got changed",
				oldTree.equals(t.toString()));
		// replace the only command in commands with command in other rules
		oldCommands = t.nodeAt(6).toString().split("\\s+");
		assertTrue("Command 1 does not got replaced", 
				((Command)t.nodeAt(7)).beMutated(m) == true);
		newCommands = t.nodeAt(7).toString();
		assertTrue("Replace does not match expectation", 
				newCommands.equals(oldCommands2[0]) || 
				newCommands.equals(oldCommands2[1]));
		// remove the all the command expect one, check the last command
		// can not be replaced
		((ProgramImpl)t).getChild(1).beMutated(rm);
		assertTrue("The only command got replaced", 
				((Command)t.nodeAt(7)).beMutated(m) == false);
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = parse.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testReplaceCommand Succeed");
	}
	
	/**
	 * Test Condition can be replaced
	 * @throws SyntaxError 
	 */
	@Test
	public void testReplaceCondition() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/testsA4/twoConditions.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getReplace();
		String[] possibleConditions = {"ahead[1] != 1 or ahead[1] != 2", "ahead[1] != 1",
				"ahead[1] != 2", "ahead[1] != 0 - 1", "ahead[1] > 0"
		};
		assertTrue("Condtion does not got replaced", 
				((Condition)t.nodeAt(2)).beMutated(m) == true);
		assertTrue("New condition does not match expectation", 
				util.EqualityCheck.checkIsOneOf(possibleConditions, 
						t.nodeAt(2).toString()));
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = parse.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testReplaceCondition Succeed");
	}
	
	
	/**
	 * Test Expr can be replaced
	 * @throws SyntaxError 
	 */
	@Test
	public void testReplaceExpr() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/testsA4/oneExpr.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getReplace();
		String[] possibleExprs = {"mem[mem[3] + 10]", "mem[3] + 10",
				"mem[3]", "3", "10", "6"
		};
		assertTrue("Expr does not got replaced", 
				((Expr)t.nodeAt(3)).beMutated(m) == true);
		
		
		assertTrue("New Expr does not match expectation", 
				util.EqualityCheck.checkIsOneOf(possibleExprs, 
						t.nodeAt(3).toString()));
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = parse.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testReplaceExpr Succeed");
	}
}
