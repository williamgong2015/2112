package ast;

import parse.TokenType;

public class Relation extends Condition{
	private Expr ep1;
	private Expr ep2;
	private TokenType  r;
	private Condition con;
	private boolean isCondition;
	
	public Relation(Expr e1,Expr e2,TokenType r) {
		ep1 = e1;
		ep2 = e2;
		this.r = r;
		isCondition = false;
	}
	
	public Relation (Condition c) {
		con = c;
		isCondition = true;
	}
	
	@Override
	public int size() {
		if (isCondition) 
			return 1 + con.size();
		return 1 + ep1.size() + ep2.size();
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
		if (isCondition)
			return con.nodeAt(index-1);
		if(index <= ep1.size()) {
        	return ep1.nodeAt(index - 1);
        }
        return ep2.nodeAt(index - 1 - ep1.size());
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if(con == null)
			sb.append(ep1 + " " + r.toString() +  " " +ep2);
		else 
			sb.append("{ " + con +" }");
		return sb;
	}
	
	@Override
	public void beMutated(AbstractMutation m) {
		m.mutate(this);
	}
	
	protected boolean isCondition() {
		return isCondition;
	}
	
	protected Condition getCondition() {
		return con;
	}
}
