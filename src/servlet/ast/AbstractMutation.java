package servlet.ast;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;

import servlet.parser.Tokenizer;

/**
 * Abstract class of Mutation
 * 
 * Reference: 
 * http://stackoverflow.com/questions/4040001/creating-random-numbers-with-no-duplicates
 *
 */
public abstract class AbstractMutation implements Mutation{
	
	public boolean equals(Mutation m) {
		return this.getClass().equals(m.getClass());
	}
	
	public boolean mutate(ProgramImpl n) {
		return false;
	}
	
	public boolean mutate(Rule n) {
		return false;
	}
	
	public boolean mutate(Commands n) {
		return false;
	}
	
	public boolean mutate(NullaryCommand n) {
		return false;
	}
	
	public boolean mutate(UnaryCommand n) {
		return false;
	}
	
	public boolean mutate(BinaryCommand n) {
		return false;
	}
	
	public boolean mutate(BinaryCondition n) {
		return false;
	}
	
	public boolean mutate(Relation n) {
		return false;
	}
	
	public boolean mutate(Number n) {
		return false;
	}
	
	public boolean mutate(UnaryExpr n) {
		return false;
	}
	
	public boolean mutate(BinaryExpr n) {
		return false;
	}
	
	public boolean mutate(smell n) {
		return false;
	}
	
	public abstract String getClassName();
	
	public String toString() {
		return "Applying " + getClassName() + " mutation method ";
	}
	

	/**
	 * Find a node in the AST tree of specific classes, but not the same as 
	 * a given node
	 *
	 * @param n the given node, node to find can't be the same as it
	 * @param cls the node to find should be one of the classes in cls
	 * @return the found node
	 */
	public Node findMyFellow(MutableNode n, Class<?>... cls) {
		// get the root (ProgramImpl node)
		Node root = n.getParent();
		// while root is still a mutable node (not ProgramImpl yet)
		while (MutableNode.class.isAssignableFrom( root.getClass() ))
			root = ((MutableNode) root).getParent();
		// generate a random sequence of checking order to check all the nodes
		int size = root.size();
		ArrayList<Integer> generated = new ArrayList<Integer>();
		for (int i = 0; i < size; ++i)
			generated.add(i);
		Collections.shuffle(generated);
		for (Integer i : generated) {
			if (root.nodeAt(i).toString().equals(n.toString()))
				continue;
			// if found fellow node match one of the given class
			for (Class<?> c : cls) {
				if (root.nodeAt(i).getClass().equals(c))
					return root.nodeAt(i);
			}	
		}
		return null;
	}
	
	/**
	 * Find a node in AST tree being the same class or subclass of given class
	 * but not the same as the given node
	 *         
	 * @param cls find node of {@code cls} class or subclass of {@code cls}
	 * @param n the node found should not be the same as {@code n}
	 * @param notCls the found node should not be {@code notCls}
	 * @return the found node
	 */
	public Node findMyFellowAndSub(Class<?> cls, MutableNode n, Class<?> notCls) {
		// get the root (ProgramImpl node)
		Node root = n.getParent();
		
		// TODO: Dirty fix
		if (root == null)
			return null;
		
		// while root is still a mutable node (not ProgramImpl yet)
		while (MutableNode.class.isAssignableFrom( root.getClass() )) {
			root = ((MutableNode) root).getParent();
			if (root == null)
				return null;
		}
			
		// generate a random sequence of checking order to check all the nodes
		int size = root.size();
		ArrayList<Integer> generated = new ArrayList<Integer>();
		for (int i = 0; i < size; ++i)
			generated.add(i);
		Collections.shuffle(generated);
		for (Integer i : generated) {
			if (!root.nodeAt(i).toString().equals(n.toString()) && 
				cls.isAssignableFrom(root.nodeAt(i).getClass()) &&
				(notCls == null || !notCls.equals(root.nodeAt(i).getClass())))
				return root.nodeAt(i);
		}
		return null;
	}
	
}
