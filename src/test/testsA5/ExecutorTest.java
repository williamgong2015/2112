package test.testsA5;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import game.constant.Constant;
import game.constant.DIR;
import game.constant.IDX;
import game.exceptions.SyntaxError;
import servlet.element.Critter;
import servlet.element.Food;
import servlet.world.Position;
import servlet.world.World;

/**
 * Black box test of Executor
 * Initialize world and critter, use some Outcome as input, 
 * check every kind of executing methods if the critter status and 
 * world changed as expected
 * 
 * Randome test of Executor
 * Generate a random world and insert random number of element as WorldTest,
 * Generate a random commands like the random test in InterpreterTest,
 * Use these two as input for the executor to check it won't crash, and the 
 * output is a legal state of World and Critter.
 *
 */
public class ExecutorTest {
	
	int session_id = 0;
	
	/**
	 * Test a single critter being update and can WAIT as expected
	 *     some memory shouldn't be updated,
	 *     some memory has a limited range of valid value
	 *     if no action happens, a critter will keep doing update
	 *     until it has finished 999 PASS
	 * @throws SyntaxError 
	 * @throws IOException 
	 */
	@Test
	public void testUpdate() throws IOException, SyntaxError {
		final int POSTURE = 10; 
		
		World w = World.loadWorld("txt/UpdateTest/world.txt", session_id);
		Critter c = w.order.get(0);
		
		// check the initial state of the critter 
		int[] refMem = {9, 2, 3, 1, 100, 1, 0, 17, 0};
		for (int i = 0; i < refMem.length; ++i)
			assertTrue("memory " + i + " doesn't get initialized as expected",
					c.getMem(i) == refMem[i]);
		
		w.lapse();
		refMem[7] = POSTURE;
		refMem[4] += Constant.SOLAR_FLUX;  // energy increase because of wait
		// the index of last rule being executed before came to the MAX_PASS
		refMem[8] = Constant.MAX_PASS % c.getProgram().getChildren().size();
		
		// check the state of the critter changed as expected
		for (int i = 0; i < refMem.length; ++i)
			assertTrue("memory " + i + " doesn't get initialized as expected",
					c.getMem(i) == refMem[i]);
	}
	/**
	 * Test single critter MOVE forward and backward
	 * @throws SyntaxError 
	 * @throws IOException 
	 */
	@Test
	public void testMove() throws IOException, SyntaxError {
		World w = World.loadWorld("txt/MoveTest/world.txt", session_id);
		Critter c = w.order.get(0);
		final int INITENERGY = 13;
		
		// check the initial status of the critter
		assertTrue("the critter dones't got initialized with expected energy",
				c.getMem(IDX.ENERGY) == INITENERGY);
		assertTrue("the critter doesn't got initialized to the expected "
				+ " position", c.getPosition().equals(new Position(2,2)));
		
		// the critter start to keep moving forward
		w.lapse();
		assertTrue("the critter doesn't moved as expected",
				c.getPosition().equals(new Position(2,3)));
		assertTrue("the last rule executed hasn't got set as expected",
				c.getLastRuleExe().toString().trim().
				equals("1 = 1 --> forward;"));
		assertTrue("the critter's energy doesn't changed as expected",
				c.getMem(IDX.ENERGY) == 
				INITENERGY - Constant.MOVE_COST * c.getMem(IDX.SIZE));
		w.lapse();
		w.lapse();
		assertTrue("the critter doesn't moved as expected",
				c.getPosition().equals(new Position(2,4)));
		
		// critter start to move backward
		w.lapse();
		assertTrue("the critter doesn't moved as expected",
				c.getPosition().equals(new Position(2,3)));
		assertTrue("the last rule executed hasn't got set as expected",
				c.getLastRuleExe().toString().trim().
				equals("mem[4] <= 4 --> backward;"));
		
		// the critter should die because of the movement
		w.lapse();
		assertTrue("the critter doesn't die as expected",
				w.order.size() == 0);
		assertTrue("Critter doesn't turn into expected amount of food at "
				+ "expected position", 
				((Food)w.getElemAtPosition(new Position(2,3))).getAmount()
				== 200);
		
		// try if the world can still lapse even if there are no critter
		w.lapse();
	}
	
	/**
	 * Test single critter TURN left and right
	 * @throws SyntaxError 
	 * @throws IOException 
	 */
	@Test
	public void testTurn() throws IOException, SyntaxError {
		World w = World.loadWorld("txt/TurnTest/world.txt", session_id);
		Critter c = w.order.get(0);
		final int INITENERGY = 7;

		
		// check the initial status of the critter
		assertTrue("the critter dones't got initialized with expected energy",
				c.getMem(IDX.ENERGY) == INITENERGY);
		assertTrue("the critter doesn't got initialized to the expected "
				+ " position", c.getPosition().equals(new Position(2,2)));
		
		// the critter keep turning right
		w.lapse();
		assertTrue("the critter doesn't turn as expected",
				c.getDir() == DIR.RFRONT);
		assertTrue("the critter unexpectedly moved",
				c.getPosition().equals(new Position(2,2)));
		assertTrue("the last rule executed hasn't got set as expected",
				c.getLastRuleExe().toString().trim().
				equals("1 = 1 --> right;"));
		assertTrue("the critter's energy doesn't changed as expected",
				c.getMem(IDX.ENERGY) == INITENERGY - c.getMem(IDX.SIZE));
		w.lapse();
		w.lapse();
		assertTrue("the critter doesn't turn as expected",
				c.getDir() == DIR.BACK);
		
		// critter start to turn left
		w.lapse();
		assertTrue("the critter doesn't turn as expected",
				c.getDir() == DIR.RBACK);
		assertTrue("the last rule executed hasn't got set as expected",
				c.getLastRuleExe().toString().trim().
				equals("mem[4] <= 4 --> left;"));
		
		// the critter should die because of the turning
		w.lapse();
		w.lapse();
		w.lapse();
		assertTrue("the critter doesn't die as expected",
				w.order.size() == 0);
		assertTrue("Critter doesn't turn into expected amount of food at "
				+ "expected position", 
				((Food)w.getElemAtPosition(new Position(2,2))).getAmount()
				== 200);
		
		// try if the world can still lapse even if there are no critter
		w.lapse();
	}

	/**
	 * Test a single critter EAT food
	 * @throws SyntaxError 
	 * @throws IOException 
	 */
	@Test
	public void testEat() throws IOException, SyntaxError {
		World w = World.loadWorld("txt/EatTest/world.txt", session_id);
		Critter c = w.order.get(0);
		// place some food in front of the critter
		Position pos1 = new Position(2,2);
		Position pos2 = new Position(2,3);
		Position pos3 = new Position(2,4);
		final int INITENERGY = 100; 
		final int FOODENERGY = 200;
		
		// the critter eat one piece of food in front of it
		assertTrue("there is a food in front of the critter", 
				w.getElemAtPosition(pos1).equals(new Food(FOODENERGY)));
		w.lapse();
		assertTrue("critter doesn't eat the food and increase energy", 
				c.getMem(IDX.ENERGY) == 
				INITENERGY + FOODENERGY - c.getMem(IDX.SIZE));
		assertTrue("the food doesn't got removed", 
				w.getElemAtPosition(pos1) == null);
		
		// the critter walk forward one step
		w.lapse();
		assertTrue("critter doesn't move ahead", 
				c.getPosition().equals(pos1));

		// the critter eat one piece of food in front of it
		w.lapse();
		assertTrue("critter doesn't eat the food and increase energy", 
				c.getMem(IDX.ENERGY) == INITENERGY + FOODENERGY * 2 
				- c.getMem(IDX.SIZE) * 2
				- Constant.MOVE_COST * c.getMem(IDX.SIZE));
		assertTrue("the food doesn't got removed", 
				w.getElemAtPosition(pos2) == null);
		
		// the critter move forward one step
		w.lapse();
		assertTrue("critter doesn't move ahead", 
				c.getPosition().equals(pos2));
		
		// the critter only eat a certain amount of food because it will reach 
		// its max amount of energy it can have,
		// then it can't move because there is a the food in front of it
		w.lapse();
		assertTrue("critter moved", 
				c.getPosition().equals(pos2));
		assertTrue("there is a food in front of the critter", 
				w.getElemAtPosition(pos3).equals(
					new Food(FOODENERGY - 3 * c.getMem(IDX.SIZE) -
						2 * Constant.MOVE_COST * c.getMem(IDX.SIZE))));
	}
	
	/**
	 * Test a single critter SERVE food to the hex in front of it
	 * @throws IOException
	 * @throws SyntaxError
	 */
	@Test
	public void testServe() throws IOException, SyntaxError {
		World w = World.loadWorld("txt/ServeTest/world.txt", session_id);
		Critter c = w.order.get(0);
		Position pos1 = new Position(2,2);
		final int INITENERGY = 100; 
		final int SERVEENERGY = 10;
		final int FOODENERGY = 5;
		
		assertTrue("the initial energy of critter is wrong",
				c.getMem(IDX.ENERGY) == INITENERGY);
		
		// the critter serve some energy to the food in front of it
		w.lapse();
		assertTrue("the food doesn't got the energy", 
				w.getElemAtPosition(pos1).equals(
						new Food(FOODENERGY + SERVEENERGY)));
		assertTrue("the critter doesn't reduce its energy as expected",
				c.getMem(IDX.ENERGY) == 
				INITENERGY - SERVEENERGY - c.getMem(IDX.SIZE));
		
		// the hex on the right front of the critter would get served
		w.lapse();
		w.lapse();
		w.printASCIIMap();
		assertTrue("the food doesn't got the energy",
				w.getElemAtPosition(new Position(3,2)).equals(
						new Food(SERVEENERGY)));
		assertTrue("the critter doesn't reduce its energy as expected",
				c.getMem(IDX.ENERGY) == 
				INITENERGY - SERVEENERGY*2 - c.getMem(IDX.SIZE)*3);
		
		// the critter serve the food to outside of the world 
		// (the food will be destroyed and critter still lose energy)
		w.lapse();
		w.lapse();
		w.printASCIIMap();
		assertTrue("the critter doesn't reduce its energy as expected",
				c.getMem(IDX.ENERGY) == 
				INITENERGY - SERVEENERGY*3 - c.getMem(IDX.SIZE)*5);
		
	}
	
	/**
	 * Test a single critter GROW
	 * @throws IOException
	 * @throws SyntaxError
	 */
	@Test
	public void testGrow() throws IOException, SyntaxError {
		World w = World.loadWorld("txt/GrowTest/world.txt", session_id);
		Critter c = w.order.get(0);
		Position pos = c.getPosition();
		final int INITENERGY = 200;
		
		assertTrue("the critter doesn't got initialized to expected size",
				c.getMem(IDX.SIZE) == 1);
		
		// the critter grow
		w.lapse();
		assertTrue("the critter doesn't grow into expected size",
				c.getMem(IDX.SIZE) == 2);
		assertTrue("the critter doesn't decrease it energy as expected",
				c.getMem(IDX.ENERGY) == INITENERGY -(c.getMem(IDX.SIZE) - 1)
				* c.getComplexity() * Constant.GROW_COST);
		
		// the critter die because of grow
		w.lapse();
		assertTrue("the critter doesn't turn into food because of growth", 
				w.getElemAtPosition(pos).equals(new Food(
						c.getMem(IDX.SIZE) * Constant.FOOD_PER_SIZE)));
		assertTrue("the critter doesn't die",
				w.order.size() == 0);
	}
	
	/**
	 * Test a critter {@code c1} TAG another critter {@code c2}
	 * @throws IOException
	 * @throws SyntaxError
	 */
	@Test
	public void testTag() throws IOException, SyntaxError {
		World w = World.loadWorld("txt/TagTest/world.txt", session_id);
		Critter c1 = w.order.get(0);
		Critter c2 = w.order.get(1);
		final int TAGVAL = 23;
		w.printCoordinatesASCIIMap();
		w.printASCIIMap();
		
		// Critter c1 start to tag Critter c2
		int c1Energy = c1.getMem(IDX.ENERGY);
		int c1Size = c1.getMem(IDX.SIZE);
		c1Energy -= c1Size;
		w.lapse();
		assertTrue("Critter c2 has not been tag", 
				c2.getMem(IDX.TAG) == TAGVAL);
		assertTrue("Critter c1 doesn't lose the energy to tag",
				c1.getMem(IDX.ENERGY) == c1Energy);
		
		// Critter c1 still try to tag some critter 
		w.lapse();
		c1Size = c1.getMem(IDX.SIZE);
		c1Energy -= c1Size;
		assertTrue("Critter still lose energy although there is "
				+ "no critter for it to tag", 
				c1.getMem(IDX.ENERGY) == c1Energy);
		
	}
	
	/**
	 * Test a critter {@code c1} ATTACK another critter {@code c2}
	 * @throws IOException
	 * @throws SyntaxError
	 */
	@Test
	public void testAttack() throws IOException, SyntaxError {
		World w = World.loadWorld("txt/AttackTest/world.txt", session_id);
		Critter c1 = w.order.get(0);
		Critter c2 = w.order.get(1);
		Position posC2 = c2.getPosition();
		w.printCoordinatesASCIIMap();
		w.printASCIIMap();
		
		// Critter c1 start to attack Critter c2
		int c1Energy = c1.getMem(IDX.ENERGY);
		int c2Energy = c2.getMem(IDX.ENERGY);
		int c1Size = c1.getMem(IDX.SIZE);
		int c2Size = c2.getMem(IDX.SIZE);
		c1Energy -= c1Size * Constant.ATTACK_COST;
		int da = (int) (Constant.BASE_DAMAGE * c1Size * game.utils.Formula.logistic(
				Constant.DAMAGE_INC * (c1Size * c1.getMem(IDX.OFFENSE) -
						               c2Size * c2.getMem(IDX.DEFENSE))));
		c2Energy -= da;
		c2Energy += c2Size * Constant.SOLAR_FLUX;
		w.lapse();
		
		assertTrue("Critter c2 doesn't lose energy as expected", 
				c2.getMem(IDX.ENERGY) == c2Energy);
		assertTrue("Critter c1 doesn't lose the energy to attack",
				c1.getMem(IDX.ENERGY) == c1Energy);
		
		
		// c1 keep attack c2, but c2 is still alive
		w.lapse();
		c2Energy -= da;
		c2Energy += c2Size * Constant.SOLAR_FLUX;
		assertTrue("Critter c2 doesn't lose energy as expected", 
				c2.getMem(IDX.ENERGY) == c2Energy);
		System.out.println(c2);
		
		w.lapse();
		c2Energy -= da;
		c2Energy += c2Size * Constant.SOLAR_FLUX;
		assertTrue("Critter c2 doesn't lose energy as expected", 
				c2.getMem(IDX.ENERGY) == c2Energy);
		
		w.lapse();
		c2Energy -= da;
		c2Energy += c2Size * Constant.SOLAR_FLUX;
		assertTrue("Critter c2 doesn't lose energy as expected", 
				c2.getMem(IDX.ENERGY) == c2Energy);
		
		w.lapse();
		c2Energy -= da;
		c2Energy += c2Size * Constant.SOLAR_FLUX;
		assertTrue("Critter c2 doesn't lose energy as expected", 
				c2.getMem(IDX.ENERGY) == c2Energy);
		
		// c2 is killed
		w.lapse();
		assertTrue("c2 has not been turn into food",
				w.getElemAtPosition(posC2).
				equals(new Food(c2Size * Constant.FOOD_PER_SIZE)));
		assertTrue("c2 has not been removed from the list",
				w.order.size() == 1 && w.order.get(0).equals(c1));
				
	}
	
	
	/**
	 * Test a critter {@code c1} BUD and create a new critter {@code c2}
	 * and the new critter has the property as expected
	 * @throws IOException
	 * @throws SyntaxError
	 */
	@Test
	public void testBud() throws IOException, SyntaxError {
		World w = World.loadWorld("txt/BudTest/world.txt", session_id);
		Critter c1 = w.order.get(0);
		Position posC1 = c1.getPosition();
		w.printCoordinatesASCIIMap();
		w.printASCIIMap();
		
		// Critter c1 bud and create a new critter c2 behind it
		int c1Energy = c1.getMem(IDX.ENERGY);
		c1Energy -= Constant.BUD_COST * c1.getComplexity();
		w.lapse();
		assertTrue("Critter c1 has decrease certain amount of energy",
				c1.getMem(IDX.ENERGY) == c1Energy);
		
		// c2 may turn into food if it bud again 
		if (w.order.size() == 1) {
			assertTrue("Critter c2 doesn't turn into food as expected",
					w.getElemAtPosition(posC1.getRelativePos(1, DIR.BACK))
							.equals(new Food(Constant.FOOD_PER_SIZE * 
									Constant.INIT_SIZE)));
			return;
		}
		assertTrue("Critter c2 has not been created",
				w.order.size() == 2);
		Critter c2 = w.order.get(1);
		assertTrue("Critter c2 doesn't wait and gather energy in"
				+ "the round it is created", 
				c2.getMem(IDX.ENERGY) == Constant.INITIAL_ENERGY + 1);
		for (int i = 0; i < Constant.MIN_MEMORY; ++i) {
			if (i == IDX.ENERGY || i == IDX.SIZE || i == IDX.POSTURE
					|| i == IDX.TAG)
				continue;
			assertTrue("Critter c2 doesn't inherient it parent as expected",
				c1.getMem(i) == c2.getMem(i));
		}
	}
	
	
	/**
	 * Test a critter {@code c1} MATE with another critter {@code c2}
	 * and create a new critter {@code c3}
	 * 
	 * and the new critter has the property as expected
	 * @throws IOException
	 * @throws SyntaxError
	 */
	@Test
	public void testMate() throws IOException, SyntaxError {
		World w = World.loadWorld("txt/MateTest/world.txt", session_id);
		Critter c1 = w.order.get(0);
		Critter c2 = w.order.get(1);
		
		int c1Energy = c1.getMem(IDX.ENERGY);
		int c2Energy = c2.getMem(IDX.ENERGY);
		
		// check the Critter c1 want to mate in this turn
		// but it doesn't success because Critter c2 wait in this turn
		c1Energy -= c1.getMem(IDX.SIZE);
		c2Energy += c2.getMem(IDX.SIZE);
		
		w.lapse();
		assertTrue("Critter c1 doesn't try to mate", 
				c1.getWantToMate() == true);
		assertTrue("Critter c1 doesn't lose energy because it try to mate",
				c1.getMem(IDX.ENERGY) == c1Energy);
		assertTrue("Critter c2 doesn't get energy when it should wait",
				c2.getMem(IDX.ENERGY) == c2Energy);
		assertTrue("The number of citters in the world is not as expected",
				w.order.size() == 2);

		
		// Critter c1 mate with Critter c2 in this turn
		// a new Critter c3 is created in this turn either after c1 or c2
		w.lapse();
		assertTrue("Critter c1 doesn't lose the energy as expected", 
				c1.getMem(IDX.ENERGY) == 239);
		assertTrue("Critter c2 doesn't lose the energy as expected",
				c2.getMem(IDX.ENERGY) == 231);
		assertTrue("The number of citters in the world is not as expected",
				w.order.size() == 3);
		Critter c3 = w.order.get(2);
		assertTrue("The new critter is at the expected location", 
				w.getElemAtPosition(new Position(2,1)) == c3 ||
				w.getElemAtPosition(new Position(2,4)) == c3);
		for (int i = 0; i < 3; ++i)
			assertTrue("critter doesn't inherient the property of its parents",
				c3.getMem(i) == c1.getMem(i) || c3.getMem(i) == c2.getMem(i));
		assertTrue("Critter hasn't got initialized as expected",
				c3.getMem(IDX.SIZE) == 1 && 
				( c3.getMem(IDX.ENERGY) == Constant.INITIAL_ENERGY -
				  c3.getMem(IDX.SIZE) || 
				  c3.getMem(IDX.ENERGY) == Constant.INITIAL_ENERGY + 
				  c3.getMem(IDX.SIZE)) &&
				c3.getMem(IDX.PASS) == 1 &&
				( c3.getMem(IDX.POSTURE) == 0 ||
				  c3.getMem(IDX.POSTURE) == 10));
		assertTrue("Critter doesn't have the rule as expected", 
				c3.getProgram().getChildren().get(0).toString().equals( 
				c2.getProgram().getChildren().get(0).toString()) || 
				c3.getProgram().getChildren().get(0).toString().equals(
				c1.getProgram().getChildren().get(0).toString()));
		if (c3.getProgram().getChildren().size() == 2) 
			assertTrue("Critter doesn't have the rule as expected", 
					c3.getProgram().getChildren().get(1).toString().trim()
					.equals(c2.getProgram().getChildren().get(1).toString().trim()));
		
		
		// Critter c1 and c2 try to mate with each other again, but they 
		// fail because they can't afford the energy
		w.lapse();
		assertTrue("Critter c1 doesn't have the energy as expected", 
				c1.getMem(IDX.ENERGY) == 238);
		assertTrue("Critter c2 doesn't have the energy as expected",
				c2.getMem(IDX.ENERGY) == 230);
		System.out.println(c3.getMem(IDX.ENERGY));
		assertTrue("Critter c3 doesn't have the energy as expected",
				c3.getMem(IDX.ENERGY) == 250 || c3.getMem(IDX.ENERGY) == 248
				|| c3.getMem(IDX.ENERGY) == 252);
		
	}
	
	
	/**
	 * Test single critter walking a spiral 
	 * @throws SyntaxError 
	 * @throws IOException 
	 */
	@Test
	public void testSpiralMove() throws IOException, SyntaxError {
		World w = World.loadWorld("txt/SpiralMoveTest/world.txt", session_id);
		Critter c = w.order.get(0);
		w.printCoordinatesASCIIMap();
		w.printASCIIMap();
		
		for (int i = 0; i < 100; ++i) {
			w.lapse();
			w.printASCIIMap();
			System.out.println("Critter energy: " + c.getMem(IDX.ENERGY));
		}
	}
	
	/**
	 * Test single critter walking a spiral 
	 * @throws SyntaxError 
	 * @throws IOException 
	 */
	@Test
	public void testThousandSteps() throws IOException, SyntaxError {
		World w = new World();
		Critter.loadCrittersIntoWorld(w, 
				"txt/ThousandSteps/critter.txt", 200, session_id);
		Food.loadFoodIntoWorld(w, 10, 20);
		w.printASCIIMap();
		
		for (int i = 0; i < 1000; ++i) {
			w.lapse();
			assertTrue(w.getTurns() == i+1);
			if (i % 100 == 0) {
				System.out.println(i + "th iteration");
				System.out.println("there are " + w.order.size() +
						" critters in the world");
				w.printASCIIMap();
				System.out.println("\n \n ");
			}
		}
	}
		
	
	
}
