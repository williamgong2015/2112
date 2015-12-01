package test.testsA7;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import api.JsonClasses.State;
import api.JsonClasses.WorldState;
import client.MyClient;
import client.element.ClientElement;
import client.world.ClientPosition;
import game.exceptions.SyntaxError;

public class serverTester {
	public static void main(String[] args) throws IOException, SyntaxError {
		MyClient client = new MyClient("http://localhost:8080/2112/servlet/servlet.Servlet/");
		ClientElement c = new ClientElement(new File("Critter1.txt"));
		client.logIn("admin", "admin");
		client.newWorld("123");
		client.createCritter(new File("Critter1.txt"), null, 3);
		client.advanceWorldByStep(3);
		client.createFoodOrRock(new ClientPosition(3,4,0,0), null, "rock");
//		ArrayList<State> t = client.lisAllCritters();
//		for(State x : t) {
//			System.out.println(x);
//		}
//		WorldState t = client.getStateOfWorld(0);
//		for(State x :t.state) {
//			System.out.println(x);
//		}
		ClientElement e= client.retrieveCritter(1);
//		System.out.println(e.col);
//		System.out.println(e.row);
		System.out.println(e.program);
//		System.out.println(e.id);
//		System.out.println(e.direction);
//		System.out.println(e.recently_executed_rule);
//		System.out.println(e.species_id);
	}
}
