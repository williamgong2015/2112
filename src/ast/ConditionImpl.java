package ast;

public class ConditionImpl extends BinaryCondition implements Mutable{

	public ConditionImpl(Conjuction l, Operator op, Conjuction r) {
		super(l, op, r);
	}

	@Override
	public void beMutated(MutationImpl m) {
		m.mutate(this);
	}

}
