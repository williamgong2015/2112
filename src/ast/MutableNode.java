package ast;

public abstract class MutableNode implements Node ,Mutable {
	
	private Node parent;
	
	public abstract boolean beMutated(AbstractMutation m);
	
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
