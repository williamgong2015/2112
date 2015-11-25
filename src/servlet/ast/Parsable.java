package servlet.ast;

import servlet.parser.Tokenizer;

/**
 * Interface for Nodes that can be parsed 
 */
public interface Parsable {

	Node parseMyType(Tokenizer t);
}
