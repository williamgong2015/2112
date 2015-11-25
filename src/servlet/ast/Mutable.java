package servlet.ast;

/**
 * an interface that denotes this node can be mutated
 *
 */
public interface Mutable {
	public boolean beMutated(AbstractMutation m);
}
