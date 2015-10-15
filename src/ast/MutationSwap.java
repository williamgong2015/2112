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
		// if two child are the same, the tree won't change, swap can't success
		if (n.getFirChild().toString().equals(n.getSecChild().toString()))
			return false;
		Node tmp = n.getFirChild();
		n.setFirChild(n.getSecChild());
		n.setSecChild(tmp);
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
	
	@Override
	public boolean mutate(ProgramImpl n) {
		if (n.numOfChildren() > 1) {
			int[] indexes = new int[2];
			for (int i = 0; i < n.numOfChildren(); ++i) {
				for (int j = i+1; j < n.numOfChildren(); ++j) {
					indexes[0] = i;
					indexes[1] = j;
					// if the two rules are the same
					if (n.getChild(indexes[0]).toString()
							.equals(n.getChild(indexes[1]).toString()))
						continue;
					Node tmp = n.getChild(indexes[0]);
					n.setChild(indexes[0], n.getChild(indexes[1]));
					n.setChild(indexes[1], tmp);
					return true;
				}
			}	
		}
		return false;
	}
	
	// only updates in Commands may be swaped
	@Override
	public boolean mutate(Commands n) {
		int size = n.getUpdates().size();
		if (size > 1) {
			int[] indexes = new int[2];
			for (int i = 0; i < size; ++i) {
				for (int j = i+1; j < size; ++j) {
					indexes[0] = i;
					indexes[1] = j;
					// if the two command are the same, the tree doesn't change
					// try other swap
					if (n.getChild(indexes[0]).toString()
							.equals(n.getChild(indexes[1]).toString()))
						continue;
					Node tmp = n.getChild(indexes[0]);
					n.setChild(indexes[0], n.getChild(indexes[1]));
					n.setChild(indexes[1], tmp);
					return true;
				}
			}
		}
		return false;
	}
	
}
