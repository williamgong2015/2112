package ast;

import exceptions.SyntaxError;
import parse.Tokenizer;

/**
 * An abstract class representing a Boolean condition in a critter program.
 *
 */
public abstract class Condition extends MutableNode {
	@Override
	public MutableNode parseMyType(Tokenizer t) {
		try {
			return parse.ParserImpl.parseCondition(t);
		} catch (SyntaxError e) {
			e.printStackTrace();
			return null;
		}
	}
}
