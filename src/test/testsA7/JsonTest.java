package test.testsA7;


import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


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
		ArrayList<Integer> intArr = new ArrayList<>();
		intArr.add(1);
		intArr.add(2);
		Type t = new TypeToken<ArrayList<Integer>>(){}.getType();
		String s= gson.toJson(intArr, t);
		ArrayList<Integer> tx = (ArrayList<Integer>) gson.fromJson(s, t);
		System.out.println(s);
		for (Integer i : tx)
			System.out.println(i);
	}
}
