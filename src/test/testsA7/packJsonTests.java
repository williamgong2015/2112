package test.testsA7;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import api.PackJson;
import game.exceptions.SyntaxError;
import servlet.element.Critter;
import servlet.element.Food;
import servlet.element.Rock;
import servlet.world.Position;
import servlet.world.World;

/**
 *  tests for Json to see if we get the right api
 *  some tests were obsolete 
 *
 */
public class packJsonTests {
	public static void main(String[] args) throws IOException, SyntaxError {
		System.out.println(PackJson.packSessionID(10));
		Critter c = new Critter(new File("critter1.txt"),0,0);
		c.setPosition(new Position(3,5));
		System.out.println(PackJson.packPassword("abcd", "abcd"));
		ArrayList<Critter> al = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			al.add(c);
		}
		System.out.println(PackJson.packListOfCritters(al, 2,false));
		System.out.println(PackJson.packListOfCritters(al, 2,true));
		World w = new World(10,10,"test");
		w.setElemAtPosition(c, new Position(4,5));
		Critter d = new Critter(new File("critter1.txt"),0,0);
		w.setElemAtPosition(d, new Position(4,4));
		w.setElemAtPosition(new Rock(), new Position(5,5));
		w.setElemAtPosition(new Food(12), new Position(5,5));
		Critter e = new Critter(new File("critter1.txt"),0,0);
		w.setElemAtPosition(e, new Position(6,5));
		System.out.println(PackJson.packStateOfWorld(w, 1, false, 0, 0, 0, 0, 0, false));
		System.out.println(PackJson.packStateOfWorld(w, 2, false, 0, 0, 0, 0, 0, false));

	}
}
