package ast;

import parse.TokenType;

public class NullaryCommand extends Command{

	private TokenType a;
	
	public NullaryCommand(TokenType s) {
		a = s;
	}
	@Override
	public int size() {
		return 1;
	}

	@Override
	public Node nodeAt(int index) {
		return this;
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		return sb.append(a.toString());
	}
	@Override
	public void beMutated(AbstractMutation m) {
		m.mutate(this);
	}
}
