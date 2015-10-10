package ast;

import java.util.ArrayList;

public class MutationRemove extends AbstractMutation{

	@Override
	public boolean mutate(MutableNode n) {
		if(n instanceof Program || n instanceof Commands)
			return false;
		
		if(n instanceof Rule) {
			ProgramImpl par = (ProgramImpl) n.getParent();
			ArrayList<Rule> r = par.getChild();
			r.remove(n);
			return true;
		}
		
		if(n instanceof Command) {
			Commands par = (Commands) n.getParent();
			ArrayList<Command> com = par.getChild();
			com.remove(par);
			return true;
		}
		
		return false;
	}

}
