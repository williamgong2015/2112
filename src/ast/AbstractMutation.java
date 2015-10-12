package ast;

public abstract class AbstractMutation implements Mutation{
	
	public boolean equals(Mutation m) {
		return this.getClass().equals(m.getClass());
	}
	
	public boolean mutate(ProgramImpl n) {
		return false;
	}
	
	public boolean mutate(Rule n) {
		return false;
	}
	
	public boolean mutate(Commands n) {
		return false;
	}
	
	public boolean mutate(NullaryCommand n) {
		return false;
	}
	
	public boolean mutate(UnaryCommand n) {
		return false;
	}
	
	public boolean mutate(BinaryCommand n) {
		return false;
	}
	
	public boolean mutate(BinaryCondition n) {
		return false;
	}
	
	public boolean mutate(Number n) {
		return false;
	}
	
	public boolean mutate(UnaryExpr n) {
		return false;
	}
	
	public boolean mutate(BinaryExpr n) {
		return false;
	}
	
	public boolean mutate(Relation n) {
		return false;
	}
	
}
