package servlet.ast;

import servlet.ast.UnaryExpr.T;

/**
 * A newly created node is inserted as the parent of the mutated node. 
 * The old parent of the mutated node becomes the parent of the inserted node,
 * and the mutated node becomes a child of the inserted node. 
 * If the inserted node has more than one child, the children that are not the
 * original node are copies of randomly chosen nodes of the right kind from 
 * the entire rule set.
 * 
 * The nodes support insert mutation are 
 *     BinaryCondition, Relation, Number, UnaryExpr, BinaryExpr
 *
 */
public class MutationInsert extends AbstractMutation {
	
	
	/**
	 * Share common code for nodes of class Condition
	 */
	private boolean mutate(Condition n) {
		Condition fellow = (Condition) findMyFellowAndSub(Condition.class, n, null);
		if (fellow == null)
			return false;
		Condition newChild = fellow.copy();
		BinaryCondition b;
		if (game.utils.RandomGen.randomNumber(2) == 0)
			b = new BinaryCondition(n, BinaryCondition.Operator.OR, newChild);
		else
			b = new BinaryCondition(n, BinaryCondition.Operator.AND, newChild);
		b.setParent(n.getParent());
		if (n.getParent() instanceof BinaryCondition) {
			if (((BinaryCondition) n.getParent()).getFirChild() == n)
				((BinaryCondition) n.getParent()).setFirChild(b);
			else 
				((BinaryCondition) n.getParent()).setSecChild(b);
		}
		else {
			((Rule) n.getParent()).setCondition(b);
		}
		n.setParent(b);
		newChild.setParent(b);
		return true;
	}
	
	
	public boolean mutate(BinaryCondition n) {
		return mutate((Condition) n);
	}
	
	public boolean mutate(Relation n) {
		return mutate((Condition) n);
	}
	
	/**
	 * Share common code for nodes of class Expr
	 */
	private boolean mutate(Expr n) {
		Expr fellow = (Expr) findMyFellowAndSub(Expr.class, n, null);
		if (fellow == null)
			return false;
		Expr newChild = fellow.copy();
		// insert an UnaryExpr
		if (game.utils.RandomGen.randomNumber(2) == 0) {
			UnaryExpr u = new UnaryExpr(n);
			Object[] types = u.getAllPossibleType();
			Object newType = types[game.utils.RandomGen.randomNumber(types.length)];
			// TODO Dirty Fix: not supporting transform to -factor
			Node parent = ((MutableNode) n).getParent();
			if ((parent instanceof UnaryExpr &&
					((UnaryExpr) parent).getType().equals(T.neg)) ||
					n.toString().charAt(0) == '-')
				if (newType.equals(T.neg))
					return false;
			u.setType(newType);
			u.setParent(n.getParent());
			if (n.getParent() instanceof UnaryOperation) {
				((UnaryOperation) n.getParent()).setChild(u);
			}
			else if (n.getParent() instanceof BinaryOperation) {
				if (((BinaryOperation) n.getParent()).getFirChild() == n)
					((BinaryOperation) n.getParent()).setFirChild(u);
				else 
					((BinaryOperation) n.getParent()).setSecChild(u);
			}
			n.setParent(u);
			return true;
		}
		// insert a BinaryExpr
		else {
			BinaryExpr b = new BinaryExpr(n, newChild);
			Object[] types = b.getAllPossibleType();
			b.setType(types[game.utils.RandomGen.randomNumber(types.length)]);
			b.setParent(n.getParent());
			if (n.getParent() instanceof UnaryOperation) {
				((UnaryOperation) n.getParent()).setChild(b);
			}
			else if (n.getParent() instanceof BinaryOperation) {
				if (((BinaryOperation) n.getParent()).getFirChild() == n)
					((BinaryOperation) n.getParent()).setFirChild(b);
				else 
					((BinaryOperation) n.getParent()).setSecChild(b);
			}
			n.setParent(b);
			newChild.setParent(b);
			return true;
		}
	}
	
	
	public boolean mutate(Number n) {
		return mutate((Expr) n);
	}
	
	public boolean mutate(UnaryExpr n) {
		return mutate((Expr) n);
	}
	
	public boolean mutate(BinaryExpr n) {
		return mutate((Expr) n);
	}


	@Override
	public String getClassName() {
		return "Insert";

	}
}
