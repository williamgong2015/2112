package ast;

import java.util.ArrayList;

public class Commands extends Command{

	private ArrayList<Command> up;
	private Command act;
	
	public Commands(ArrayList<Command> a,Command b) {
		up = a;
		act = b;
	}
	
	@Override
	public int size() {
		int s = 1;
		if(up.size() != 0) {
			for(Command c : up)
				s += c.size();
		}
		if(act != null)
			s += act.size();
		return s;
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
		if(up.size() != 0) {
			for(Command c : up) {
				if(index <=c.size() )
					return c.nodeAt(index - 1);
				else
					index -= c.size();
			}
		}
		return act.nodeAt(index - 1);
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if(up.size() != 0) {
			for(Command c : up) {
				sb.append(c + " ");
			}
		}
		if(act != null)
			sb.append(act + " ");
		return sb;
	}

}
