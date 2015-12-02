package test.testsA7;

import java.util.Timer;
import java.util.TimerTask;

public class test {
	
	public static void main(String[] args) {
		Timer t = new Timer();

		t.scheduleAtFixedRate(
		    new TimerTask()
		    {
		        public void run()
		        {
		            System.out.println("3 seconds passed");
		            
		            t.scheduleAtFixedRate(
		        		    new TimerTask()
		        		    {
		        		        public void run()
		        		        {
		        		            System.out.println("0.3 seconds passed");
		        		        }
		        		    },
		        		    0,      // run first occurrence immediately
		        		    300);  // run every three seconds
		        }
		    },
		    0,      // run first occurrence immediately
		    3000);  // run every three seconds
	}
	
}