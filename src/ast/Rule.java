package ast;

/**
 * A representation of a critter rule.
 */
public class Rule extends MutableNode {
	private Condition con;
	private Command   com;
    
    public Rule(Condition con,Command com) {
    	this.con = con;
    	this.com = com;
    }
    @Override
    public int size() {
        return 1 + con.size() +com.size();
    }

    @Override
    public Node nodeAt(int index) {
        if(index == 0)
        	return this;
        if(index < con.size())
        	return con.nodeAt(index - 1);
        else
        	return com.nodeAt(index - 1 - con.size());
    }

    @Override
    public StringBuilder prettyPrint(StringBuilder sb) {
        return sb.append(con.toString() + " --> " + com.toString());
    }
    
}
