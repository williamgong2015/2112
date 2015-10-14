package ast;

import exceptions.SyntaxError;
import parse.Tokenizer;

/**
 * an abstract class which represents all kinds of command
 * including "update" and "action" in grammar.
 *
 */
public abstract class Command extends MutableNode{

	@Override
	public MutableNode parseMyType(Tokenizer t) {
		try {
			return ((Commands) parse.ParserImpl.parseCommand(t)).getChild(0);
		} catch (SyntaxError e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
