package ast;

public class BinaryCommand extends Command{

	private Expr e1;
	private Expr e2;
	
	public BinaryCommand(Expr ep1,Expr ep2) {
		e1 = ep1;
		e2 = ep2;
	}
	
	@Override
	public int size() {
		return 1 + e1.size() + e2.size();
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
		if(index <= e1.size())
			return e1.nodeAt(index - 1);
		return e2.nodeAt(index - 1 - e1.size());
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append("mem[" + e1 + "]:="  + e2);
		return sb;
	}
	
	@Override
	public void beMutated(AbstractMutation m) {
		m.mutate(this);
	}

}
