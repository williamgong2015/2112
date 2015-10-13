package ast;

public class BinaryExpr extends Expr implements BinaryOperation, GenericalOperation {

	private Expr e1;
	private Expr e2;
	private op o;
	
	public BinaryExpr(Expr e1,Expr e2) {
		this.e1 = e1;
		this.e2 = e2;
	}
	
	public BinaryExpr(Expr e1,Expr e2,op o) {
		this.e1 = e1;
		this.e2 = e2;
		this.o = o;
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
		sb.append("" + e1 + " " + o + " " + e2);
		return sb;
	}

	public enum op {
		MUL("*"),
		DIV("/"),
		PLUS("+"),
		MIN("-"),
		MOD("mod");
		
		private String name;
		private op(String s) {
			name = s;
		}
		public String toString () {
			return name;
		}
	}
	
	@Override
	public boolean beMutated(AbstractMutation m) {
		return m.mutate(this);
	}
	
	public Node getFirChild() {
		return e1;
	}
	
	public Node getSecChild() {
		return e2;
	}
	
	public Expr getRandomChild() {
		if (util.RandomGen.randomNumber(1) == 0)
			return e1;
		else 
			return e2;
	}
	
	public void setFirChild(Node newExpr) {
		e1 = (Expr) newExpr;
	}
	
	public void setSecChild(Node newExpr) {
		e2 = (Expr) newExpr;
	}
	
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
			System.out.println("BinaryExpr: can't find oldChild");
	}

	@Override
	public op getType() {
		return o;
	}

	@Override
	public Object[] getAllPossibleType() {
		op[] r = {op.MUL, op.DIV, op.PLUS, op.MIN, op.MOD};
		return r;
	}

	@Override
	public void setType(Object newType) {
		o = (op) newType;
	}
}
