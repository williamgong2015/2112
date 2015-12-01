package test.testsA7;

import client.gui.GUIHex;
import client.world.ClientPosition;
import servlet.world.World;

public class testClientPosition {

	public static void main(String[] args) {
		
		World world = new World();
		System.out.println(world.printCoordinatesASCIIMap());
		
		
		ClientPosition pos = new ClientPosition(10, 31, 68, GUIHex.HEX_SIZE);
		
		System.out.println(pos);
	}
}
