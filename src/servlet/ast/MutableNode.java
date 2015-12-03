package servlet.ast;

import servlet.element.Critter;
import servlet.world.World;

/**
 * an abstract class which is the superclass
 * of all other nodes
 *
 */
public abstract class MutableNode implements Node ,Mutable {
	
	private Node parent;
	
	public abstract String eval(Critter c,World w);
	
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
	
	
	public abstract MutableNode copy();
}
