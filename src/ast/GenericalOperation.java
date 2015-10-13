package ast;

/**
 * Interface for generic nodes which has a TokenType attribute 
 *
 */
public interface GenericalOperation {

	Object getType();
	
	Object[] getAllPossibleType();
	
	void setType(Object newType);
}
