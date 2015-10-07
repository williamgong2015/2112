package ast;

public class Relation implements Condition,Mutable{
	private Expr ep1;
	private Expr ep2;
	private rel r;
	public Relation (Expr e1,Expr e2,rel r) {
		ep1 = e1;
		ep2 = e2;
		this.r = r;
	}
	@Override
	public int size() {
		return ep1.size() + ep2.size() + 1;
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
        	return this;
        if(index <= ep1.size())
        	return ep1.nodeAt(index - 1);
        else
        	return ep2.nodeAt(index - 1 - ep1.size());
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		return sb.append(ep1.toString() + " " + r.name + " " + ep2.toString() );
	}

	public enum rel {
		NE("!="),
		GE(">="),
		GT(">"),
		LT("<"),
		LE("<="),
		EQ("=");
		final public String name;
		private rel (String str) {
			name = str;
		}
	}

	@Override
	public void beMutated(MutationImpl m) {
		m.mutate(this);
		
	}
	
}
