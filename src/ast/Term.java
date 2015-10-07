package ast;

import java.util.ArrayList;

public class Term extends MutableNode implements Expr{
	private ArrayList<Factor> f;
	private ArrayList<mulop> op = new ArrayList<mulop> ();  
	
	public Term(Factor f) {
		this.f.add(f); 
	}
	
	public void add(Object o) {
		if(o instanceof Factor)
			f.add((Factor) o);
		else {
			op.add((mulop)o);
		}
	}
	
	@Override
	public int size() {
		int s = 1;
		for(Factor i : f)
			s += i.size();
		return s;
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
		for(Factor i : f) {
			if(index <= f.size())
				return i.nodeAt(index - 1);
			else {
				index -= i.size();
			}
		}
		return null;
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append(f.get(0));
		for(int i = 1;i < f.size();i++) {
			sb.append(op.get(i - 1));
			sb.append(f.get(i));
		}
		return sb;
	}

	public enum mulop {
		MUL,
		DIV;
	}
}
