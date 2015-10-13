package ast;

import java.util.ArrayList;

import exceptions.SyntaxError;
import parse.Parser;
import parse.ParserFactory;
import parse.Tokenizer;

import java.io.StringReader;

/**
 * A data structure representing a critter program.
 *
 */
public class ProgramImpl implements Program, Mutable, Placeholder, Parsable {

	private ArrayList<Rule> root;
	
	public ProgramImpl(ArrayList<Rule> al) {
		root = al;
	}
	
    @Override
    public int size() {
    	int r = 1;
    	for (int i = 0; i < root.size(); ++i)
    		r += root.get(i).size();
        return r;
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
    
    @Override
    public ArrayList<Rule> getChildren() {
    	return root;
    }

	@Override
	public boolean beMutated(AbstractMutation m) {
		return m.mutate(this);
	}

	@Override
	public int numOfChildren() {
		return root.size();
	}

	
	@Override 
	public void setChild(int index, Node newChild) {
		root.set(index, (Rule) newChild);
	}
	
	@Override 
	public Rule getChild(int index) {
		return root.get(index);
	}

	@Override
	public int indexOfChild(Node child) {
		return root.indexOf((Rule) child);
	}

	@Override
	public ProgramImpl parseMyType(Tokenizer t) {
		try {
			return parse.ParserImpl.parseProgram(t);
		} catch (SyntaxError e) {
			e.printStackTrace();
			return null;
		}
	}

}
