package ast;

public class Conjuction extends BinaryCondition implements Mutable{

	public Conjuction(Relation l, Operator op, Relation r) {
		super(l, op, r);
	}

	@Override
	public void beMutated(MutationImpl m) {
		m.mutate(this);
	}

}
