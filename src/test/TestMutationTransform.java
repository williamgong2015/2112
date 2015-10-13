package test;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Test;

import ast.*;
import parse.ParserImpl;

/**
 * JUnit Test for MutationTransform
 * Testing transform method on all the AST nodes.
 * 
 * Nodes that shouldn't support replace
 *     ProgramImpl, Rule, Commands, BinaryCommand 
 *     
 * Nodes that should support replace
 *     NullaryCommand, UnaryCommand, Relation, BinaryCondition, 
 *     UnaryExpr, BinaryExpr, Number
 *
 */
public class TestMutationTransform {

	/**
	 * Test some nodes that should not support transform
	 */
	@Test
	public void testTransformUnsupport() throws FileNotFoundException {
		FileReader f = new FileReader("src/test/test.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
	//		for (int i = 0; i < t.size(); ++i) {
	//		System.out.println("i = " + i);
	//		System.out.println(t.nodeAt(i));
	//	}
		AbstractMutation m = (AbstractMutation) MutationFactory.getTransform();
		String oldTree = t.toString();
		// ProgramImpl
		assertTrue("ProgramImpl does got replaced", ((ProgramImpl) t.nodeAt(0)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// Rule
		assertTrue("Rule does got replaced", ((Rule) t.nodeAt(1)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// Commands
		assertTrue("Commands does got replaced", ((Commands) t.nodeAt(6)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// BinaryCommand
		assertTrue("Commands does got replaced", ((BinaryCommand) t.nodeAt(7)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		
		System.out.println("testTransformUnsupport Succeed");
	}
	
	
	
	
	
}
