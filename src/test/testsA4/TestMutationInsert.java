package test.testsA4;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import org.junit.Test;

import game.exceptions.SyntaxError;
import servlet.ast.*;
import servlet.parser.ParserImpl;
import servlet.parser.Tokenizer;

/**
 * JUnit test for class MutationInsert
 *
 * The nodes don't support insert mutation are
 *     ProgramImpl, Rule, Commands, NullaryCommand, UnaryCommand, BinaryCommand
 *
 * The nodes support insert mutation are 
 *     BinaryCondition, Relation, Number, UnaryExpr, BinaryExpr
 */
public class TestMutationInsert {
	/**
	 * Test some nodes that should not support transform
	 * @throws SyntaxError 
	 */
	@Test
	public void testInsertUnsupport() throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/testsA4/mutationTest.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		AbstractMutation m = (AbstractMutation) MutationFactory.getInsert();
		String oldTree = t.toString();
		// ProgramImpl
		assertTrue("ProgramImpl does got inserted", ((ProgramImpl) t.nodeAt(0)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// Rule
		assertTrue("Rule does got inserted", ((Rule) t.nodeAt(1)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// Commands
		assertTrue("Commands does got inserted", ((Commands) t.nodeAt(6)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// BinaryCommand
		assertTrue("BinaryCommand does got inserted", ((BinaryCommand) t.nodeAt(7)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// UnaryCommand
		assertTrue("UnaryCommand does got inserted", ((UnaryCommand) t.nodeAt(79)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		// NullaryCommand
		assertTrue("NullaryCommand does got inserted", ((NullaryCommand) t.nodeAt(53)).beMutated(m) == false);
		assertTrue("The tree got changed", oldTree.equals(t.toString()));
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = servlet.parser.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testInsertUnsupport Succeed");
	}
	
	
	/**
	 * Test MutationInsert BinaryCondition node, the inserted node should be 
	 * a BinaryCondition, but its type may various
	 * 
	 * Testing the BinaryCondition at node[3]: { ahead[1] != 1 or ahead[1] != 2 }
	 * @throws SyntaxError 
	 */
	@Test
	public void testInsertBinaryCondition() 
			throws FileNotFoundException, SyntaxError {
		testInsertCondition(3, "BinaryCondition");
	}
	
	/**
	 * Test MutationInsert Relation node, the inserted node should be 
	 * a BinaryCondition, but its type may various
	 * 
	 * Testing the Relation at node[4]: ahead[1] != 1
	 * @throws FileNotFoundException 
	 * @throws SyntaxError 
	 */
	@Test
	public void testInsertRelation() throws FileNotFoundException, SyntaxError {
		testInsertCondition(4, "Relation");
	}
	
	/**
	 * Share the common test method for BinaryCondition and Relation
	 * @param index the index of the node being test on counting start from root
	 * @param className the name of the class being test on
	 * @throws FileNotFoundException
	 * @throws SyntaxError 
	 */
	private void testInsertCondition(int index, String className) throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/testsA4/twoConditions.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		String oldNodeStr = t.nodeAt(index).toString().replaceAll("[{}]", "").trim();
		AbstractMutation m = (AbstractMutation) MutationFactory.getInsert();
		assertTrue(className + " fail to got inserted", 
				((MutableNode) t.nodeAt(index)).beMutated(m) == true);	
		Object[] types = ((GenericOperation) t.nodeAt(index)).getAllPossibleType();
		Object newType = ((GenericOperation) t.nodeAt(index)).getType();
		// see if the inserted type can various
		assertTrue("the changed type does not match expectation", 
				game.utils.EqualityCheck.checkIsOneOf(types, newType));
		
		// see if one of the children of inserted node is a copy of 
		// of the old node and the other is a random selected node 
		// of the same type
		String newChild1Str = ((BinaryOperation) t.nodeAt(index)).getFirChild()
				.toString().replaceAll("[{}]", "").trim();
		String newChild2Str = ((BinaryOperation) t.nodeAt(index)).getSecChild()
				.toString().replaceAll("[{}]", "").trim();
		
		String[] possibleConditions = 
			{"{ ahead[1] != 1 or ahead[1] != 2 } and ahead[1] != 0 - 1", 
					"{ ahead[1] != 1 or ahead[1] != 2 }", "ahead[1] != 1",
					"ahead[1] != 2", "ahead[1] != 0 - 1", "ahead[1] > 0"
			};
		for (int i = 0; i < possibleConditions.length; ++i) 
			possibleConditions[i] = 
					possibleConditions[i].replaceAll("[{}]", "").trim();
		assertTrue("the node being mutated doesn't become a "
				+ "child of inserted node", 
				newChild1Str.equals(oldNodeStr) || 
				newChild2Str.equals(oldNodeStr));
		assertTrue("the new inserted child is not copy of one of the "
				+ "same kind of node", 
				game.utils.EqualityCheck.checkIsOneOf(possibleConditions, newChild1Str) && 
				game.utils.EqualityCheck.checkIsOneOf(possibleConditions, newChild2Str));
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = servlet.parser.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testInsert" + className + " Succeed");
	}
	
	/**
	 * Share the common test method for BinaryExpr and UnaryExpr and Number
	 * @param index the index of the node being test on counting start from root
	 * @param className the name of the class being test on
	 * @throws FileNotFoundException
	 * @throws SyntaxError 
	 */
	private void testInsertExpr(int index, String className) throws FileNotFoundException, SyntaxError {
		FileReader f = new FileReader("src/testsA4/oneExpr.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);

		String oldNodeStr = t.nodeAt(index).toString().replaceAll("[()]", "").trim();
		AbstractMutation m = (AbstractMutation) MutationFactory.getInsert();
		assertTrue(className + " fail to got inserted", 
				((MutableNode) t.nodeAt(index)).beMutated(m) == true);	
		// BinaryExpr and UnaryExpr need to check their variated type
		if (className.equals("BinaryExpr") || className.equals("UnaryExpr")) {
			Object[] types = ((GenericOperation) t.nodeAt(index)).getAllPossibleType();
			Object newType = ((GenericOperation) t.nodeAt(index)).getType();
			// see if the inserted type can be various
			assertTrue("the changed type does not match expectation", 
					game.utils.EqualityCheck.checkIsOneOf(types, newType));
		}
		String[] possibleExprs = 
			{"mem[mem[3] + 10] mod 6", "mem[mem[3] + 10]", "mem[3] + 10",
					"mem[3]", "3", "6", "10"};
		
		// if inserted a BinaryExpr see if one of the children of inserted 
		// node is a copy of the old node and the other is a random selected 
		// node of the same type
		if (t.nodeAt(index) instanceof BinaryExpr) {
			String newChild1Str = ((BinaryOperation) t.nodeAt(index)).getFirChild()
					.toString().replaceAll("[()]", "").trim();
			String newChild2Str = ((BinaryOperation) t.nodeAt(index)).getSecChild()
					.toString().replaceAll("[()]", "").trim();
			assertTrue("the node being mutated doesn't become a "
					+ "child of inserted node", 
					newChild1Str.equals(oldNodeStr) || 
					newChild2Str.equals(oldNodeStr));
			assertTrue("the new inserted child is not copy of one of the "
					+ "same kind of node", 
					game.utils.EqualityCheck.checkIsOneOf(possibleExprs, newChild1Str) && 
					game.utils.EqualityCheck.checkIsOneOf(possibleExprs, newChild2Str));
		}
		// if inserted an UnaryExpr, see if the child is a copy of the old node
		else if (t.nodeAt(index) instanceof UnaryExpr){
			String newChildStr = ((UnaryOperation) t.nodeAt(index)).getChild()
					.toString().replaceAll("[()]", "").trim();
			assertTrue("the node being mutated doesn't become a "
					+ "child of inserted node", newChildStr.equals(oldNodeStr));
		}
		else {
			assertTrue("inserted node is not BinaryExpr nor UnaryExpr", false);
		}
		
		// pretty print the tree and then parse it again to check if the tree is legal
		String old = t.toString();
		StringReader s = new StringReader(old);
		Tokenizer token = new Tokenizer(s);
		ProgramImpl program = servlet.parser.ParserImpl.parseProgram(token);
		assertTrue("Mutated Tree is illegal and can't be parsed", 
				old.equals(program.toString()));
		
		System.out.println("testInsert" + className + " Succeed");
	}
	
	
	/**
	 * Testing on BinaryExpr at node[5]: mem[3] + 10
	 * @throws FileNotFoundException
	 * @throws SyntaxError 
	 */
	@Test
	public void testInsertBinaryExpr() throws FileNotFoundException, SyntaxError {
		testInsertExpr(5, "BinaryExpr");
	}
	
	/**
	 * Testing on UnaryExpr at node[6]: mem[3]
	 * @throws FileNotFoundException
	 * @throws SyntaxError 
	 */
	@Test
	public void testInsertUnaryExpr() throws FileNotFoundException, SyntaxError {
		testInsertExpr(6, "UnaryExpr");
	}
	
	/**
	 * Testing on Number at node[7]: 3
	 * @throws FileNotFoundException
	 * @throws SyntaxError 
	 */
	@Test
	public void testInsertNumber() throws FileNotFoundException, SyntaxError {
		testInsertExpr(7, "Number");
	}
	
}
