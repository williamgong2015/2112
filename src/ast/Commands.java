package ast;

import java.util.ArrayList;

public class Commands extends Command{

	private ArrayList<Command> up;
	
	public Commands(ArrayList<Command> a) {
		up = a;

	}
	
	@Override
	public int size() {
		int s = 1;
		if(up.size() != 0) {
			for(Command c : up)
				s += c.size();
		}
		return s;
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
		for(Command c : up) {
			if(index <=c.size() )
				return c.nodeAt(index - 1);
			else
				index -= c.size();
		}
		return null;
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		for(Command c : up) {
			sb.append(c + " ");
		}
		return sb;
	}
	
	public ArrayList<Command> getChild() {
		return up;
	}
	
	@Override
	public void beMutated(AbstractMutation m) {
		m.mutate(this);
	}

}
