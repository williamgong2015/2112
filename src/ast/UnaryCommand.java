package ast;
/**
 * a node which is a command and it only has one child
 *
 */
public class UnaryCommand extends Command implements UnaryOperation,
                                                     GenericalOperation {
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
	public boolean beMutated(AbstractMutation m) {
		return m.mutate(this);
	}
	
	@Override
	public Expr getChild() {
		return e;
	}
	
	@Override
	public void setChild(Node newExpr) {
		e = (Expr) newExpr;
	}

	@Override
	public tp getType() {
		return t;
	}

	@Override
	public tp[] getAllPossibleType() {
		tp[] r = {tp.tag, tp.serve};
		return r;
	}

	@Override
	public void setType(Object newType) {
		t = (tp) newType;
	}
}
