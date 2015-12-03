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
	 * @throws SyntaxError 
	 */
	@Test
	public void testTransformUnsupport() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/test/testsA4/mutationTest.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getTransform();
		String oldTree = t.toString();
		// ProgramImpl
		assertTrue("ProgramImpl does got transfromed", ((ProgramImpl) t.nodeAt(0)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// Rule
		assertTrue("Rule does got transfromed", ((Rule) t.nodeAt(1)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// Commands
		assertTrue("Commands does got transfromed", ((Commands) t.nodeAt(6)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// BinaryCommand
		assertTrue("Commands does got transfromed", ((BinaryCommand) t.nodeAt(7)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = servlet.parser.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testTransformUnsupport Succeed");
	}
	
	
	/**
	 * Subroutine to test Nodes that support transforming mutation
	 * @param index 
	 *     index of the node to test counting from root 
	 * @param className
	 *     the class of node being test, used when logging error message
	 * @throws SyntaxError 
	 */
	private void testTransform(int index, String className) 
			throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/test/testsA4/mutationTest.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);

		AbstractMutation m = (AbstractMutation) MutationFactory.getTransform();
		Object oldType = ((GenericOperation) t.nodeAt(index)).getType();
		assertTrue(className + " does not got transfromed", 
				((MutableNode) t.nodeAt(index)).beMutated(m) == true);
		Object[] types = ((GenericOperation) t.nodeAt(index)).getAllPossibleType();
		Object newType = ((GenericOperation) t.nodeAt(index)).getType();
		assertTrue("the changed type does not match expectation", 
				!oldType.equals(newType) && 
				game.utils.EqualityCheck.checkIsOneOf(types, newType));
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = servlet.parser.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testTransform" + className + " Succeed");
	}
	
	/**
	 * Test NullaryCommand, at node[37]: right
	 * @throws SyntaxError 
	 */
	@Test
	public void testTransformNullaryCommand() throws FileNotFoundException, SyntaxError {
		testTransform(37, "NullaryCommand");
	}
	
	/**
	 * Test UnaryCommand, at node[79]: tag[1]
	 * @throws SyntaxError 
	 */
	@Test
	public void testTransformUnaryCommand() throws FileNotFoundException, SyntaxError {		
		testTransform(79, "UnaryCommand");
	}
	
	/**
	 * Test Relation, at node[101]: mem[4] > 1000
	 * @throws SyntaxError 
	 */
	@Test
	public void testTransformRelation() throws FileNotFoundException, SyntaxError {		
		testTransform(101, "Relation");
	}
	
	/**
	 * Test BinaryCondition, at node[79]: tag[1]
	 * @throws SyntaxError 
	 */
	@Test
	public void testTransformBinaryCondition() throws FileNotFoundException, SyntaxError {		
		testTransform(79, "BinaryCondition");
	}
	
	/**
	 * Test BinaryExpr, with node[108]: mem[7] + mem[5]
	 * @throws SyntaxError 
	 */
	@Test
	public void testTransformBinaryExpr() throws FileNotFoundException, SyntaxError {		
		testTransform(108, "BinaryExpr");
	}
	
	/**
	 * Test UnaryExpr, with node[109]: mem[7]
	 * @throws SyntaxError 
	 */
	@Test
	public void testTransformUnaryExpr() throws FileNotFoundException, SyntaxError {		
		testTransform(109, "UnaryExpr");
	}
	
	/**
	 * Test Number, with node[103]: 4
	 * @throws SyntaxError 
	 */
	@Test
	public void testTransformNumber() throws FileNotFoundException, SyntaxError {		
		FileReader f = new FileReader("src/test/testsA4/mutationTest.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getTransform();
		int oldVal = ((Number) t.nodeAt(103)).getVal();
		assertTrue("Number" + " does not got transfromed", 
				((MutableNode) t.nodeAt(103)).beMutated(m) == true);
		int newVal = ((Number) t.nodeAt(103)).getVal();
		assertTrue("the number doesn't changed", oldVal != newVal);
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = servlet.parser.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testTransform" + "Number" + " Succeed");
	}
	
}
