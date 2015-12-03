package test.testsA7;

import java.util.ArrayList;

import api.JsonClasses.*;
import servlet.element.Critter;
import api.PackJson.*;

public class packJsonTests {
	public static void main(String[] args) {
		System.out.println(packSessionID(10));
		Critter c = new Critter(new File("critter1.txt"));
		c.setPosition(new Position(3,5));
		System.out.println(packCritterWithAllFields(c));
		System.out.println(packPassword(3, "abcd"));
		System.out.println(packCritter(c,p));
		System.out.println(packCritterWithAllFields(c,p));
		ArrayList<Critter> al = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			Critter c = new Critter(new File("critter1.txt"));
			c.session_id = i;
			al.add(c);
		}
		System.out.println(packListOfCritters(al, 2));
		World w = new World(10,10,"test");
		Critter c = new Critter(new File("critter1.txt"));
		c.session_id =1;
		w.setElemAtPosition(c, new Position(4,5));
		Critter d = new Critter(new File("critter1.txt"));
		d.session_id =1;
		w.setElemAtPosition(d, new Position(4,4));
		w.setElemAtPosition(new Rock(), new Position(5,5));
		w.setElemAtPosition(new Food(12), new Position(5,5));
		Critter e = new Critter(new File("critter1.txt"));
		e.session_id = 2;
		w.setElemAtPosition(e, new Position(6,5));
		System.out.println(packStateOfWorld(w, 1));
//		System.out.println(packStateOfWorld(w, 2));

	}
}
