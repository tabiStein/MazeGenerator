package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import model.Maze;

/**A panel that displays given char arrays as the current state
 * of a Maze.*/
public class MazePanel extends JPanel {

	private final int myCellSize;
	
	private char[][] myDispArr;
	
	private char[][] myOverlayArr;
	
	private Color wallColor;
	
	private Color pathColor;
	
	private Color tentativeColor;
	
	private Color visitedColor;
	
	/**
	 * Accepts the dimensions of the Maze to display
	 */
	public MazePanel(char[][] theDisplayArray, int theCellSize) {
		myCellSize = theCellSize;
		myDispArr = theDisplayArray;
		myOverlayArr = new char[myDispArr.length][myDispArr[0].length];
		wallColor = Color.BLACK;		
		pathColor = Color.RED.darker().darker();	
		tentativeColor = new Color(0, 0, 0, 100);	
		visitedColor = Color.WHITE;
		this.setPreferredSize(new Dimension(myDispArr[0].length * myCellSize, myDispArr.length * myCellSize));
		this.setMinimumSize(new Dimension(myDispArr[0].length * myCellSize, myDispArr.length * myCellSize));
		this.setMaximumSize(new Dimension(myDispArr[0].length * myCellSize, myDispArr.length * myCellSize));
		this.setBackground(visitedColor);
		repaint();
	}
	
	public void setDisplayArr(char[][] theDisplayArray) {
		myDispArr = theDisplayArray;
		this.setPreferredSize(new Dimension(myDispArr[0].length * myCellSize, myDispArr.length * myCellSize));
		this.setMinimumSize(new Dimension(myDispArr[0].length * myCellSize, myDispArr.length * myCellSize));
		this.setMaximumSize(new Dimension(myDispArr[0].length * myCellSize, myDispArr.length * myCellSize));
		repaint();
		revalidate();
	}
	
	public void setOverlayArr(char[][] overlayArr) {
		myOverlayArr = overlayArr;
		repaint();
		
	}
	
    @Override
    public void paintComponent(final Graphics theGraphics) {
        super.paintComponent(theGraphics);
        final Graphics2D graphics2D = (Graphics2D) theGraphics;
        
        for (int m = 0; m < myOverlayArr.length; m++) {
        	for (int n = 0; n < myOverlayArr[m].length; n++) {
        		char cell = myOverlayArr[m][n];
        		if (cell == 0) {
        			cell = myDispArr[m][n];
        		}
        		switch (cell) {
	        		case Maze.WALL : graphics2D.setPaint(wallColor);
	        			break;
	        		case Maze.VISITED : graphics2D.setPaint(visitedColor);
	        			break;
	        		case ' ' : graphics2D.setPaint(visitedColor);
	        			break;
	        		case Maze.TENTATIVE : graphics2D.setPaint(tentativeColor);
	        			break;
	        		case Maze.PATH : graphics2D.setPaint(pathColor);
	        			break;
	        		default : graphics2D.setPaint(wallColor);
	        			break;
        		}
        		if (cell == Maze.PATH || cell == Maze.TENTATIVE) {
        			graphics2D.fillRect(n * myCellSize + myCellSize/4, m * myCellSize + myCellSize/4,
        					myCellSize/2, myCellSize/2);
        		} else {
	        		graphics2D.fillRect(n * myCellSize, m * myCellSize,
	        				myCellSize, myCellSize);
        		}
        	}
        }        
        
        
    }
	
}
