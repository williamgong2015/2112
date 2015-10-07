package ast;

public class Sensor3 extends Sensor {

	private Expr ep;
	
	public Sensor3(Expr e) {
		ep = e;
	}
	
	@Override
	public int size() {
		return 1 + ep.size();
	}

	@Override
	public Node nodeAt(int index) {
		return (index == 0) ? this : ep.nodeAt(index - 1);
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		return sb.append("random [ " + ep.toString() + " ] ");
	}

}
