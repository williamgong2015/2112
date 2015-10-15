package ast;

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
		Rule newRule = (Rule) getACopy(r.get(util.RandomGen.
				randomNumber(r.size())));
		newRule.setParent(n);
		r.add(newRule);
		return true;
	}
	
	public boolean mutate(Commands n) {
		if(n.act == null) {
			ArrayList<Command> c = n.getChildren();
			Command newCommand = (Command) getACopy(c.get(util.RandomGen.
					randomNumber(c.size())));
			newCommand.setParent(n);
			c.add(newCommand);
			return true;
		}
		else
			return false;
	}
}
