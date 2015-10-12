package ast;

public class BinaryExpr extends Expr{

	private Expr e1;
	private Expr e2;
	private op o;
	
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
	public void beMutated(AbstractMutation m) {
		m.mutate(this);
	}
}
