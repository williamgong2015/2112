package util;

import java.io.IOException;

import com.google.gson.Gson;

import ast.ProgramImpl;
import exceptions.SyntaxError;
import simulate.Critter;
import simulate.World;

public class JsonTest {

	public class Tmp {
		
		public Tmp() {
			
		}
		public String str1;
		public int int1;
		public char c1;
		public int[] mem;
	}
	
	public static void main(String[] args) {
		JsonTest js = new JsonTest();
		js.test();
	}
	
	public void test() {
		Gson gson = new Gson();
		Tmp t = new Tmp();
//		t.str1 = "string";
//		t.int1 = 100;
		t.c1 = 'c';
//		t.mem[0] = 12;
//		t.mem[1] = 23;
		Critter c;
		try {
			c = new Critter("critter1.txt");
			System.out.println(c);
			ProgramImpl p = c.getProgram();
			System.out.println(gson.toJson(t, Tmp.class));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SyntaxError e) {
			e.printStackTrace();
		}
	}
}
