package ast;

/**
 * Remove a node along with its descendants and return true,
 * if the parent of the node being removed needs a replacement child,
 * one of the node's children of the correct kind is randomly selected.
 * if the node can't be removed, return false
 * 
 * The nodes support remove mutation are 
 *     (can be removed if its parent has more than 1 child)
 *     Rule, NullaryCommand, UnaryCommand, BinaryCommand, Commands,
 *     (can be removed and replaced by one of its child) 
 *     BinaryCondition, UnaryExpr, BinaryExpr
 *
 */
public class MutationRemove extends AbstractMutation{
	
	/* Nodes that can be remove if parent has more than 1 child */
	/**
	 * Rule can be remove if ProgramImpl has more than 1 rule
	 */
	@Override
	public boolean mutate(Rule n) {
		ProgramImpl p = (ProgramImpl) n.getParent();
		if (p.numOfChildren() > 1) {
			p.getChildren().remove(n);
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
		if (c.numOfChildren() > 1) {
			c.getChildren().remove(n);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean mutate(NullaryCommand n) {
		return mutate((Command) n);
	}
	
	@Override
	public boolean mutate(UnaryCommand n) {
		return mutate((Command) n);
	}
	
	@Override
	public boolean mutate(BinaryCommand n) {
		return mutate((Command) n);
	}
	
	/* Nodes that be removed and replaced by a randomly selected child */
	/**
	 * Commands can be removed and replaced by a randomly selected child
	 * if it children has more than one command (otherwise, the remove commands
	 * and replace it with its only child won't change the pretty print result)
	 */
	@Override
	public boolean mutate(Commands n) {
		Rule r = (Rule) n.getParent();
		if (n.numOfChildren() > 1) {
			r.setCommand(n.getRandomChild());
			return true;
		}
		return false;
	}
	
	/**
	 * BinaryCondition can be removed and replaced by a random selected child
	 */
	@Override
	public boolean mutate(BinaryCondition n) {
		// Parent of Binary Condition could be BinaryCondition or Rule 
		Node parent = n.getParent();
		if (parent instanceof BinaryCondition) {
			BinaryCondition b = (BinaryCondition) parent;
			// n is the left child of its parent
			if (b.getFirChild() == n) 
				b.setFirChild(n.getRandomChild());
			else if (b.getSecChild() == n)
				b.setSecChild(n.getRandomChild());
			// TODO: Delete these two lines if passing all the test
			else
				System.out.println("MutationRemove: can't find the BinaryCondition from its parent");
			return true;
		}
		else if (parent instanceof Rule) {
			Rule r = (Rule) parent;
			r.setCondition(n.getRandomChild());
			return true;
		}
		// TODO: Delete these two lines if passing all the test
		else {
			System.out.println("MutationRemove: can't resolve BinaryCondition's parent");
			return false;
		}
	}
	
	/**
	 * Share common code of UnaryExpr and BinaryExpr
	 */
	private boolean mutate(Expr n) {
		// the child of n used to replace n
		Expr child;
		if (n instanceof UnaryExpr) 
			child = ((UnaryExpr) n).getChild();
		else 
			child = ((BinaryExpr) n).getRandomChild();

		Node parent = n.getParent();
		if (parent instanceof UnaryExpr || parent instanceof UnaryCommand) {
			((UnaryOperation) parent).setChild(child);
			return true;
		}
		else if (parent instanceof BinaryExpr || parent instanceof Relation ||
				parent instanceof BinaryCommand) {
			if (((BinaryOperation) parent).getFirChild() == n)
				((BinaryOperation) parent).setFirChild(child);
			else
				((BinaryOperation) parent).setSecChild(child);
			return true;
		}
		// TODO: Delete these two lines if passing all the test
		else {
			System.out.println("MutationRemove: can't resolve Expr n's parent");
			return false;
		}
	}
	
	@Override
	public boolean mutate(UnaryExpr n) {
		return mutate((Expr) n);
	}
	
	@Override
	public boolean mutate(BinaryExpr n) {
		return mutate((Expr) n);
	}
	
}
