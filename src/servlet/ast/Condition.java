package servlet.ast;

import game.exceptions.SyntaxError;
import servlet.parser.Tokenizer;

/**
 * An abstract class representing a Boolean condition in a critter program.
 *
 */
public abstract class Condition extends MutableNode {
	@Override
	public MutableNode parseMyType(Tokenizer t) {
		try {
			return servlet.parser.ParserImpl.parseCondition(t);
		} catch (SyntaxError e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public abstract Condition copy();
}