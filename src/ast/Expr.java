package ast;

import exceptions.SyntaxError;
import parse.Tokenizer;

/**
 * A critter program expression that has an integer value.
 */
public abstract class Expr extends MutableNode {
	
	@Override
	public MutableNode parseMyType(Tokenizer t) {
		try {
			return parse.ParserImpl.parseExpression(t);
		} catch (SyntaxError e) {
			e.printStackTrace();
			return null;
		}
	}
}
