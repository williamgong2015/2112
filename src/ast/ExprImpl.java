package ast;

import java.util.ArrayList;

public class ExprImpl extends MutableNode implements Expr{
	private ArrayList<Term> t;
	
	public ExprImpl(Term... t) {
		for(Term i : t) {
			this.t.add(i);
		}
	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Node nodeAt(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		// TODO Auto-generated method stub
		return null;
	}

}
