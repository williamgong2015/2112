package ast;

import java.util.ArrayList;

/**
 * Interface of nodes that has variable number children but at least 1
 */
public interface Placeholder extends Node {
	
	/** @return the number of children in the placeholder */
	int numOfChildren();
	
	/** 
	 * Effect: set the child at index {@code index}
	 * with {@code newChild}
	 * Require: index inside the bound
	 */
	void setChild(int index, Node newChild);
	
	/**
	 * Effect: get the child at index {@code index}
	 * Require: index inside the bound
	 */
	Node getChild(int index);
	
	/** @return the index of {@code child} in its child nodes */
	int indexOfChild(Node child);
	
	/** @return an ArrayList of children */
	public ArrayList<?> getChildren();
}


