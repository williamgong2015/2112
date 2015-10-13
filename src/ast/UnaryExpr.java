package ast;

public class UnaryExpr extends Expr implements UnaryOperation,
                                               GenericalOperation {

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
			sb.append("(" + e + " )");
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
//		T[] r = {T.nearby, T.ahead, T.random, T.mem, T.paren,
//				T.neg, T.sensor};
		T[] r = {T.nearby, T.ahead, T.random, T.mem, T.paren,
				T.neg};
		return r;
	}

	@Override
	public void setType(Object newType) {
		t = (T) newType;
	}
}
