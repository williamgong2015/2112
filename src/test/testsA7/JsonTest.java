package test.testsA7;


import java.io.IOException;

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
	
	public static void main(String[] args) throws IOException {
		JsonTest js = new JsonTest();
		js.test();
	}
	
	public void test() throws IOException {
		Gson gson = new Gson();
		String s = "abc" + '\n' + "ghjk";
		System.out.println(s);
		String x = gson.toJson(s);
		String t = gson.fromJson(x, String.class);
		System.out.println(t);
	}
}
