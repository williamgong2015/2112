package servlet.ast;

/**
 * An abstract class representing a Boolean condition in a critter program.
 *
 */
public abstract class Condition extends MutableNode {

	@Override
	public abstract Condition copy();
}