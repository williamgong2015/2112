package ast;

import java.util.ArrayList;

import parse.Parser;
import parse.ParserFactory;

import java.io.StringReader;

/**
 * A data structure representing a critter program.
 *
 */
public class ProgramImpl implements Program {

	private ArrayList<Rule> root;
	
	public ProgramImpl(ArrayList<Rule> al) {
		root = al;
	}
	
    @Override
    public int size() {
        return 1 + root.size();
    }

    @Override
    public Node nodeAt(int index) {
        if(index == 0)
        	return this;
        for(Rule r : root) {
        	if(index <= r.size()) {
        		return r.nodeAt(index - 1);
        	}
        	else
        		index -= r.size();
        }
        return null;
    }

    @Override
    public Program mutate() {
    	String s =  this.toString();
    	StringReader rd = new StringReader(s);
    	Parser p = ParserFactory.getParser();
    	Program pro = p.parse(rd);
    	pro.mutate();
        // TODO Auto-generated method stub
        return pro;
    }

    @Override
    public Program mutate(int index, Mutation m) {
    	String s = this.toString();
    	
        MutableNode n = (MutableNode)this.nodeAt(index);
        n.beMutated((AbstractMutation)m);
        return null;
    }

    @Override
    public StringBuilder prettyPrint(StringBuilder sb) {
    	for(Rule r : root)
    		sb.append(r.toString());
        return sb;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return this.prettyPrint(sb).toString();
    }
    
    public ArrayList<Rule> getChild() {
    	return root;
    }

}
