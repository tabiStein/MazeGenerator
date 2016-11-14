/*	Model Tests; Exclude from build path when bulding with GUI.	*/

package model;


import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * A controller for the Maze class. Note that the Maze does not automatically solve itself
 * upon construction; one must call the dfs() (depth first search) method to solve it.
 * I did this so I could time generation and solving separately.
 * @author Tabitha Stein
 * @version 1.0
 */
public class Main {

	/**Runs the method for the assignment and some tests.*/
	public static void main(String[] args) {
		assignment();
		
		/*Tests*/
		//timedTest(100, 100);				
		//paramExceptionsTest();
		//randomStartAndExitTest();
		//startAndExitTest();
		//oddSizesTest();
	}
	
	/**This method executes the requirements of a 5 x 5 maze with debugging and a larger
	 * maze without debugging, as per the assignment specs.*/
	private static void assignment() {
		
		Maze m1 = new Maze(5, 5, true);
		m1.display();
		MazeObserver mb = new MazeObserver();
		m1.addObserver(mb);
		m1.prim();
		System.out.println("DFS");
		m1.dfs();
		m1.display();
		
		System.out.println("BFS");
		m1.bfs();

		m1.display();
		
//		System.out.println("Larger, Debug off");
//		Maze m2 = new Maze(20, 20, false);
//		m2.prim();
//		m2.dfs();
//		m2.display();
	}
	
	/**
	 * Times how long it takes to generate a massive maze, so I can determine if I need to
	 * optimize the algorithms.
	 * @param m the m dimension
	 * @param n the n dimension
	 */
	private static void timedTest(int m, int n) {
		long startTime = System.currentTimeMillis();
		
		Maze massive = new Maze(m, n, false);
		
		//massive.display();
		System.out.printf("Time to generate %d x %d maze: %d milliseconds\n", 
				m, n, System.currentTimeMillis() - startTime);
		
		startTime = System.currentTimeMillis();
		massive.dfs();
		System.out.printf("Time to solve %d x %d maze: %d milliseconds\n", 
				m, n, System.currentTimeMillis() - startTime);
	}
	

	
	/**
	 * Confirms that negative dimensions are handled.
	 */
	private static void paramExceptionsTest() {
		
		System.out.println("Negative horizontal dimension");
		Maze negHoriz = new Maze(-20, 20, false);
		negHoriz.display();
		System.out.println("Negative vertical dimension");
		Maze negVert = new Maze(20, -20, false);
		negVert.display();
		
		//Noticed post-turn in that these will throw exceptions when dfs is called on them
		//likely because there's nothing to search??
		System.out.println("Zero horizontal dimension");
		Maze zeroHoriz = new Maze(0, 1, true);
		//zeroHoriz.dfs();
		zeroHoriz.display();
		System.out.println("Zero vertical dimension");
		Maze zeroVert = new Maze(1, 0, true);
		zeroVert.display();
		System.out.println("Zero both dimensions");
		Maze empty = new Maze(0, 0, true);
		empty.display();
	}
	

	
	/**
	 * Randomly generates start and exit locations to make sure Maze handles index out of 
	 * bounds problems
	 */
	private static void randomStartAndExitTest() {
		Random randy = new Random();
		System.out.println("Larger, Debug off, entrance and exit randomly specified.");
		int entranceColumn = randy.nextInt();		
		int exitColumn = randy.nextInt()/2;
		System.out.printf("Entrance column: %d\n", entranceColumn);
		System.out.printf("Exit column: %d\n", exitColumn);
		Maze m3 = new Maze(10, 20, entranceColumn, exitColumn, 's', 'f', false);
		m3.display();
	}
	
	/**
	 * Tests specific dimensions, including ones that caused exceptions in the 
	 * randomStartAndExitTest.
	 * One specifical situation:
	 * "Exception in thread "main" java.lang.IndexOutOfBoundsException: Index: 200, Size: 200"
	 * Seemed to happen when location came to 1 less than the perimeter. (59 and 60 in this case)
	 * Then I ran this method with the debugger and figured out which one of my formulas
	 * was off.
	 */
	private static void startAndExitTest() {
		//Both generated exceptions:
		Maze test1 = new Maze(10, 20, 0, 59,'s', 'f', false); //perimeter = 60, location = 59
		test1.display();
		Maze test2 = new Maze(5, 5, 0, 19, 's', 'f', false); //perimeter = 20, location = 19
		test2.display();
		
		//Confirms that the coordinate system I made works (Start and exit in correct locations)
		Maze test3 = new Maze(10, 20, 43, 2, 's', 'f', false);
		test3.dfs();
		test3.display();
	}
	
	/**
	 * Makes sure that certain dimensions -- n divisible by m, size 1 x 1 -- work properly.
	 */
	private static void oddSizesTest() {
		System.out.println("9 x 3 maze");
		Maze narrow = new Maze(9, 3, false);
		narrow.display();
		System.out.println("Baby maze!");
		Maze tiny = new Maze(1, 1, false);	
		tiny.display();
	}


	
	
	public static class MazeObserver implements Observer {
		
		@Override
		public void update(Observable o, Object arg) {
			if (o instanceof Maze) {
				Maze m = (Maze) o;
				//display m
				m.display();
			}
		}
		
		
	}


}
