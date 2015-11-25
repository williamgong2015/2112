package game.utils;


import com.google.gson.Gson;


public class JsonTest {

	public class Tmp2{
		public char c1;
		public int[] mem;
	}
	public class Tmp {

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
		Tmp2 t = new Tmp2();
		t.mem = new int[3];
		t.mem[0] = 12;
		t.mem[1] = 23;
		t.c1 = 'a';
		String s= gson.toJson(t);
		Tmp tx = gson.fromJson(s, Tmp.class);
		System.out.println(tx.c1);
		System.out.println(tx.int1);
		System.out.println(tx.str1);
	}
}
