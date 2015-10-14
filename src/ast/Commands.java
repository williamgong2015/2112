package ast;

import java.util.ArrayList;

import exceptions.SyntaxError;
import parse.Tokenizer;

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
		return act == null ? up.size() : up.size() + 1;
	}
	
	@Override
	public void setChild(int index, Node newChild) {
		if(index <= up.size())
			up.set(index, (Command) newChild);
		else
			act = (Command) newChild;
	}
	
	@Override
	public Command getChild(int index) {
		if(index <= up.size())
			return up.get(index);
		else
			return act;
	}

	@Override
	public int indexOfChild(Node child) {
		return up.indexOf((Command) child) > 0 ? up.indexOf((Command) child) : up.size() + 1;
	}
	
	@Override
	public MutableNode parseMyType(Tokenizer t) {
		try {
			return parse.ParserImpl.parseCommand(t);
		} catch (SyntaxError e) {
			e.printStackTrace();
			return null;
		}
	}

}
