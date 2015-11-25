package servlet.ast;

import java.util.ArrayList;
import java.util.Collections;

import game.exceptions.SyntaxError;
import game.utils.RandomGen;
import servlet.parser.Parser;
import servlet.parser.ParserFactory;
import servlet.parser.Tokenizer;

import java.io.StringReader;

/**
 * A data structure representing a critter program.
 *
 */
public class ProgramImpl implements Program, Mutable, Placeholder, Parsable {

	private ArrayList<Rule> root;
	private ArrayList<Integer> orderOfMutation;
	
	public ProgramImpl(ArrayList<Rule> al) {
		root = al;
		orderOfMutation = new ArrayList<Integer>();
		for(int i = 0;i < 6;i++) 
			orderOfMutation.add(i);
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
    	Collections.shuffle(orderOfMutation);
    	while(true) {
    		int index = RandomGen.randomNumber(this.size());
    		for(int i = 0;i < 6;i++) {
    			Mutation m = null;
    			if(orderOfMutation.get(i) == 0) {
    				m = MutationFactory.getDuplicate();
    			}
    			if(orderOfMutation.get(i) == 1) {
    				m = MutationFactory.getInsert();
    			}
    			if(orderOfMutation.get(i) == 2) {
    				m = MutationFactory.getRemove();
    			}
    			if(orderOfMutation.get(i) == 3) {
    				m = MutationFactory.getReplace();
    			}
    			if(orderOfMutation.get(i) == 4) {
    				m = MutationFactory.getSwap();
    			}
    			if(orderOfMutation.get(i) == 5) {
    				m = MutationFactory.getTransform();
    			}
    			Program pro = mutate(index,m);
    			if(pro != null) 
    				return pro;
    		}
    	}
    }

    @Override
    public Program mutate(int index, Mutation m) {
       if(index >= this.size())
    	   throw new IndexOutOfBoundsException();
       String temp = this.toString();
       StringReader rd = new StringReader(temp);
       Parser p = ParserFactory.getParser();
   	   Program pro = p.parse(rd);
   	   
   	   Mutable n = (Mutable) pro.nodeAt(index);
   	   String pre = n.toString();
   	   if(n.beMutated((AbstractMutation)m)) {
   		   System.out.print(m);
   		   System.out.println("to mutate node \"" + pre +"\"\n");
   		   return pro;
   	   }
   	   else
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
			return servlet.parser.ParserImpl.parseProgram(t);
		} catch (SyntaxError e) {
			e.printStackTrace();
			return null;
		}
	}

}
