package ast;

import java.io.StringReader;

import exceptions.SyntaxError;
import parse.ParserImpl;
import parse.Tokenizer;

public class test {
	public static void main(String[] args) throws SyntaxError {
		ParserImpl p = new ParserImpl();
		StringReader s = new StringReader("((3+2))+");
		Tokenizer t = new Tokenizer(s);
		Expr e = p.parseExpression(t);
		System.out.println(e);
	}
}
