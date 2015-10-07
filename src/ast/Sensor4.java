package ast;

public class Sensor4 extends Sensor{
	private int smell = 0;

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
		return sb.append(smell);
	}
}
