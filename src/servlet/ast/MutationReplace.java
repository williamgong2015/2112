package servlet.ast;

import servlet.ast.UnaryExpr.T;

/**
 * The node and its children are replaced with a copy of another randomly 
 * selected node of the right kind, found somewhere in the rule set. 
 * The entire AST subtree rooted at the selected node is copied.
 * 
 * The nodes support replace mutation are 
 *     all the nodes except ProgramImpl, Commands 
 *     
 * 
 */
public class MutationReplace extends AbstractMutation {
	
	/**
	 * Rule use this replace method
	 * 
	 * It find the same kind of nodes from it parent ProgramImpl, which is 
	 * faster than randomly picking nodes and test if it is of the same kind
	 */
	@Override
	public boolean mutate(Rule n) {
		Placeholder parent = (Placeholder) n.getParent();
		int size = parent.numOfChildren();
		if (size <= 1)
			return false;
		int oldIndex = parent.indexOfChild(n);
		int[] otherIndexes = game.utils.RandomGen.arrOfRanNumExcept(size, oldIndex);
		for (int i = 0; i < otherIndexes.length; ++i) {
			// keep trying to find a rule different from {@code n}
			MutableNode newChild = (MutableNode) 
					getACopy((MutableNode) parent.getChild(otherIndexes[i]));
			if (!newChild.toString().equals(n.toString())) {
				newChild.setParent(parent);
				parent.setChild(oldIndex, newChild);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Nodes of class Command share this method 
	 * 
	 * These nodes are:
	 *     NullaryCommand (child of Commands), 
	 *     UnaryCommand (child of Commands), 
	 *     BinaryCommand (child of Commands)
	 */
	private boolean mutateChildOfPlaceholder(Command n, Class<?>... cls) {
		Placeholder parent = (Placeholder) n.getParent();
		int oldIndex = parent.indexOfChild(n);
		MutableNode fellow = (MutableNode) findMyFellow(n, cls);
		if (fellow == null)
			return false;
		Command newChild = (Command) getACopy(fellow);
		newChild.setParent(parent);
		parent.setChild(oldIndex, newChild);
		return true;
	}
	
	// Action can be mutated and become action or update
	@Override
	public boolean mutate(NullaryCommand n) {
		return mutateChildOfPlaceholder(n, UnaryCommand.class, 
				BinaryCommand.class, NullaryCommand.class);
	}
	
	// Action can be mutated and become action or update
	@Override
	public boolean mutate(UnaryCommand n) {
		return mutateChildOfPlaceholder(n, UnaryCommand.class, 
				BinaryCommand.class, NullaryCommand.class);
	}
	
	// Update can be mutated into another update if it is not the last command
	// if it is the last command, it can be mutated and become an action
	@Override
	public boolean mutate(BinaryCommand n) {
		int numOfFellow = ((Commands) n.getParent()).numOfChildren();
		// Update is not the last command
		if (((Commands) n.getParent()).getChild(numOfFellow-1) != n)
			return mutateChildOfPlaceholder(n, BinaryCommand.class);
		return mutateChildOfPlaceholder(n, UnaryCommand.class, 
				BinaryCommand.class, NullaryCommand.class);
	}
	
	/**
	 * Nodes of class Condition share this method 
	 */
	private boolean mutate(Condition n) {
		MutableNode fellow = (MutableNode) findMyFellowAndSub(Condition.class, n, null);
		if (fellow == null)
			return false;
		Condition newChild = (Condition) getACopy(fellow);
		Node parent = n.getParent();
		newChild.setParent(parent);
		if (parent instanceof Rule) {
			((Rule) parent).setCondition(newChild);
			return true;
		}
		else if (parent instanceof BinaryCondition) {
			((BinaryCondition) parent).replaceChild(n, newChild);
			return true;
		}
		else {
//			System.out.println("MutationReplace: can't resolve parent type");
			return false;
		}
	}
	
	@Override
	public boolean mutate(BinaryCondition n) {
		return mutate((Condition) n);
	}
	
	@Override
	public boolean mutate(Relation n) {
		return mutate((Condition) n);
	}
	
	/**
	 * Nodes of class Expr share this method
	 */
	private boolean mutate(Expr n) {
		Expr fellow = (Expr) findMyFellowAndSub(Expr.class, n, null);
		if (fellow == null)
			return false;
		Expr newChild = (Expr) getACopy(fellow);
		// TODO Dirty Fix: not supporting transform to -factor
		Node parent = ((MutableNode) n).getParent();
		if ((parent instanceof UnaryExpr &&
				((UnaryExpr) parent).getType() == T.neg) ||
				(n instanceof UnaryExpr &&
				((UnaryExpr) n).getChild().toString().charAt(0) == '-'))
			if (newChild.toString().charAt(0) == '-')
				return false;
		newChild.setParent(parent);
		if (parent instanceof UnaryExpr || parent instanceof UnaryCommand) {
			((UnaryExpr) parent).setChild(newChild);
			return true;
		}
		else if (parent instanceof BinaryExpr || parent instanceof BinaryCommand
				|| parent instanceof Relation) {
			((BinaryOperation) parent).replaceChild(n, newChild);
			return true;
		}
		else {
			System.out.println("MutationReplace: can't resolve parent type");
			return false;
		}
	}
	
	@Override
	public boolean mutate(Number n) {
		return mutate((Expr) n);
	}
	
	@Override
	public boolean mutate(UnaryExpr n) {
		return mutate((Expr) n);
	}
	
	@Override
	public boolean mutate(BinaryExpr n) {
		return mutate((Expr) n);
	}

	@Override
	public String getClassName() {
		return "Replace";
	}
}
