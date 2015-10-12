package ast;

/**
 * A representation of a critter rule.
 */
public class Rule extends MutableNode {

	private Condition con;
	private Command com;
	
	public Rule(Condition a,Command b) {
		con = a;
		com = b;
	}
	
    @Override
    public int size() {
        return 1 + con.size() + com.size();
    }

    @Override
    public Node nodeAt(int index) {
        if(index == 0)
        	return this;
        if(index <= con.size())
        	return con.nodeAt(index - 1);
        return com.nodeAt(index - 1 - con.size());
    }

    @Override
    public StringBuilder prettyPrint(StringBuilder sb) {
    	String str = con + " --> " + com;
        sb.append(str.trim() +  ";\n");
        return sb;
    }
    
	@Override
	public void beMutated(AbstractMutation m) {
		m.mutate(this);
	}
	
	protected void setCommand(Command newCom) {
		com = newCom;
	}
	
	protected void setCondition(Condition newCon) {
		con = newCon;
	}
}
