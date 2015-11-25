package servlet.ast;

import servlet.element.Critter;
import servlet.world.World;

/**
 * A representation of a binary Boolean condition: 'and' or 'or'
 *
 */
public class BinaryCondition extends Condition implements BinaryOperation, GenericOperation {
	
	private Condition left;
	private Condition right;
	private Operator op;
	
    /**
     * Create an AST representation of l op r.
     * @param l
     * @param op
     * @param r
     */
    public BinaryCondition(Condition l, Operator op, Condition r) {
        left = l;
        right = r;
        this.op = op;
    }

    @Override
    public int size() {
        return 1 + left.size() + right.size();
    }

    @Override
    public Node nodeAt(int index) {
        if(index == 0)
        	return this;
        if(index <= left.size()) {
        	return left.nodeAt(index - 1);
        }
        return right.nodeAt(index - 1 - left.size());
    }
    
    @Override
    public StringBuilder prettyPrint(StringBuilder sb) {
    	// print {} if parent is an AND BinaryCondition and 
    	// this is an OR BinaryCondition
    	boolean parentIsAnd = false;
    	Node parent = this.getParent();
    	if (parent instanceof BinaryCondition) {
    		if (((BinaryCondition) parent).op.equals(Operator.AND))
    			parentIsAnd = true;
    	}
    	if (parentIsAnd && op.equals(Operator.OR))
    		sb.append("{ ");
    	sb.append(left.toString());
    	if(op.equals(Operator.AND))
    		sb.append(" and ");
    	else
    		sb.append(" or ");
    	sb.append(right.toString());
    	if (parentIsAnd && op.equals(Operator.OR))
    		sb.append(" }");
        return sb;
    }

    /**
     * An enumeration of all possible binary condition operators.
     */
    public enum Operator {
        OR, AND;
    }
    
	@Override
	public boolean beMutated(AbstractMutation m) {
		return m.mutate(this);
	}
	
	@Override
	public Condition getFirChild() {
		return left;
	}
	
	@Override
	public Condition getSecChild() {
		return right;
	}
	
	@Override
	public Condition getRandomChild() {
		if (game.utils.RandomGen.randomNumber(1) == 0)
			return left;
		else
			return right;
	}
	
	@Override
	public void setFirChild(Node newLeft) {
		left = (Condition) newLeft;
	}
	
	@Override
	public void setSecChild(Node newRight) {
		right = (Condition) newRight;
	}
	
	@Override
	public void setRandomChild(Node newChild) {
		if (game.utils.RandomGen.randomNumber(1) == 0)
			left = (Condition) newChild;
		else 
			right = (Condition) newChild;
	}

	@Override
	public void replaceChild(Node oldChild, Node newChild) {
		if (left == oldChild)
			left = (Condition) newChild;
		else if (right == oldChild) 
			right = (Condition) newChild;
		else 
			System.out.println("BinaryCondition: can't find oldChild");
	}

	@Override
	public Operator getType() {
		return op;
	}

	@Override
	public Object[] getAllPossibleType() {
		Operator[] r = {Operator.AND, Operator.OR};
		return r;
	}

	@Override
	public void setType(Object newType) {
		op = (Operator) newType;
	}

	@Override
	public String eval(Critter c,World w) {
		boolean l = (left.eval(c,w).equals("true"));
		boolean r = (right.eval(c,w).equals("true"));
		if(op.equals(Operator.AND)) {
			if(l && r)
				return "true";
			else
				return "false";
		}
			
		else {
			if(l || r)
				return "true";
			else
				return "false";
		}
	}

	public BinaryCondition copy() {
		return new BinaryCondition(left.copy(),op,right.copy());
	}
	
}
