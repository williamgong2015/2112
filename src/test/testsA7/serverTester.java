package test.testsA7;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import api.PositionInterpreter;
import api.JsonClasses.State;
import api.JsonClasses.WorldState;
import client.MyClient;
import client.element.ClientElement;
import client.world.ClientPosition;

public class serverTester {
	public static void main(String[] args) throws Exception {
		MyClient client = new MyClient("http://localhost:8080/2112/servlet/servlet.Servlet/");
//		ClientElement c = new ClientElement(new File("Critter1.txt"));
//		client.logIn("admin", "admin");
//		client.newWorld("123");
//		client.createCritter(new File("Critter1.txt"), null, 3);
//		client.advanceWorldByStep(3);
//		client.createFoodOrRock(new ClientPosition(3,4,0,0), null, "rock");
//		ArrayList<State> t = client.lisAllCritters();
//		for(State x : t) {
//			System.out.println(x);
//		}
//		WorldState t = client.getStateOfWorld(0, client.getSessionID());
//		for(State x :t.state) {
//			System.out.println(x);
//		}
		
		
		
		client.logIn("admin", "admin");
		ArrayList<ClientPosition> a = new ArrayList<>();
		a.add(new ClientPosition(3,4,0,0,0));
		a.add(new ClientPosition(2,4,0,0,0));
		a.add(new ClientPosition(5,4,0,0,0));
		client.createCritter(new File("Critter1.txt"), a, 0);
		client.removeCritter(5);
		WorldState t = client.getStateOfWorld(0);
		for(State x : t.state) 
			System.out.println(x);
	}
}
