package servlet.ast;

import game.exceptions.SyntaxError;
import servlet.parser.Tokenizer;

/**
 * A critter program expression that has an integer value.
 */
public abstract class Expr extends MutableNode {
	
	@Override
	public MutableNode parseMyType(Tokenizer t) {
		try {
			return servlet.parser.ParserImpl.parseExpression(t);
		} catch (SyntaxError e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public abstract Expr copy();
}
