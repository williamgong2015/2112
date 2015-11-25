package game.utils;

public class EqualityCheck {

	
	public static boolean checkIsOneOf(Object[] possible, Object tocheck) {
		for (int i = 0; i < possible.length; ++i) {
			if (possible[i].equals(tocheck))
				return true;
		}
		return false;
	}
}
