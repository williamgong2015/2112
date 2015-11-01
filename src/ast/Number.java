package ast;

import simulate.Critter;
import simulate.World;

/**
 * a node which represents a number
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
	
	public void setVal(int newVal) {
		val = newVal;
	}
	
	public int getVal() {
		return val;
	}

	@Override
	public String eval(Critter c,World w) {
		return "" + val;
	}

	public Number copy() {
		return new Number(val);
	}
}
