package servlet.ast;

import java.util.ArrayList;

import servlet.element.Critter;
import servlet.world.World;

/**
 * a node which represents "Command" in grammar
 */
public class Commands extends Command implements Placeholder {

	private ArrayList<Command> up;
	Command act;
	
	public Commands(ArrayList<Command> a,Command ac) {
		up = a;
		act = ac;
	}
	
	@Override
	public int size() {
		int s = 1;
		if(act != null)
			s += act.size();
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
		return act.nodeAt(index - 1);
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		for(Command c : up) {
			sb.append(c + " ");
		}
		if(act != null)
			sb.append(act);
		return sb;
	}
	
	/**
	 * @return all the updates and the action if it exists
	 */
	@Override
	public ArrayList<Command> getChildren() {
		ArrayList<Command> r = up;
		if (act != null)
			r.add(act);
		return r;
	}
	
	/**
	 * @return all the updates
	 */
	public ArrayList<Command> getUpdates() {
		return up;
	}
	
	@Override
	public boolean beMutated(AbstractMutation m) {
		return m.mutate(this);
	}
	
	
	protected Command getRandomChild() {
		ArrayList<Command> r = getChildren();
		return r.get(game.utils.RandomGen.randomNumber(r.size()));
	}

	@Override
	public int numOfChildren() {
		if (act != null)
			return up.size() + 1;
		else 
			return up.size();
	}
	
	@Override
	public void setChild(int index, Node newChild) {
		if(index < up.size())
			up.set(index, (Command) newChild);
		else
			act = (Command) newChild;
	}
	
	@Override
	public Command getChild(int index) {
		if(index < up.size())
			return up.get(index);
		else
			return act;
	}

	@Override
	public int indexOfChild(Node child) {
		if (child == act)
			return up.size();
		return up.indexOf((Command) child);
	}


	@Override
	public String eval(Critter c,World w) {
		StringBuilder sb = new StringBuilder();
		for(Command i : up) {
			sb.append(i.eval(c,w));
			sb.append(" ");
		}
		if(act != null)
			sb.append(act.eval(c, w));
		return sb.toString();
	}

	public Commands copy() {
		ArrayList<Command> newCopy = new ArrayList<Command>();
		for(Command c : up) {
			newCopy.add(c.copy());
		}
		if(act == null)
			return new Commands(newCopy, null);
		else
			return new Commands(newCopy, act.copy());
	}
}