package ast;

public abstract class AbstractMutation implements Mutation{
	public abstract boolean mutate(MutableNode n);
	public boolean equals(Mutation m) {
		return this.getClass().equals(m.getClass());
	}
}
