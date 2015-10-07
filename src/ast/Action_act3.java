package ast;

public class Action_act3 extends Action{
	private Expr ep;
	public Action_act3 (Expr e) {
		ep = e;
	}
	@Override
	public int size() {
		return ep.size() + 1;
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
		return ep;
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		return sb.append("serve [ " + ep + " ] ");
	}

}
