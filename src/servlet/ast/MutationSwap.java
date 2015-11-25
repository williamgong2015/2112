package servlet.ast;

import java.util.ArrayList;
import java.util.Collections;

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
			ArrayList<TwoIndexes> indexes = new ArrayList<>();
			for (int i = 0; i < n.numOfChildren(); ++i) 
				for (int j = i+1; j < n.numOfChildren(); ++j) 
					indexes.add(new TwoIndexes(i,j));
			Collections.shuffle(indexes);
			for (TwoIndexes index : indexes) {
				// if the two rules are the same
				if (n.getChild(index.first).toString()
						.equals(n.getChild(index.second).toString()))
					continue;
				Node tmp = n.getChild(index.first);
				n.setChild(index.first, n.getChild(index.second));
				n.setChild(index.second, tmp);
				return true;
			}
		}
		return false;
	}
	
	// only updates in Commands may be swaped
	@Override
	public boolean mutate(Commands n) {
		int size = n.getUpdates().size();
		if (size > 1) {
			ArrayList<TwoIndexes> indexes = new ArrayList<>();
			for (int i = 0; i < n.numOfChildren(); ++i) 
				for (int j = i+1; j < n.numOfChildren(); ++j) 
					indexes.add(new TwoIndexes(i,j));
			Collections.shuffle(indexes);
			for (TwoIndexes index : indexes) {
				// if the two command are the same, the tree doesn't change
				// try other swap
				if (n.getChild(index.first).toString()
						.equals(n.getChild(index.second).toString()))
					continue;
				Node tmp = n.getChild(index.first);
				n.setChild(index.first, n.getChild(index.second));
				n.setChild(index.second, tmp);
				return true;
			}
		}
		return false;
	}

	@Override
	public String getClassName() {
		return "Swap";
	}
	
	private class TwoIndexes {
		int first;
		int second;
		
		TwoIndexes(int i, int j) {
			first = i;
			second = j;
		}
	}
	
}
