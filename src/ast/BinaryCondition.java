package ast;

/**
 * A representation of a binary Boolean condition: 'and' or 'or'
 *
 */
// not sure about whether "op" will be in lower case or upper case;
public class BinaryCondition implements Condition {
	Condition left;
	Condition right;
	Operator o;
	
    /**
     * Create an AST representation of l op r.
     * @param l
     * @param op
     * @param r
     */
    public BinaryCondition(Condition l, Operator op, Condition r) {
        left = l;
        right = r;
        o = op;
    }

    @Override
    public int size() {
        return 1 + left.size() + right.size();
    }

    @Override
    public Node nodeAt(int index) {
        if(index == 0)
        	return this;
        if(index <= left.size())
        	return left.nodeAt(index - 1);
        else
        	return right.nodeAt(index - 1 - left.size());
    }
    
    @Override
    public StringBuilder prettyPrint(StringBuilder sb) {
        return sb.append(left.toString() + " " + o.toString() + " " + right.toString());
    }


    /**
     * An enumeration of all possible binary condition operators.
     */
    public enum Operator {
        OR, AND;
    }
}
