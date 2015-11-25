package servlet.executor;

import java.util.ArrayList;

import servlet.element.Critter;

/**
 * Two ArrayList of Critter, 
 * first ArrayList is the critters needed to be deleted after this turn
 * second ArrayList is the critters needed to be inserted after this turn
 *
 */
public class ResultList {
	
	public ArrayList<Critter> toDelete;
	public ArrayList<Critter> toInsert;
	
	public ResultList() {
		toDelete = new ArrayList<Critter>();
		toInsert = new ArrayList<Critter>();
	}
}
