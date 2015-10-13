package test;

import java.io.FileNotFoundException;
import java.io.FileReader;

import ast.*;
import parse.ParserImpl;

public class TestMutationRemove {
	
	public void testRemove() throws FileNotFoundException {
		FileReader f = new FileReader("test.txt");
		ParserImpl p = new ParserImpl();
		Program t = p.parse(f);
		System.out.println(t.nodeAt(0));
		AbstractMutation m = (AbstractMutation) MutationFactory.getRemove();

		for (int i = 80; i > 70; --i) {
			System.out.println();
			System.out.println(t.nodeAt(i));
			System.out.println(((MutableNode) t.nodeAt(i)).beMutated(m));
			System.out.println(t.nodeAt(0));
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		TestMutationRemove t = new TestMutationRemove();
		t.testRemove();
	}
}
