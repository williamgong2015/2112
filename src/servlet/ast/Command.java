package servlet.ast;

/**
 * an abstract class which represents all kinds of command
 * including "update" and "action" in grammar.
 *
 */
public abstract class Command extends MutableNode{

	public abstract Command copy();
}
