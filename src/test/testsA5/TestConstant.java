package test.testsA5;

import java.io.IOException;

import game.constant.Constant;

public class TestConstant {

	
	public static void main(String[] args) throws IOException {
		Constant.init();
		
		System.out.println(Constant.ABILITY_COST);
	}
}
