package test.testsA7;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import api.JsonClasses.State;


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
		File f = new File("A7.txt");
		BufferedReader r = new BufferedReader(new FileReader(f));
		String t = r.readLine();
		System.out.println(t);
		gson.fromJson(t, State.class);
	}
}
