package servlet.ast;

import servlet.element.Critter;
import servlet.parser.TokenType;
import servlet.world.World;

/**
 * a node represents the condition which
 * is made up by two expressions
 */
public class Relation extends Condition implements BinaryOperation, 
                                                   GenericOperation {
	private Expr ep1;
	private Expr ep2;
	private TokenType  r;
	
	public Relation(Expr e1,Expr e2,TokenType r) {
		ep1 = e1;
		ep2 = e2;
		this.r = r;
	}
	
	@Override
	public int size() {
		return 1 + ep1.size() + ep2.size();
	}

	@Override
	public Node nodeAt(int index) {
		if(index == 0)
			return this;
		if(index <= ep1.size()) {
        	return ep1.nodeAt(index - 1);
        }
        return ep2.nodeAt(index - 1 - ep1.size());
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append(ep1 + " " + r.toString() +  " " +ep2);
		return sb;
	}
	
	@Override
	public boolean beMutated(AbstractMutation m) {
		return m.mutate(this);
	}

	@Override
	public Expr getFirChild() {
		return ep1;
	}

	@Override
	public Expr getSecChild() {
		return ep2;
	}
	
	@Override
	public Expr getRandomChild() {
		if (game.utils.RandomGen.randomNumber(1) == 0)
			return ep1;
		else 
			return ep2;
	}

	@Override
	public void setFirChild(Node newExpr) {
		ep1 = (Expr) newExpr;
	}

	@Override
	public void setSecChild(Node newExpr) {
		ep2 = (Expr) newExpr;
	}

	@Override
	public void setRandomChild(Node newExpr) {
		if (game.utils.RandomGen.randomNumber(1) == 0)
			ep1 = (Expr) newExpr;
		else 
			ep2 = (Expr) newExpr;
	}
	
	@Override
	public void replaceChild(Node oldChild, Node newChild) {
		if (ep1 == oldChild)
			ep1 = (Expr) newChild;
		else if (ep2 == oldChild) 
			ep2 = (Expr) newChild;
		else 
			System.out.println("BinaryExpr: can't find oldChild");
	}

	@Override
	public Object getType() {
		return r;
	}

	@Override
	public Object[] getAllPossibleType() {
		TokenType[] r = {TokenType.LT, TokenType.LE, TokenType.EQ,
				TokenType.GE, TokenType.GT, TokenType.NE};
		return r;
	}

	@Override
	public void setType(Object newType) {
		r = (TokenType) newType;
	}
	
	@Override
	public String eval(Critter c,World w) {
		int left = Integer.parseInt(ep1.eval(c,w));
		int right = Integer.parseInt(ep2.eval(c,w));
		String str = r.toString();
		switch(str) {
		case "<=" :
			return "" + (left <= right);
		case "<"  :
			return "" + (left < right);
		case ">"  :
			return "" + (left > right);
		case ">="  :
			return "" + (left >= right);
		case "="  :
			return "" + (left == right);
		case "!="  :
			return "" + (left != right);
		}
		return "false";
	}

	public Relation copy() {
		return new Relation(ep1.copy(),ep2.copy(),r);
	}
}
