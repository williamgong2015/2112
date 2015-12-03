package servlet.ast;

/**
 * A critter program expression that has an integer value.
 */
public abstract class Expr extends MutableNode {
	
	public abstract Expr copy();
}
