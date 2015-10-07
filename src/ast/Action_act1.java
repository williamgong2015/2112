package ast;

public class Action_act1 extends Action{
	private ACT a;
	public Action_act1 (ACT a) {
		this.a = a;
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
		return sb.append(a);
	}
	
	public enum ACT {
		WAIT,FORWARD,BACKWARD,LEFT,RIGHT,EAT,ATTACK,GROW,BUD,MATE;
	}
}
