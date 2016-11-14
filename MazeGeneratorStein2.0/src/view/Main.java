/*	Tabitha Stein 	*
 *	Maze Generator	*/

package view;

import java.awt.EventQueue;

/**
 * Runs a new MazeGUI.
 * @author Tabitha Stein
 * @version 1.0
 */
public class Main {

    public static void main(final String[] theArgs) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MazeGUI(); // create and start the graphical user interface
            }
        });
    }
	
}
