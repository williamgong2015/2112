package ast;

public class Factor5 extends Factor{

	private Sensor s;
	
	public Factor5 (Sensor s) {
		this.s = s;
	}
	
	@Override
	public int size() {
		return 1 + s.size();
	}

	@Override
	public Node nodeAt(int index) {
		return (index == 0) ? this : s.nodeAt(index - 1);
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		return sb.append(s.toString());
	}

}
