package ast;

public class UnaryCommand extends Command{
	private Expr e;
	private tp t;
	
	public UnaryCommand(Expr e,tp t) {
		this.e = e;
		this.t = t;
	}

	@Override
	public int size() {
		return 1 + e.size();
	}


	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
		return e.nodeAt(index - 1);
	}


	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append(t + "[" + e + "]");
		return sb;
	}
	public enum tp {
		serve,
		tag;
	}
	
	@Override
	public void beMutated(AbstractMutation m) {
		m.mutate(this);
	}
}
