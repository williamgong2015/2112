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
import game.exceptions.SyntaxError;

public class test {
	public static void main(String[] args) throws IOException, SyntaxError {
		System.out.println(new ClientPosition(2,4,0,0,0));
		System.out.println(PositionInterpreter.clientToServer(
				new ClientPosition(2,4,0,0,0)));
	}
}