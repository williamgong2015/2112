package ast;

import java.io.StringReader;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import parse.Tokenizer;

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
	
	/**
	 * @return randomly chosen node in the ast tree that is of 
	 *         the same kind as {@code n} but not {@code n} itself
	 */
	public Node findMyFellow(MutableNode n) {
		// get the root (ProgramImpl node)
		Node root = n.getParent();
		// while root is still a mutable node (not ProgramImpl yet)
		while (MutableNode.class.isAssignableFrom( root.getClass() ))
			root = ((MutableNode) n).getParent();
		// generate a random sequence of checking order to check all the nodes
		int size = root.size();
		Random rng = new Random();
		// Note: use LinkedHashSet to maintain insertion order
		Set<Integer> generated = new LinkedHashSet<Integer>();
		while (generated.size() < size)
		{
		    Integer next = rng.nextInt(size);
		    // As we're adding to a set, this will automatically do a containment check
		    generated.add(next);
		}
		for (Integer i : generated) {
			if (root.nodeAt(i) != n && root.nodeAt(i).getClass().equals( n.getClass()))
				return root.nodeAt(i);
		}
		System.out.println("MutationReplace: Can't find fellow mutable node");
		return null;
	}
	
	/**
	 * @return randomly chosen node in the ast tree that is of the same kind as
	 *         or is the sub class of {@code cls} but the node is not {@code n} 
	 */
	public Node findMyFellowAndSub(Class<?> cls, MutableNode n) {
		// get the root (ProgramImpl node)
		Node root = n.getParent();
		// while root is still a mutable node (not ProgramImpl yet)
		while (MutableNode.class.isAssignableFrom( root.getClass() ))
			root = ((MutableNode) n).getParent();
		// generate a random sequence of checking order to check all the nodes
		int size = root.size();
		Random rng = new Random();
		// Note: use LinkedHashSet to maintain insertion order
		Set<Integer> generated = new LinkedHashSet<Integer>();
		while (generated.size() < size)
		{
		    Integer next = rng.nextInt(size);
		    // As we're adding to a set, this will automatically do a containment check
		    generated.add(next);
		}
		for (Integer i : generated) {
			if (root.nodeAt(i) != n && cls.isAssignableFrom(root.nodeAt(i).getClass()))
				return root.nodeAt(i);
		}
		System.out.println("MutationReplace: Can't find fellow mutable node");
		return null;
	}
	
	
	public Node getACopy(Node n) {
		StringReader s = new StringReader(n.toString());
		Tokenizer t = new Tokenizer(s);
		Node newChild = n.parseMyType(t);
		return newChild;
	}
}
