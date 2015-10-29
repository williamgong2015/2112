package ast;

import simulate.Critter;
import simulate.Element;
import simulate.Food;
import simulate.Position;
import simulate.World;
import util.RandomGen;

/**
 * a node which is an expression and has no child.
 */
public class UnaryExpr extends Expr implements UnaryOperation,
                                               GenericOperation {

	private Expr e;
	private T t;
	
	public UnaryExpr(Expr ep) {
		e = ep;
	}
	
	public UnaryExpr(Expr ep,T type) {
		e = ep;
		t = type;
	}
	
	@Override
	public int size() {
		return 1 + e.size();
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
		return e.nodeAt(index - 1);
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if(t.equals(T.nearby))
			sb.append("nearby[" + e + "]");
		if(t.equals(T.ahead))
			sb.append("ahead[" + e + "]");
		if(t.equals(T.mem))
			sb.append("mem[" + e + "]");
		if(t.equals(T.random))
			sb.append("random[" + e + "]");
		if(t.equals(T.paren))
			sb.append("(" + e + ")");
		if(t.equals(T.neg))
			sb.append("-" + e);
		if(t.equals(T.sensor))
			sb.append(e.toString());
		return sb;
	}

	public enum T {
		nearby,
		ahead,
		random,
		mem,
		paren,
		neg,
		sensor;
	}
	
	@Override
	public boolean beMutated(AbstractMutation m) {
		return m.mutate(this);
	}
	
	@Override
	public Expr getChild() {
		return e;
	}
	
	@Override
	public void setChild(Node newExpr) {
		e = (Expr) newExpr;
	}

	@Override
	public T getType() {
		return t;
	}

	@Override
	public T[] getAllPossibleType() {
		T[] r = {T.nearby, T.ahead, T.random, T.mem, T.paren,
				T.neg};
		return r;
	}

	@Override
	public void setType(Object newType) {
		t = (T) newType;
	}

	@Override
	public String eval(Critter c,World w) {
		UnaryExpr.T type = t;
		int val = Integer.parseInt(e.eval(c,w));
		switch(type) {
		case nearby :
			Position pos = c.getPosition().getNextStep(val);
			Element e = w.getElemAtPosition(pos);
			return "" + elementDistinguisher(e);
		case ahead :
			Position pos2 = c.getPosition();
			pos2 = pos2.getRelativePos(val, c.getDir());
			Element e2 = w.getElemAtPosition(pos2);
			return "" + elementDistinguisher(e2);
		case random :
			return "" + (RandomGen.randomNumber(val) > 0 ? RandomGen.randomNumber(val) : 0);
		case mem :
			return "" + c.getMem(val);
		case paren :
			return "" + val;
		case neg :
			return "" + (-val);
		case sensor :
			return "0";
		}
		return "";
	}
	
	public int elementDistinguisher(Element e) {
		if(e == null)
			return 0;
		if(e.getType().equals("ROCK"))
			return -1;
		else if(e.getType().equals("CRITTER"))
			return ((Critter)e).appearance();
		else if(e.getType().equals("FOOD"))
			return -1 - ((Food)e).getAmount();
		return 0;
	}
}
