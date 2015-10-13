package ast;

import exceptions.SyntaxError;
import parse.Tokenizer;

/**
 * An interface representing a Boolean condition in a critter program.
 *
 */
public abstract class Condition extends MutableNode {
	@Override
	public Node parseMyType(Tokenizer t) {
		try {
			return parse.ParserImpl.parseCondition(t);
		} catch (SyntaxError e) {
			e.printStackTrace();
			return null;
		}
	}
}
