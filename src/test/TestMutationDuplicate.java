package test;


import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Test;

import ast.*;
import ast.Number;
import parse.ParserImpl;
/**
 * JUnit test for class MutationDuplicate
 *
 * The nodes don't support duplicate mutation are
 *     Rule, NullaryCommand, UnaryCommand, BinaryCommand
 *     BinaryCondition, Relation, Number, UnaryExpr, BinaryExpr
 *
 * The nodes support insert mutation are 
 *     ProgramImpl, Commands
 */
public class TestMutationDuplicate {

	
	/**
	 * Test some nodes that should not support duplicate
	 */
	@Test
	public void testDuplicateUnsupport() throws FileNotFoundException {
		FileReader f = new FileReader("src/test/mutationTest.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getDuplicate();
		String oldTree = t.toString();

		// Rule
		assertTrue("Rule does got duplicate", ((Rule) t.nodeAt(1)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// BinaryCommand
		assertTrue("BinaryCommand does got duplicate", ((BinaryCommand) t.nodeAt(7)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// UnaryCommand
		assertTrue("UnaryCommand does got duplicate", ((UnaryCommand) t.nodeAt(79)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// NullaryCommand
		assertTrue("NullaryCommand does got duplicate", ((NullaryCommand) t.nodeAt(53)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// BinaryCondition
		assertTrue("BinaryCondition does got duplicate", ((BinaryCondition) t.nodeAt(55)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// Relation
		assertTrue("Relation does got duplicate", ((Relation) t.nodeAt(57)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// Number
		assertTrue("Number does got duplicate", ((Number) t.nodeAt(63)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// UnaryExpr
		assertTrue("UnaryExpr does got duplicate", ((UnaryExpr) t.nodeAt(62)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// NullaryCommand
		assertTrue("BinaryExpr does got duplicate", ((BinaryExpr) t.nodeAt(68)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		
		System.out.println("testDuplicateUnsupport Succeed");
	}
	
	
	/**
	 * Test Duplicate method of Commands
	 */
	@Test
	public void testDuplicateCommands() throws FileNotFoundException {
		FileReader f = new FileReader("src/test/twoCommands.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getDuplicate();

		// if the last command is action, does not support duplicate mutation
		assertTrue("commands doesn't got mutated", !((Commands) t.nodeAt(21)).beMutated(m));
		
		// if the last command is update, support duplicate mutation
		String[] list = {"mem[6] := mem[4]", "mem[5] := 2"};
		assertTrue("commands doesn't got mutated", ((Commands) t.nodeAt(6)).beMutated(m));
		Commands c = (Commands) t.nodeAt(6);
		// the duplicate item is chosen from one of the command in the ArrayList
		assertTrue("new added command isn't from the list", 
				util.EqualityCheck.checkIsOneOf(list, 
						c.getChild(c.getChildren().size()-1).toString()));
		
		System.out.println("testingDuplicateCommands Succeed");
	}
	
	
	/**
	 * Test Duplicate method of ProgramImpl
	 */
	@Test
	public void testDuplicateProgramImpl() throws FileNotFoundException {
		FileReader f = new FileReader("src/test/threeRules.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getDuplicate();

		// ProgramImpl should be able to get mutated
		assertTrue("ProgramImpl doesn't got mutated", 
				((ProgramImpl) t.nodeAt(0)).beMutated(m));
		String[] list = {"mem[5] = 1 --> mem[6] := mem[4] mem[5] := 2;",
				"mem[6] mod 1000 < 6 --> mem[5] := 1 eat;",
				"mem[mem[3] + 10] mod 6 <= 3 --> right;"};
		// the duplicate item is chosen from one of the command in the ArrayList
		ProgramImpl pro = (ProgramImpl) t.nodeAt(0);
		assertTrue("new added command isn't from the list", 
				util.EqualityCheck.checkIsOneOf(list, 
						pro.getChild(pro.getChildren().size()-1).toString().trim()));
		
		System.out.println("testDuplicateProgramImpl Succeed");
	}
	
	
	
}
