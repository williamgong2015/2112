package client.element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import api.JsonClasses.CritterState;
import api.JsonClasses.FoodState;
import api.JsonClasses.NothingState;
import api.JsonClasses.RockState;
import api.JsonClasses.State;
import game.constant.Constant;
import game.constant.IDX;

/**
 * Element stored in client.world World
 * Could be Critter, Food, Rock or Nothing, indicated by {@code type}
 * some fields may be not used for some type of element. 
 */
public class ClientElement {

	public String type;
	public int value = -1;  // food amount
	public int id = -1;
	public String species_id;
	public int row = -1;
	public int col = -1;
	public int direction = -1;
	public int[] mem;
	public String program;
	public int recently_executed_rule = -1;
	
	public final static String SOMETHING = "something";
	
	/**
	 * Create a critter with critter file 
	 * {@code type}, {@code species_id}, {@code mem} and 
	 * {@code program} will get initialized
	 * @param file - critter file
	 */
	public ClientElement(File file) {
		try {
			FileReader f = new FileReader(file);
			BufferedReader br = new BufferedReader(f);
			String n = br.readLine();
			int i = 0;
			for(;n.charAt(i) != ':';i++);
			i++;
			species_id = n.substring(i).trim();
			String s = br.readLine();
			i = 0;
			while(s.charAt(i) > '9' || s.charAt(i) < '0')
				i++;
			int temp = Integer.parseInt(s.substring(i).trim());
			if(temp < Constant.MIN_MEMORY)
				temp = Constant.MIN_MEMORY;
			mem = new int[temp];
			mem[IDX.MEMLEN] = temp; // memsize
			for(int j = 1;j < 5;j++) {
				s = br.readLine();
				i = 0;
				while(s.charAt(i) > '9' || s.charAt(i) < '0')
					i++;
				temp = Integer.parseInt(s.substring(i).trim());
				mem[j] = temp;
			}
			mem[IDX.PASS] = Constant.INIT_PASS; // pass = 1
			s = br.readLine();
			i = 0;
			while(s.charAt(i) > '9' || s.charAt(i) < '0')
				i++;
			temp = Integer.parseInt(s.substring(i).trim());
			mem[IDX.POSTURE] = temp;
			StringBuilder builder = new StringBuilder();
			String next = br.readLine();
			while (next != null) {
				builder.append(next);
				next = br.readLine();
			}
			program = builder.toString();
			type = "critter";
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create a critter element with a CritterState {@code c}
	 * @param c
	 */
	public ClientElement(CritterState c) {
		type = c.getType();
		id = c.id;
		species_id = c.species_id;
		row = c.row;
		col = c.col;
		direction = c.direction;
		mem = c.mem;
		program = c.program;
		recently_executed_rule = c.recently_executed_rule;
	}

	/**
	 * Create a food element with a FoodState {@code s}
	 * @param s
	 */
	public ClientElement(FoodState s) {
		type = s.getType();
		row = s.row;
		col = s.col;
		value = s.value;
	}
	
	/**
	 * Create a rock element with a FoodState {@code s}
	 * @param s
	 */
	public ClientElement(RockState s) {
		type = s.getType();
		row = s.row;
		col = s.col;
	}
	
	/**
	 * Create a food element with a FoodState {@code s}
	 * @param s
	 */
	public ClientElement(NothingState s) {
		type = s.getType();
		row = s.row;
		col = s.col;
	}

	public ClientElement() {
		type = SOMETHING;
	}


	
}
