package ast;

import java.util.ArrayList;

public class ExprImpl extends MutableNode implements Expr{
	private ArrayList<Term> t;
	private ArrayList<addop> op = new ArrayList<addop> ();  
	
	public ExprImpl(Term t) {
		this.t.add(t); 
	}
	
	public void add(Object o) {
		if(o instanceof Term)
			t.add((Term) o);
		else {
			op.add((addop)o);
		}
	}
	
	@Override
	public int size() {
		int s = 1;
		for(Term i : t)
			s += i.size();
		return s;
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
		for(Term i : t) {
			if(index <= t.size())
				return i.nodeAt(index - 1);
			else {
				index -= i.size();
			}
		}
		return null;
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append(t.get(0));
		for(int i = 1;i < t.size();i++) {
			sb.append(op.get(i - 1));
			sb.append(t.get(i));
		}
		return sb;
	}

	public enum addop {
		PlUS,
		MINUS;
	}
}
