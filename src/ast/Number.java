package ast;

/**
 * this node represents a number
 */
public class Number extends Expr{

	private int val;
	
	public Number(int a) {
		val = a;
	}
	
	@Override
	public int size() {
		return 1;
	}

	@Override
	public Node nodeAt(int index) {
		return this;
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		return sb.append(val);
	}

	@Override
	public boolean beMutated(AbstractMutation m) {
		return m.mutate(this);
	}
}
