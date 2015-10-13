package ast;

import exceptions.SyntaxError;
import parse.Tokenizer;

public abstract class Command extends MutableNode{

	@Override
	public MutableNode parseMyType(Tokenizer t) {
		try {
			return parse.ParserImpl.parseCommand(t);
		} catch (SyntaxError e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
