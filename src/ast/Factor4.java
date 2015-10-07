package ast;

public class Factor4 extends Factor{

	private Factor f;
	
	Factor4(Factor f) {
		this.f = f;
	}
	
	@Override
	public int size() {
		return 1 + f.size();
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
		return f.nodeAt(index - 1);
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		return sb.append("- " + f.toString());
	}

}
