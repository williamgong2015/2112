package servlet.ast;

import servlet.ast.UnaryExpr.T;

/**
 * The node is replaced with a randomly chosen node of the same kind
 * (for example, replacing attack with eat, or + with *), 
 * but its children remain the same. Literal integer constants are
 * adjusted up or down by the value of java.lang.Integer.MAX VALUE/r.nextInt(),
 * where legal, and where r is a java.util.Random object.
 * 
 * The nodes support transform mutation are 
 *     all the nodes except ProgramImpl, Rule, Commands, BinaryCommand 
 *     (because they don't have node of the same kind)
 *     
 * 
 */
public class MutationTransform extends AbstractMutation {
	
	/**
	 * Share common code for nodes of GnericalOperation
	 */
	private boolean mutate(GenericOperation n) {
		Object[] allType = n.getAllPossibleType();
		int size = allType.length;
		Object type = n.getType();
		int oldType = -1;
		for (int i = 0; i < size; ++i) {
			if (allType[i] == type) 
				oldType = i;
		}
		if (oldType == -1) {
			System.out.println("MutationTransfer: can't find the type");
			return false;
		}
		int newType = game.utils.RandomGen.anotherRandomNum(size, oldType);
		// TODO Dirty Fix: not supporting transform to -factor
		Node parent = ((MutableNode) n).getParent();
		if ((parent instanceof UnaryExpr &&
				((UnaryExpr) parent).getType() == T.neg) ||
				(n instanceof UnaryExpr &&
				((UnaryExpr) n).getChild().toString().charAt(0) == '-'))
			if (allType[newType].equals(T.neg)) 
				return false;
		n.setType(allType[newType]);
		return true;
	}
	
	public boolean mutate(NullaryCommand n) {
		return mutate((GenericOperation) n);
	}
	
	public boolean mutate(UnaryCommand n) {
		return mutate((GenericOperation) n);
	}
	
	public boolean mutate(BinaryCondition n) {
		return mutate((GenericOperation) n);
	}
	
	public boolean mutate(Relation n) {
		return mutate((GenericOperation) n);
	}
	
	public boolean mutate(UnaryExpr n) {
		return mutate((GenericOperation) n);
	}
	
	public boolean mutate(BinaryExpr n) {
		return mutate((GenericOperation) n);
	}
	
	public boolean mutate(Number n) {
		int oldVal = n.getVal();
		int newVal;
		int d = game.utils.RandomGen.randomNumber();
		while (d == 0) 
			d = game.utils.RandomGen.randomNumber();
		if (game.utils.RandomGen.randomNumber(2) == 0)
			newVal = oldVal + Integer.MAX_VALUE / d;
		else
			newVal = oldVal - Integer.MAX_VALUE / d;
		Node parent = n.getParent();
		if (parent instanceof UnaryExpr &&
				((UnaryExpr) parent).getType() == T.neg && newVal < 0)
				newVal = -newVal;
		n.setVal(newVal);
		return true;
	}

	@Override
	public String getClassName() {
		return "Transform";
	}
}
