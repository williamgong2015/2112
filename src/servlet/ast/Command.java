package servlet.ast;

import game.exceptions.SyntaxError;
import servlet.parser.Tokenizer;

/**
 * an abstract class which represents all kinds of command
 * including "update" and "action" in grammar.
 *
 */
public abstract class Command extends MutableNode{

	@Override
	public MutableNode parseMyType(Tokenizer t) {
		try {
			return ((Commands) servlet.parser.ParserImpl.parseCommand(t)).getChild(0);
		} catch (SyntaxError e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public abstract Command copy();
}
