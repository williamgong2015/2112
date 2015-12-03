package servlet.ast;

import java.util.ArrayList;

/**
 * For nodes with a variable number of children, an additional copy of 
 * one of the children, randomly selected, is appended to the end of 
 * the list of children. This applies to the root node, where a new rule 
 * can be added, and also to command nodes, where the sequence of updates can
 * be extended with another update.
 *
 * Nodes that support Duplicate Mutation:
 *     ProgramImpl, Commands
 */
public class MutationDuplicate extends AbstractMutation {

	public boolean mutate(ProgramImpl n) {
		ArrayList<Rule> r = n.getChildren();
		Rule newRule =  r.get(game.utils.RandomGen.randomNumber(r.size())).
				copy();
		newRule.setParent(n);
		r.add(newRule);
		return true;
	}
	
	public boolean mutate(Commands n) {
		if(n.act == null) {
			ArrayList<Command> c = n.getChildren();
			Command newCommand = c.get(game.utils.RandomGen.
					randomNumber(c.size())).copy();
			newCommand.setParent(n);
			c.add(newCommand);
			return true;
		}
		else
			return false;
	}

	@Override
	public String getClassName() {
		return "Duplicate";
	}
}
