package ast;

public class Factor2 extends Factor{
	private Expr ep;
	
	public Factor2 (Expr e) {
		ep = e;
	}
	
	@Override
	public int size() {
		return 1 + ep.size();
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
		return ep.nodeAt(index - 1);
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		return sb.append("mem [ " + ep.toString() + " ] ");
	}

}
