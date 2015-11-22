package interpret;

import ast.Command;
import ast.Condition;
import ast.Expr;
import ast.Program;
import ast.ProgramImpl;
import ast.Rule;
import simulate.Critter;
import simulate.World;

/**
 * Implement the interpreter of AST nodes
 *
 */
public class InterpreterImpl implements Interpreter {

	// need the mediator the get and set the critter's properties
	private World w;
	private Critter c;
	
	public InterpreterImpl(World w,Critter c) {
		this.c = c;
		this.w = w;
	}
	
	/**
	 * The highest module for interpreting a critter
	 * @return A new Outcome object contains all the commands in an ArrayList
	 */
	@Override
	public Outcome interpret(Program p) {
		String result = null;
		ProgramImpl pro = (ProgramImpl) p;
		// finds the first rule in its list of rules whose condition is true
		int ruleCounter = 0;
		for (Rule r : pro.getChildren()) {
			c.setLastRuleExe(ruleCounter++);
			if (this.eval(r.getCondition())) {
				result = eval(r.getCommand());
				break;
			}
		}
		// If no rule’s condition is true, the critter perform a wait
		if (result == null)
			result = "wait";
		return new Outcome(result);
	}

	@Override
	public boolean eval(Condition con) {
		return con.eval(c, w).equals("true");
	}

	@Override
	public int eval(Expr e) {
		return Integer.parseInt(e.eval(c, w));
	}
	
	@Override
	public String eval(Command com) {
		return com.eval(c, w);
	}
}
