package ast;

public class Factor1 extends Factor{
	private int num;
	
	public Factor1 (int i) {
		num = i;
	}
	
	@Override
	public int size() {
		return 1;
	}

	@Override
	public Node nodeAt(int index) {
		return this;
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		return sb.append(num);
	}

}
