package ast;

import exceptions.SyntaxError;
import parse.Tokenizer;

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
