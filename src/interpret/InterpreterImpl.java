package interpret;

import ast.BinaryCommand;
import ast.BinaryCondition;
import ast.BinaryExpr;
import ast.Command;
import ast.Commands;
import ast.Condition;
import ast.Expr;
import ast.NullaryCommand;
import ast.Program;
import ast.ProgramImpl;
import ast.Relation;
import ast.Rule;
import ast.UnaryCommand;
import ast.UnaryExpr;
import parse.TokenType;
import simulate.Mediator;
import util.RandomGen;
import ast.Number;

/**
 * Implement the interpreter of AST nodes
 *
 */
public class InterpreterImpl implements Interpreter {

	private Mediator m;
	
	public InterpreterImpl(Mediator med) {
		m = med;
	}
	
	@Override
	public Outcome interpret(Program p) {
		
		// TODO Auto-generated method stub
		
		// eval the condition 
		
		// eval the command
				
		// return a outcome (a list of commands)
		
		ProgramImpl pro = (ProgramImpl)p;
		for(Rule r : pro.getChildren()) {
			if(this.eval(r.getCondition())) {
				String s = eval(r.getCommand());//TODO
			}
		}
		return null;
	}

	@Override
	public boolean eval(Condition c) {
		if(c instanceof Relation)
			return eval((Relation)c);
		return eval((BinaryCondition)c);
	}
	
	private boolean eval(Relation r) {
		int left = eval(r.getFirChild());
		int right = eval(r.getSecChild());
		String str = r.operator();
		switch(str) {
		case "<=" :
			return (left <= right);
		case "<"  :
			return (left < right);
		case ">"  :
			return (left > right);
		case ">="  :
			return (left >= right);
		case "="  :
			return (left == right);
		case "!="  :
			return (left != right);
		}
		return false;
	}
	
	private boolean eval(BinaryCondition b) {
		boolean left = eval(b.getFirChild());
		boolean right = eval(b.getSecChild());
		BinaryCondition.Operator op = b.getType();
		if(op.equals(BinaryCondition.Operator.AND))
			return left && right;
		else
			return left || right;
	}

	@Override
	public int eval(Expr e) {
		if(e instanceof UnaryExpr)
			return eval((UnaryExpr)e);
		if(e instanceof BinaryExpr)
			return eval((BinaryExpr)e);
		return eval((Number)e);
	}
	
	private int eval(UnaryExpr u) {
		UnaryExpr.T type = u.getType();
		int val = eval(u.getChild());
		switch(type) {
		case nearby :
			return m.getCritterNearby(val);
		case ahead :
			return m.getCritterAhead(val);
		case random :
			return RandomGen.randomNumber(val) > 0 ? RandomGen.randomNumber(val) : 0;
		case mem :
			return m.getCritterMem(val);
		case paren :
			return val;
		case neg :
			return -val;
		case sensor :
			return 0;//TODO
		}
		return 0;
	}
	
	private int eval(BinaryExpr u) {
		BinaryExpr.op o = u.getType();
		int left = eval((Expr)u.getFirChild());
		int right = eval((Expr)u.getSecChild());
		switch(o) {
		case DIV:
			if(right == 0)
				return 0;
			else
				return left / right;
		case MOD:
			if(right == 0)
				return 0;
			else
				return left % right;
		case MUL:
			return left * right;
		case PLUS:
			return left + right;
		case MIN:
			return left - right;
		}
		return 0;
	}

	private int eval(Number n) {
		return n.getVal();
	}
	
	@Override
	public String eval(Command c) {
		StringBuilder res = new StringBuilder();
		if(c instanceof Commands) {
			for(Command i :((Commands)c).getChildren()) {
				res.append(eval(i));
				res.append(" ");
			}
		}
		if(c instanceof BinaryCommand) {
			res.append(eval((BinaryCommand)c));
		}
		if(c instanceof UnaryCommand) {
			res.append(eval((UnaryCommand)c));
		}
		if(c instanceof NullaryCommand) {
			res.append(eval((NullaryCommand)c));
		}
		return res.toString();
	}
	
	public String eval(BinaryCommand b) {
		int e1 = eval(b.getFirChild());
		int e2 = eval(b.getSecChild());
		return "u" + e1 + "," + e2;
	}
	
	public String eval(UnaryCommand u) {
		int e = eval(u.getChild());
		UnaryCommand.tp t = u.getType();
		if(t.equals(UnaryCommand.tp.serve))
			return "s" + e;
		else
			return "t" + e;
	}
	
	public String eval(NullaryCommand n) {
		TokenType t = n.getType();
		String act = t.toString();
		return act;
	}
}
