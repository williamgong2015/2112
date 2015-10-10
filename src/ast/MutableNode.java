package ast;

public abstract class MutableNode implements Node ,Mutable {
	
	private Node parent;
	
	public void beMutated (AbstractMutation m) {
		m.mutate(this);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder ();
		return this.prettyPrint(sb).toString();
	}
	public void setParent(Node n) {
		parent = n;
	}
	
	public Node getParent() {
		return parent;
	}
}
