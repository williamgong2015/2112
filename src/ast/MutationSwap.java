package ast;

/**
 * The order of two children of the node is switched. 
 * For example, this allows swapping the positions of two rules,
 * or changing a − b to b − a.
 * 
 * The nodes support swap mutation are 
 *     (always can be swap) BinaryCommand, BinaryCondition, BinaryExpr, Relation
 *     (can be swap if have more than 1 child) Commands, ProgramImpl
 *
 */
public class MutationSwap extends AbstractMutation {
	
	/* Nodes that always can be swap */
	
	/**
	 * Share common code between BinaryCommand, BinaryCondition, 
	 * BinaryExpr and Relation
	 */
	private boolean mutate(BinaryOperation n) {
		n.setFirChild(n.getSecChild());
		n.setSecChild(n.getFirChild());
		return true;
	}
	
	@Override
	public boolean mutate(BinaryCommand n) {
		return mutate((BinaryOperation) n);
	}
	
	@Override
	public boolean mutate(BinaryCondition n) {
		return mutate((BinaryOperation) n);
	}
	
	@Override
	public boolean mutate(BinaryExpr n) {
		return mutate((BinaryOperation) n);
	}
	
	@Override
	public boolean mutate(Relation n) {
		return mutate((BinaryOperation) n);
	}
	
	/* Nodes that can be swap if it has more than 1 child */
	
	/** Share common code between ProgramImpl and Commands */
	private boolean mutate(Placeholder n) {
		if (n.numOfChildren() > 1) {
			int[] indexes = util.RandomGen.twoUniqueRandomNum(n.numOfChildren());
			n.setOneWithAnother(indexes[0], indexes[1]);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean mutate(ProgramImpl n) {
		return mutate((Placeholder) n);
	}
	
	@Override
	public boolean mutate(Commands n) {
		return mutate((Placeholder) n);
	}
	
}
