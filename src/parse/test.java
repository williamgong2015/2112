package parse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class test {
	public static void main(String[] args) throws IOException {
	String name = "test.txt";
	BufferedReader f = new BufferedReader(new FileReader(name));
	Tokenizer t = new Tokenizer(f);
	Token i;
	while(t.hasNext()) {
		i = t.next();
		System.out.print(i.getType()+"    ");
		System.out.println(i.lineNo);
	}
	}
}

