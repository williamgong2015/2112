package ast;

public class Update extends MutableNode{
	private Expr ep1;
	private Expr ep2;
	@Override
	public int size() {
		return ep1.size() + ep2.size() + 1;
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
		if(index < ep1.size())
			return ep1.nodeAt(index - 1);
		return ep2.nodeAt(index - 1 - ep1.size());
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append("mem [ " + ep1.toString() + " ] := " + ep2.toString() );
		return sb;
	}

}
