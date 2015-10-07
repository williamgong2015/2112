package ast;

import java.util.ArrayList;

public class Command extends MutableNode{
	private ArrayList<Update> up = new ArrayList<Update> ();
	private Action act = null;
	public Command(Action ac,Update... a) {
		act = ac;
		for(Update u : a) {
			up.add(u);
		}
	}
	@Override
	public int size() {
		int s = up.size() + 1;
		if(act != null)
			s++;
		for(Update u : up) {
			s += u.size();
		}
		return s;
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
		if(index == this.size() - 1)
			return act;
		int i = 0;
		while(true) {
			if(up.get(i).size() > index)
				return up.get(i).nodeAt(index - 1);
			else{
				i++;
				index -= up.get(i).size();
			}
		}
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		for(Update u : up) {
			sb.append(u.toString() + " ");
		}
		if(act != null)
			sb.append(act.toString());
		return sb;
	}
}
