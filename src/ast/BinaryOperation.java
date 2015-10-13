package ast;

/**
 * Interface of nodes that has exactly two children
 */
public interface BinaryOperation extends Node {
	Node getFirChild();
	
	Node getSecChild();
	
	Node getRandomChild();
	
	void setFirChild(Node newNode);
	
	void setSecChild(Node newNode);
	
	void setRandomChild(Node newNode);
	
	/**
	 * Effect: replace oldChild with newChild
	 * Require: oldChild is one of the child of the BinaryOperation
	 */
	void replaceChild(Node oldChild, Node newChild);
}
