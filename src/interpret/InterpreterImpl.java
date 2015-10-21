package interpret;

import ast.Command;
import ast.Condition;
import ast.Expr;
import ast.Program;

/**
 * Implement the interpreter of AST nodes
 *
 */
public class InterpreterImpl implements Interpreter {

	@Override
	public Outcome interpret(Program p) {
		
		// TODO Auto-generated method stub
		
		// eval the condition 
		
		// eval the command
				
		// return a outcome (a list of commands)
		
		return null;
	}

	@Override
	public boolean eval(Condition c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int eval(Expr e) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String eval(Command c) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
