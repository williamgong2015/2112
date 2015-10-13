package ast;

import parse.TokenType;

public class NullaryCommand extends Command implements GenericalOperation {

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
	public boolean beMutated(AbstractMutation m) {
		return m.mutate(this);
	}
	
	@Override
	public TokenType getType() {
		return a;
	}
	@Override
	public Object[] getAllPossibleType() {
		TokenType[] r = {TokenType.WAIT, TokenType.FORWARD,
				TokenType.BACKWARD, TokenType.LEFT, TokenType.RIGHT,
				TokenType.EAT, TokenType.ATTACK, TokenType.GROW,
				TokenType.BUD, TokenType.MATE};
		return r;
	}
	@Override
	public void setType(Object newType) {
		a = (TokenType) newType;
	}
	
}
