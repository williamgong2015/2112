package parse;

import java.io.Reader;
import java.util.ArrayList;

import ast.BinaryCommand;
import ast.BinaryCondition;
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
import exceptions.SyntaxError;
import ast.BinaryExpr;
import ast.Number;


public class ParserImpl implements Parser {
	

	public ParserImpl() {
	}
	
    @Override
    public Program parse(Reader r) {
        Tokenizer t = new Tokenizer(r);
        Program pro = null;
        try {
			pro = parseProgram(t);
		} catch (SyntaxError e) {
			System.err.println("Wrong Syntax!");
		}
        return pro;
    }

    /** Parses a program from the stream of tokens provided by the Tokenizer,
     *  consuming tokens representing the program. All following methods with
     *  a name "parseX" have the same spec except that they parse syntactic form
     *  X.
     *  @return the created AST
     *  @throws SyntaxError if there the input tokens have invalid syntax
     */
    public static ProgramImpl parseProgram(Tokenizer t) throws SyntaxError {
    	ArrayList<Rule> al = new ArrayList<Rule> ();
        while(t.hasNext()) {
        	al.add(parseRule(t));
        }
        al.trimToSize();
        ProgramImpl pro = new ProgramImpl(al);
        for(Rule r : al)
        	r.setParent(pro);
        return pro;
    }

    public static Rule parseRule(Tokenizer t) throws SyntaxError {
        Condition con = parseCondition(t);
        consume(t,TokenType.ARR);
        Command com = parseCommand(t);
        consume(t,TokenType.SEMICOLON);
        Rule r = new Rule(con,com);
        con.setParent(r);
        com.setParent(r);
        return r;
    }

    public static Condition parseCondition(Tokenizer t) throws SyntaxError {
        Condition c1 = parseConjunction(t);
        while(t.peek().getType().equals(TokenType.OR)) {
        	consume(t,TokenType.OR);
        	Condition temp = parseConjunction(t);
        	Condition c = new BinaryCondition(c1,BinaryCondition.Operator.OR,temp);
        	temp.setParent(c);
        	c1.setParent(c);
        	c1 = c;
        }
        return c1;
    }
    
    public static Condition parseConjunction(Tokenizer t) throws SyntaxError {
        Condition c1 = parseRelation(t);
        while(t.peek().getType().equals(TokenType.AND)) {
        	consume(t,TokenType.AND);
        	Condition temp = parseRelation(t);
        	Condition c = new BinaryCondition(c1,BinaryCondition.Operator.AND,temp);
        	temp.setParent(c);
        	c1.setParent(c);
        	c1 = c;
        }
        return c1;
    }
    
    public static Condition parseRelation(Tokenizer t) throws SyntaxError {
    	if(t.peek().getType().equals(TokenType.LBRACE)) {
    		consume(t,TokenType.LBRACE);
    		Condition con = parseCondition(t);
    		consume(t,TokenType.RBRACE);
    		return con;
    	}
        Expr e1 = parseExpression(t);
        TokenType temp = t.next().getType();
        if(temp.category.equals(TokenCategory.RELOP)) {
        	Expr e2 = parseExpression(t);
        	Condition r = new Relation(e1,e2,temp);
        	e1.setParent(r);
        	e2.setParent(r);
        	return r;
        }
        else
        	throw new SyntaxError();
    }
    
    public static Command parseCommand(Tokenizer t) throws SyntaxError {
    	ArrayList<Command> al = new ArrayList<Command>();
        while(t.peek().getType().equals(TokenType.MEM) 
        		|| t.peek().isMemSugar()) {
        	Expr e1 = null;
        	if(t.peek().isMemSugar()) {
        		Token te = t.next();
        		e1 = dealWithSyntaxSugar(te);
        	}
        	else {
        		consume(t,TokenType.MEM);
        		consume(t,TokenType.LBRACKET);
        		e1 = parseExpression(t);
        		consume(t,TokenType.RBRACKET);
        	}
        	consume(t,TokenType.ASSIGN);
        	Expr e2 = parseExpression(t);
        	Command c = new BinaryCommand(e1,e2);
        	e1.setParent(c);
        	e2.setParent(c);
        	al.add(c);
        }
        al.trimToSize();
        Command c = null;
        if(t.peek().getType().equals(TokenType.TAG)) {
        	t.next();
        	consume(t,TokenType.LBRACKET);
        	Expr e = parseExpression(t);
        	consume(t,TokenType.RBRACKET);
        	c = new UnaryCommand(e,UnaryCommand.tp.tag);
        }
        else if(t.peek().getType().equals(TokenType.SERVE)) {
        	t.next();
        	consume(t,TokenType.LBRACKET);
        	Expr e = parseExpression(t);
        	consume(t,TokenType.RBRACKET);
        	c = new UnaryCommand(e,UnaryCommand.tp.serve);
        }
        else if(t.peek().isAction()) {
        	Token temp = t.next();
        	c = new NullaryCommand(temp.getType());
        }
        if(c != null)
        	al.add(c);
        Commands com = new Commands(al);
        if(c != null)
        	c.setParent(com);
        if(al.size() != 0) {
        	for(Command a :al)
        		a.setParent(com);
        }
        return com;
    }

    public static Expr parseExpression(Tokenizer t) throws SyntaxError {
        Expr ep1 = parseTerm(t);
        while(t.peek().isAddOp()) {
        	Token temp = t.next();
        	BinaryExpr.op o = null;
        	if(temp.getType().equals(TokenType.PLUS))
        		o = BinaryExpr.op.PLUS;
        	else
        		o = BinaryExpr.op.MIN;
        	Expr ep2 = parseTerm(t);
        	Expr ep = new BinaryExpr(ep1,ep2,o);
        	ep1.setParent(ep);
        	ep2.setParent(ep);
        	ep1 = ep;
        }
        return ep1;
    }

    public static Expr parseTerm(Tokenizer t) throws SyntaxError {
    	Expr ep1 = parseFactor(t);
        while(t.peek().isMulOp()) {
        	Token temp = t.next();
        	BinaryExpr.op o = null;
        	if(temp.getType().equals(TokenType.MUL))
        		o = BinaryExpr.op.MUL;
        	else if(temp.getType().equals(TokenType.DIV))
        		o = BinaryExpr.op.DIV;
        	else
        		o = BinaryExpr.op.MOD;
        	Expr ep2 = parseFactor(t);
        	Expr ep = new BinaryExpr(ep1,ep2,o);
        	ep1.setParent(ep);
        	ep2.setParent(ep);
        	ep1 = ep;
        }
        return ep1;
    }

    public static Expr parseFactor(Tokenizer t) throws SyntaxError {
        Token temp = t.peek();
        if(temp.isNum()) {
        	consume(t,TokenType.NUM);
        	return new Number(temp.toNumToken().getValue());
        }
        if(temp.getType().equals(TokenType.MEM)) {
        	consume(t,TokenType.MEM);
        	consume(t,TokenType.LBRACKET);
        	Expr e = parseExpression(t);
        	consume(t,TokenType.RBRACKET);
        	Expr ep = new UnaryExpr(e,UnaryExpr.T.mem);
        	e.setParent(ep);
        	return ep;
        }
        if(temp.getType().equals(TokenType.LPAREN)) {
        	consume(t,TokenType.LPAREN);
        	Expr e = parseExpression(t);
        	consume(t,TokenType.RPAREN);
        	Expr f = new UnaryExpr(e,UnaryExpr.T.paren);
        	e.setParent(f);
        	return f;
        }
        if(temp.getType().equals(TokenType.MINUS)) {
        	consume(t,TokenType.MINUS);
        	Expr e = parseFactor(t);
        	Expr f = new UnaryExpr(e,UnaryExpr.T.neg);
        	e.setParent(f);
        	return f;
        }
        if(temp.isSensor()) {
        	Expr e = parseSensor(t);
        	Expr f = new UnaryExpr(e,UnaryExpr.T.sensor);
        	e.setParent(f);
        	return f;
        }
        if(temp.isMemSugar()) {
        	t.next();
        	Expr n = dealWithSyntaxSugar(temp);
        	Expr e = new UnaryExpr(n,UnaryExpr.T.mem);
        	n.setParent(e);
        	return e;
        }
        throw new SyntaxError();
    }

    public static Expr parseSensor(Tokenizer t) throws SyntaxError {
        Token temp = t.next();
        if(temp.getType().equals(TokenType.NEARBY)) {
        	consume(t,TokenType.LBRACKET);
        	Expr e = parseExpression(t);
        	consume(t,TokenType.RBRACKET);
        	Expr ep = new UnaryExpr(e,UnaryExpr.T.nearby);
        	e.setParent(ep);
        	return ep;
        }
        
        if(temp.getType().equals(TokenType.AHEAD)) {
        	consume(t,TokenType.LBRACKET);
        	Expr e = parseExpression(t);
        	consume(t,TokenType.RBRACKET);
        	Expr ep = new UnaryExpr(e,UnaryExpr.T.ahead);
        	e.setParent(ep);
        	return ep;
        }
        
        if(temp.getType().equals(TokenType.RANDOM)) {
        	consume(t,TokenType.LBRACKET);
        	Expr e = parseExpression(t);
        	consume(t,TokenType.RBRACKET);
        	Expr ep = new UnaryExpr(e,UnaryExpr.T.random);
        	e.setParent(ep);
        	return ep;
        }
        //not sure...
        if(temp.getType().equals(TokenType.SMELL)) {
        	Number n = new Number(0);
        	return n;
        }
        throw new SyntaxError();
    }

    /**
     * Consumes a token of the expected type.
     * @throws SyntaxError if the wrong kind of token is encountered.
     */
    public static void consume(Tokenizer t, TokenType tt) throws SyntaxError {
    	Token temp = t.next();
        if(!temp.getType().equals(tt)){
        	throw new SyntaxError();
        }
    }
    
    public static boolean compare(Tokenizer t, TokenType tt){
        if(!t.hasNext() || !t.next().getType().equals(tt))
        	return false;
        return true;
    }
    public static Expr dealWithSyntaxSugar(Token temp) {
    	TokenType t = temp.getType();
    	if(t.equals(TokenType.ABV_MEMSIZE)) {
    		Number n = new Number(0);
    		return n;
    	}
    	if(t.equals(TokenType.ABV_DEFENSE)) {
    		Number n = new Number(1);
    		return n;
    	}
    	if(t.equals(TokenType.ABV_OFFENSE)) {
    		Number n = new Number(2);
    		return n;
    	}
    	if(t.equals(TokenType.ABV_SIZE)) {
    		Number n = new Number(3);
    		return n;
    	}
    	if(t.equals(TokenType.ABV_ENERGY)) {
    		Number n = new Number(4);
    		return n;
    	}
    	if(t.equals(TokenType.ABV_PASS)) {
    		Number n = new Number(5);
    		return n;
    	}
    	if(t.equals(TokenType.ABV_TAG)) {
    		Number n = new Number(6);
    		return n;
    	}
    	if(t.equals(TokenType.ABV_POSTURE)) {
    		Number n = new Number(7);
    		return n;
    	}
    	return null;
    }
}
