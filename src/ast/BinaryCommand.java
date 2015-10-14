package ast;

/** 
 * a node which represents "update" in grammar
 * ,which output as mem[e1] := e2
 */
public class BinaryCommand extends Command implements BinaryOperation {

	private Expr e1;
	private Expr e2;
	
	public BinaryCommand(Expr ep1, Expr ep2) {
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
		sb.append("mem[" + e1 + "] := "  + e2);
		return sb;
	}
	
	@Override
	public boolean beMutated(AbstractMutation m) {
		return m.mutate(this);
	}
	
	@Override
	public Expr getFirChild() {
		return e1;
	}
	
	@Override
	public Expr getSecChild() {
		return e2;
	}
	
	@Override
	public Expr getRandomChild() {
		if (util.RandomGen.randomNumber(1) == 0)
			return e1;
		else 
			return e2;
	}
	
	@Override
	public void setFirChild(Node newExpr) {
		e1 = (Expr) newExpr;
	}
	
	@Override
	public void setSecChild(Node newExpr) {
		e2 = (Expr) newExpr;
	}
	
	@Override
	public void setRandomChild(Node newExpr) {
		if (util.RandomGen.randomNumber(1) == 0)
			e1 = (Expr) newExpr;
		else
			e2 = (Expr) newExpr;
	}

	@Override
	public void replaceChild(Node oldChild, Node newChild) {
		if (e1 == oldChild)
			e1 = (Expr) newChild;
		else if (e2 == oldChild) 
			e2 = (Expr) newChild;
		else 
			System.out.println("BinaryCommand: can't find oldChild");
	}
}
