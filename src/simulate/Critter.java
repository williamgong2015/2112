package simulate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ast.ProgramImpl;
import exceptions.SyntaxError;
import intial.Constant;
import parse.ParserImpl;
import parse.Tokenizer;

/**
 * A critter object in the world, store the properties of the critter
 * 
 */
public class Critter extends Element {

	private int[] mem;
	private int orientation;
	private String name;
	private ProgramImpl pro;
	
	public Critter(String file) throws IOException, SyntaxError {
		super("CRITTER");
		FileReader f = new FileReader(new File(file));
		BufferedReader br = new BufferedReader(f);
		String n = br.readLine();
		int i = 0;
		for(;n.charAt(i) != ':';i++);
		i++;
		name = n.substring(i).trim();
		String s = br.readLine();
		i = 0;
		while(s.charAt(i) > '9' || s.charAt(i) < '0')
			i++;
		int temp = Integer.parseInt(s.substring(i).trim());
		if(temp < Constant.MIN_MEMORY)
			temp = Constant.MIN_MEMORY;
		mem = new int[temp];
		for(int j = 1;j < 5;j++) {
			s = br.readLine();
			i = 0;
			while(s.charAt(i) > '9' || s.charAt(i) < '0')
				i++;
			temp = Integer.parseInt(s.substring(i).trim());
			mem[j] = temp;
		}
		s = br.readLine();
		i = 0;
		while(s.charAt(i) > '9' || s.charAt(i) < '0')
			i++;
		temp = Integer.parseInt(s.substring(i).trim());
		mem[7] = temp;
		Tokenizer t = new Tokenizer(br);
		pro = ParserImpl.parseProgram(t);
	}
	/**
	 * set the specified memory {@code mem[index]} to
	 * specified value {@code val},some memory couldn't be set
	 */
	public void setMem(int index, int val) {
		if(index >= 0 && index <= 4)
			return;
		if(index == 7 || index == 6) {
			if(val < 0 || val > 99)
				return;
		}
		mem[index] = val;
	}
	
	/** 
	 * @return the specified memory{@code mem[index]}
	 */
	public int getMem(int index) {
		if(index >= mem[0] || index <= 0)
			return 0;
		return mem[index];
	}

	/**
	 * @return a position which is at direction
	 * {@code index} of the critter
	 */
	public Position nearby(int index) {
		int temp = (index + orientation) % 6;
		if(temp < 0)
			temp += 6;
		Position p = this.getPosition().get(temp);
		return p;
	}
	
	/**
	 * @return the direction of the critter
	 */
	public int getDir() {
		return orientation;
	}
	/**
	 * @return the calculated appearance of the critter
	 */
	public int appearance() {
		return mem[3] * 100000 + mem[6] * 1000 + mem[7] * 10 + orientation;
	}
	
	/**
	 * if left is true,the critter will turn left;
	 * otherwise it will turn right
	 */
	public void Turn(boolean left) {
		if(left == true)
			orientation--;
		else orientation++;
		if(orientation > 5)
			orientation %= 6;
		if(orientation < 0)
			orientation += 6;
	}
	
	/**
	 * check if the critter has used up its energy
	 */
	public boolean stillAlive() {
		return mem[3] <= 0;
	}
	
	/**
	 * set the direction of the critter
	 */
	public void setDir(int dir) {
		orientation = dir;
	}
	
	/**
	 * return the name of the critter
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * return the maximum energy that this critter could have
	 */
	public int maxEnergy() {
		return mem[3] * Constant.ENERGY_PER_SIZE;
	}
	
	/**
	 * @return the position which is in front of the critter;
	 */
	public Position inFront() {
		Position front = this.getPosition();
		return front.getRelativePos(1, this.orientation);
	}
	
	/**
	 * return a description of the critter
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder ();
		sb.append("SPECIES:" + name + "\n");
		sb.append("MEMSIZE:" + mem[0] + "\n");
		sb.append("DEFENSE:" + mem[1] + "\n");
		sb.append("OFFENSE:" + mem[2] + "\n");
		sb.append("SIZE:" + mem[3] + "\n");
		sb.append("ENERGY:" + mem[4] + "\n");
		sb.append("PASS:" + mem[5] + "\n");
		sb.append("TAG:" + mem[6] + "\n");
		sb.append("POSTURE:" + mem[7] + "\n");
		return sb.toString() + this.pro.toString();
	}
}
