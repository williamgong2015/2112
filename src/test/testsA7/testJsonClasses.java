package test.testsA7;

import java.io.IOException;

import api.PackJson;
import game.exceptions.SyntaxError;

public class testJsonClasses {
	public static void main(String[] args) throws IOException, SyntaxError {
//		Critter c = new Critter(new File("critter1.txt"),1,1);
//		ArrayList<ClientPosition> a = new ArrayList<>();
//		a.add(new ClientPosition(3,4)));
//		State s = new State(c.getPosition());
//		s.setCriiter(c);
//		System.out.print(PackJson.packCreateCritter(new ClientElement(s), a));
//		System.out.println(PackJson.packAdvWorldCount(3));
//		System.out.println(PackJson.packAdvanceWorldRate(3));
		String s = PackJson.packSessionID(123);
//		System.out.println(s);
//		s = PackJson.packRockorFood(3, 2,null, "rock");
		System.out.println(s);
	}
}
