package testsA5;


import java.io.IOException;

import org.junit.Test;

import exceptions.SyntaxError;
import simulate.Critter;

public class critterTest {

	@Test
	public void test() throws IOException, SyntaxError {
		String file = "critter1.txt";
		Critter c = new Critter(file);
		System.out.println(c);
		
		//test if the memory size will be set to MIN_MEMORY
		file = "critter2.txt";
		c = new Critter(file);
		System.out.println(c);
	}

}
