package ast;

public abstract class MutableNode implements Node,Mutable{
	public void beMutated(MutationImpl m) {
		m.mutate(this);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		return this.prettyPrint(sb).toString();
	}
}
