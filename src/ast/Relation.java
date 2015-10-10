package ast;

import parse.TokenType;

public class Relation extends Condition{
	private Expr ep1;
	private Expr ep2;
	private TokenType  r;
	private Condition con;
	
	public Relation(Expr e1,Expr e2,TokenType r) {
		ep1 = e1;
		ep2 = e2;
		this.r = r;
	}
	
	public Relation (Condition c) {
		con = c;
	}
	
	@Override
	public int size() {
		return 1 + ep1.size() + ep2.size();
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
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
}
