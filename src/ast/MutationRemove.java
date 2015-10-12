package ast;

import java.util.ArrayList;
import java.util.Random;

/**
 * Remove a node along with its descendants and return true,
 * if the parent of the node being removed needs a replacement child,
 * one of the node's children of the correct kind is randomly selected.
 * if the node can't be removed, return false
 *
 */
public class MutationRemove extends AbstractMutation{
	
	/* Nodes that can be remove if parent has more than 1 child */
	/**
	 * Rule can be remove if ProgramImpl has more than 1 rule
	 */
	public boolean mutate(Rule n) {
		ProgramImpl p = (ProgramImpl) n.getParent();
		if (p.getChild().size() > 1) {
			p.getChild().remove(n);
			return true;
		}
		return false;
	}
	
	/**
	 * Share command code for NullaryCommand, UnaryCommand and BinaryCommand
	 * There nodes can be removed if Commands has more than 1 child
	 */
	private boolean mutate(Command n) {
		Commands c = (Commands) n.getParent();
		if (c.getChild().size() > 1) {
			c.getChild().remove(n);
			return true;
		}
		return false;
	}
	
	public boolean mutate(NullaryCommand n) {
		return mutate((Command) n);
	}
	
	public boolean mutate(UnaryCommand n) {
		return mutate((Command) n);
	}
	
	public boolean mutate(BinaryCommand n) {
		return mutate((Command) n);
	}
	
	/* Nodes that be removed and replaced by a randomly selected child */
	
	/**
	 * @return random integer in [0..size-1]
	 */
	private int randomNumber(int size) {
		Random rand = new Random();
		return rand.nextInt(size);
	}
	
	/**
	 * Commands can be removed and replaced by a randomly selected child
	 * if it children has more than one command (otherwise, the remove commands
	 * and replace it with its only child won't change the pretty print result)
	 */
	public boolean mutate(Commands n) {
		Rule r = (Rule) n.getParent();
		ArrayList<Command> coms = n.getChild();
		if (coms.size() > 1) {
			r.setCommand(coms.get(randomNumber(coms.size())));
			return true;
		}
		return false;
	}
	
//	/**
//	 * Find the mutable Node
//	 * @param n
//	 */
//	private findItFromParent(MutableNode n) {
//		
//	}

	/**
	 * @return randomly return the left child of the param
	 * or the right child of the param
	 */
	private Condition getRandomBiConChild(BinaryCondition n) {
		if (randomNumber(1) == 0) 
			return n.getLeft();
		return n.getRight();
	}
	
	/**
	 * Parent of Binary Condition could be BinaryCondition, Rule and
	 * Relation
	 */
	public boolean muatate(BinaryCondition n) {
		MutableNode parent = (MutableNode) n.getParent();
		// skip all Relation that is a placeholder for BinaryCondition
		while (parent instanceof Relation)
			parent = (MutableNode) parent.getParent();
		if (parent instanceof BinaryCondition) {
			BinaryCondition b = (BinaryCondition) parent;
			// n is the left child of its parent
			if (b.getLeft() == n) 
				b.setLeft(getRandomBiConChild(n));
			else if (b.getRight() == n)
				b.setRight(getRandomBiConChild(n));
			// TODO: Delete these two lines if passing all the test
			else
				System.out.println("can't find the binary conditon from parent");
			return true;
		}
		else if (parent instanceof Rule) {
			Rule r = (Rule) parent;
			r.setCondition(getRandomBiConChild(n));
			return true;
		}
		else {
			System.out.println("can't find the parent and remove the Condition");
			return false;
		}
	}
	
	public boolean mutate(Relation n) {
		if (n.isCondition()) {
			if (n.getCondition() instanceof BinaryCondition)
				return mutate((BinaryCondition) n.getCondition());
			else if (n.getCondition() instanceof Relation)
				return mutate((Relation) n.getCondition());
			else {
				System.out.println("can't recognize type of relation's condition");
				return false;
			}
		}
		// expr rel expr type Relation does not support remove
		else 
			return false;
	}
	
	
	public boolean mutate(UnaryExpr n) {
		throw new UnsupportedOperationException();
	}
	
	public boolean mutate(BinaryExpr n) {
		throw new UnsupportedOperationException();
	}
	
}
