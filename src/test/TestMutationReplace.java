package test;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Test;

import ast.*;
import parse.ParserImpl;

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
	 */
	@Test
	public void testReplaceUnsupport() throws FileNotFoundException {
		FileReader f = new FileReader("src/test/test.txt");
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
		
		System.out.println("testReplaceUnsupport Succeed");
	}
	
	/**
	 * Test Two Rules being replaced
	 */
	@Test
	public void testReplace2Rules() throws FileNotFoundException {
		FileReader f = new FileReader("src/test/twoRules.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getReplace();
		String[] oldRules = t.toString().split("\\r?\\n");
		assertTrue("Rules 1 does not got replaced", 
				((Rule)t.nodeAt(1)).beMutated(m) == true);
		String[] newRules = t.toString().split("\\r?\\n");
		assertTrue("Replace does not match expectation", 
				newRules[0].equals(oldRules[1]) && 
				newRules[1].equals(oldRules[1]));
		
		System.out.println("testReplace2Rules Succeed");
	}
	
	/**
	 * Test Three Rules being replaced (test random selecting)
	 */
	@Test
	public void testReplace3Rules() throws FileNotFoundException {
		FileReader f = new FileReader("src/test/threeRules.txt");
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
		
		System.out.println("testReplace3Rules Succeed");
	}
	
	/**
	 * Test BinaryCommand, UnaryCommand, NullaryCommand being replaced
	 */
	@Test
	public void testReplaceCommand() throws FileNotFoundException {
		FileReader f = new FileReader("src/test/twoCommands.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);

		AbstractMutation m = (AbstractMutation) MutationFactory.getReplace();
		String[] oldCommands = t.nodeAt(6).toString().split("\\s+");
		String[] oldCommands2 = t.nodeAt(21).toString().split("\\s+");
		assertTrue("Command 1 does not got replaced", 
				((Command)t.nodeAt(7)).beMutated(m) == true);
		String[] newCommands = t.nodeAt(6).toString().split("\\s+");
		assertTrue("Replace does not match expectation", 
				(newCommands[0].equals(oldCommands[1]) || 
						newCommands[0].equals(oldCommands2[0]) ||
						newCommands[0].equals(oldCommands2[1])) && 
				newCommands[1].equals(oldCommands[1]));
		
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
		newCommands = t.nodeAt(6).toString().split("\\s+");
		assertTrue("Replace does not match expectation", 
				newCommands[0].equals(oldCommands2[0]) || 
				newCommands[0].equals(oldCommands2[1]));
		
		// remove the all the command expect one, check the last command
		// can not be replaced
		((ProgramImpl)t).getChild(1).beMutated(rm);
		assertTrue("The only command got replaced", 
				((Command)t.nodeAt(7)).beMutated(m) == false);
		
		System.out.println("testReplaceCommand Succeed");
	}
	
	private boolean checkIsOneOf(String[] possible, String tocheck) {
		for (int i = 0; i < possible.length; ++i) {
			if (possible[i].equals(tocheck))
				return true;
		}
		return false;
	}
	
	/**
	 * Test Condition can be replaced
	 */
	@Test
	public void testReplaceCondition() throws FileNotFoundException {
		FileReader f = new FileReader("src/test/twoConditions.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getReplace();
		String[] possibleConditions = {"ahead[1] != 1 or ahead[1] != 2", "ahead[1] != 1",
				"ahead[1] != 2", "ahead[1] != 0 - 1", "ahead[1] > 0"
		};
		assertTrue("Condtion does not got replaced", 
				((Condition)t.nodeAt(2)).beMutated(m) == true);
		
		assertTrue("New condition does not match expectation", 
				checkIsOneOf(possibleConditions, t.nodeAt(2).toString()));
		
		System.out.println("testReplaceCondition Succeed");
	}
	
	
	/**
	 * Test Expr can be replaced
	 */
	@Test
	public void testReplaceExpr() throws FileNotFoundException {
		FileReader f = new FileReader("src/test/oneExpr.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getReplace();
		String[] possibleExprs = {"mem[mem[3] + 10]", "mem[3] + 10",
				"mem[3]", "3", "10", "6"
		};
		assertTrue("Expr does not got replaced", 
				((Expr)t.nodeAt(3)).beMutated(m) == true);
		
		
		assertTrue("New Expr does not match expectation", 
				checkIsOneOf(possibleExprs, t.nodeAt(3).toString()));
		
		System.out.println("testReplaceExpr Succeed");
	}
}
