package ast;


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
		Condition fellow = (Condition) findMyFellowAndSub(Condition.class, n, n.hashCode());
		if (fellow == null)
			return false;
		Condition newChild = (Condition) getACopy(fellow);
		BinaryCondition b;
		if (util.RandomGen.randomNumber(1) == 0)
			b = new BinaryCondition(n, BinaryCondition.Operator.OR, newChild);
		else
			b = new BinaryCondition(n, BinaryCondition.Operator.AND, newChild);
		b.setParent(n.getParent());
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
		Expr fellow = (Expr) findMyFellowAndSub(Expr.class, n, n.hashCode());
		if (fellow == null)
			return false;
		Expr newChild = (Expr) getACopy(fellow);
		// insert an UnaryExpr
		if (util.RandomGen.randomNumber(1) == 0) {
			UnaryExpr u = new UnaryExpr(n);
			Object[] types = u.getAllPossibleType();
			u.setType(types[util.RandomGen.randomNumber(types.length)]);
			u.setParent(n.getParent());
			n.setParent(u);
			return true;
		}
		// insert a BinaryExpr
		else {
			BinaryExpr b = new BinaryExpr(n, newChild);
			Object[] types = b.getAllPossibleType();
			b.setType(types[util.RandomGen.randomNumber(types.length)]);
			b.setParent(n.getParent());
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
}
