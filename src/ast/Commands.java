package ast;

import java.util.ArrayList;

public class Commands extends Command implements Placeholder {

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
			if(index <= c.size() )
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
	
	@Override
	public ArrayList<Command> getChildren() {
		return up;
	}
	
	@Override
	public boolean beMutated(AbstractMutation m) {
		return m.mutate(this);
	}
	
	protected Command getRandomChild() {
		return up.get(util.RandomGen.randomNumber(up.size()));
	}

	@Override
	public int numOfChildren() {
		return up.size();
	}

	@Override
	public void setOneWithAnother(int one, int another) {
		up.set(one, up.get(another));
	}
	
	@Override
	public void setChild(int index, Node newChild) {
		up.set(index, (Command) newChild);
	}
	
	@Override
	public Command getChild(int index) {
		return up.get(index);
	}

	@Override
	public int indexOfChild(Node child) {
		return up.indexOf((Command) child);
	}

}
