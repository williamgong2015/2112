package ast;

/**
 * A representation of a binary Boolean condition: 'and' or 'or'
 *
 */
public class BinaryCondition extends Condition  {
	
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
    	sb.append(left.toString());
    	if(op.equals(Operator.AND))
    		sb.append(" and ");
    	else
    		sb.append(" or ");
    	sb.append(right.toString());
        return sb;
    }

    /**
     * An enumeration of all possible binary condition operators.
     */
    public enum Operator {
        OR, AND;
    }
    
	@Override
	public void beMutated(AbstractMutation m) {
		m.mutate(this);
	}
	
	protected Condition getLeft() {
		return left;
	}
	
	protected Condition getRight() {
		return right;
	}
	
	protected void setLeft(Condition newLeft) {
		left = newLeft;
	}
	
	protected void setRight(Condition newRight) {
		right = newRight;
	}
}
