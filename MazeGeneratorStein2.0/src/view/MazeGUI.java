/*	Tabitha Stein 	*
 *	Maze Generator	*/

package view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Maze;

public class MazeGUI implements Observer {
	
	private static final String DEPTH_FIRST = "Depth-First Search";
	private static final String BREADTH_FIRST = "Breadth-First Search";
	private static final int FPS_MIN = 10;
	private static final int FPS_MAX = 500;
	private static final int FPS_INIT = 350;
	
	/**List holding each phase in Maze generation.*/
	private List<char[][]> mazeArrs;
	
	/**List holding each phase in solving Maze.*/
	private List<char[][]> pathArrs;
	
	/**The current Maze.*/
	private Maze myMaze;
	
	/**The main container.*/
	private JFrame myFrame;
	
	/**The content pane for myFrame.*/
	private JPanel contentPane;	
	
	/**The panel for displaying the Maze.*/
	private MazePanel myPanel;
	
	/**The timer currently running to display steps in generating or solving the Maze.*/
	private Timer myStepTimer;
	
	/**A button to open a Maze customation dialog.*/
	private JButton myCreateNewButton;

	/**The Dialog for customizing a new Maze.*/
	private JDialog myCustomizeDialog;
	
	/**A Panel for holding buttons on solving algorithms.*/
	private JPanel mySolveSelectPanel;
	
	/**A Panel for holding the Frames Per Second slider*/
	private JPanel mySliderPanel;
	
	/**A Slider for selecting Frames Per Second of Maze Generation/Solving.*/
	private JSlider mySlider;


	/**
	 * Constructs a new MazeGUI, calling for each component to be set up.
	 */
	public MazeGUI() {
		myFrame = new JFrame("Maze Generator");
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = new JPanel();
		contentPane.setLayout(new GridBagLayout());
		
		setUpNewButton();
		setupSolveSelect();
		setUpSlider();
		introMaze();
		setupMenu();
		
		GridBagConstraints panelC = new GridBagConstraints();
		panelC.anchor = GridBagConstraints.CENTER;
		panelC.gridwidth = 2;
		panelC.gridy = 0;
		contentPane.add(myPanel, panelC);
		
		setupBuildTimer();
		
		JPanel myBuildPanel = new JPanel();
		myBuildPanel.setLayout(new GridLayout(1, 2));
		myBuildPanel.add(myCreateNewButton);
		myBuildPanel.add(mySolveSelectPanel);	
		
		
		GridBagConstraints buildC = new GridBagConstraints();
		buildC.anchor = GridBagConstraints.CENTER;
		buildC.gridwidth = 2;
		buildC.gridy = 1;
		contentPane.add(myBuildPanel, buildC);
				
		myFrame.setContentPane(contentPane);		
		myFrame.setResizable(false);
		myFrame.pack();
		myFrame.setLocationRelativeTo(null);
		myFrame.setVisible(true);
		
        myStepTimer.start();
	}
	
	/**
	 * Sets up the Maze generated when application starts.
	 */
	private void introMaze() {
		mazeArrs = new LinkedList<char[][]>();
		pathArrs = new LinkedList<char[][]>();
		myMaze = new Maze(15, 15);
		myMaze.addObserver(this);
		myMaze.prim();
		myPanel = new MazePanel(mazeArrs.get(0), 10);
	}
	
	/**
	 * Builds a new Maze using the given dimensions and algorithm.
	 */
	private void newMaze(int rows, int columns, String alg) {
		myStepTimer.stop();
		myMaze.deleteObserver(this);
		mazeArrs 	= new LinkedList<char[][]>();
		pathArrs 	= new LinkedList<char[][]>();
		myMaze 		= new Maze(rows, columns);
		myMaze.addObserver(this);
		switch (alg) {
			case MazeCustomizationPanel.PRIM 	: 	myMaze.prim();
													break;
			default								: 	myMaze.kruskal();
													break;
		}
		//myMaze.prim();
		myPanel.setDisplayArr(mazeArrs.get(0));
		myPanel.setOverlayArr(new char[mazeArrs.get(0).length][mazeArrs.get(0)[0].length]);
		myFrame.pack();
	}
	
	/**
	 * Sets up a menu bar with Help and About menus.
	 */
	private void setupMenu() {
		JMenuBar menuBar 		= new JMenuBar();		
		final JMenu helpMenu 	= new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);        
        
        final JMenuItem help 	= new JMenuItem("Help...");
        String helpInfo 		= "Create: To create a new Maze, click Create and chose dimensions and algorithm.\n"
        						+ "Solve: To solve the Maze, select one of the \n"
        						+ "solving algorithms from menu on the right and hit 'Solve.'";      
        help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                JOptionPane.showMessageDialog(null, 
                                              helpInfo, 
                                              "Help", 
                                              JOptionPane.INFORMATION_MESSAGE);
            }            
        });        
        helpMenu.add(help);        
        
        final JMenuItem about 	= new JMenuItem("About...");
        String aboutInfo 		= "Maze Generator\n"
        						+ "Author: Tabitha Stein\n"
        						+ "Course: Data Structures - Winter 2015\n";       
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                JOptionPane.showMessageDialog(null, 
                                              aboutInfo, 
                                              "About", 
                                              JOptionPane.INFORMATION_MESSAGE);
            }            
        });        
        helpMenu.add(about);
        
        menuBar.add(helpMenu);     
		myFrame.setJMenuBar(menuBar);
	}
	
	/**
	 * Sets up the Create new maze button, which opens a dialog.
	 */
	private void setUpNewButton() {
		myCreateNewButton = new JButton("Create new Maze...");

		MazeCustomizationPanel dialogPanel = new MazeCustomizationPanel();

		myCustomizeDialog = new JDialog(myFrame, "Create New Maze", true);
		myCustomizeDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		myCustomizeDialog.add(dialogPanel);			

		JButton create = new JButton("Create");
		create.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				myStepTimer.stop();
				newMaze(dialogPanel.getMazehei(), dialogPanel.getMazeWid(), dialogPanel.getMazeAlg());
				myCustomizeDialog.dispose();
				myPanel.revalidate();
				myFrame.pack();
				setupBuildTimer();
				myStepTimer.start();
			}
			
		});	
		
		myCustomizeDialog.add(create, BorderLayout.SOUTH);
		myCustomizeDialog.pack();
		myCustomizeDialog.setResizable(false);
		myCreateNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent theEvent) {
				myCustomizeDialog.setLocationRelativeTo(myFrame);
				myCustomizeDialog.setVisible(true);
			}
		});
	}
	
	/**
	 * Sets up a JSlider for controlling the speed of the maze generation.
	 */
	private void setUpSlider() {
		mySliderPanel = new JPanel();
		
		JLabel mySliderLabel = new JLabel("Speed:");
		mySlider = new JSlider(JSlider.HORIZONTAL,
                FPS_MIN, FPS_MAX, FPS_INIT);
		mySliderPanel.add(mySliderLabel);
		mySliderPanel.add(mySlider);
		mySlider.setMajorTickSpacing(50);
		mySlider.setPaintTicks(true);
		mySlider.setInverted(true);
		
		GridBagConstraints sliderC = new GridBagConstraints();
		sliderC.anchor = GridBagConstraints.CENTER;
		sliderC.gridwidth = 2;
		sliderC.gridy = 2;
		contentPane.add(mySliderPanel, sliderC);
		
		mySlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int fps = (int) ((JSlider)e.getSource()).getValue();
				myStepTimer.setDelay(fps);
			}
			
		});
	}	
	
	/**
	 * Sets up radio buttons for selecting the algorithm to solve maze.
	 */
	private void setupSolveSelect() {
		mySolveSelectPanel = new JPanel();
		mySolveSelectPanel.setLayout(new GridLayout(3, 1));
		JRadioButton solveButtons[] = new JRadioButton[2];
		solveButtons[0] = new JRadioButton(DEPTH_FIRST);		
		solveButtons[1] = new JRadioButton(BREADTH_FIRST);		
		ButtonGroup group = new ButtonGroup();
		for (JRadioButton b : solveButtons) {
			group.add(b);
			mySolveSelectPanel.add(b);
		}
		JButton solveButton = new JButton("Solve");
		solveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (JRadioButton b : solveButtons) {
					if (b.isSelected()) {
						pathArrs = new LinkedList<char[][]>();
						myStepTimer.stop();
						myPanel.setDisplayArr(mazeArrs.get(mazeArrs.size() - 1));
						
						if (b.getText().equals(DEPTH_FIRST)) {
							myMaze.depthFirstSearch();
						} else {
							myMaze.breadthFirstSearch();
						}
						setupSolveTimer();
						myStepTimer.start();
					}
				}
			}			
		});		
		mySolveSelectPanel.add(solveButton);		
		solveButtons[0].setSelected(true);
	}
	
	/**
	 * Sets up a new timer to display the steps of the current Maze.
	 */
	private void setupBuildTimer() {
        myStepTimer = new Timer(mySlider.getValue(), new ActionListener() {     
        	int nextArr = 1;
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
            	if (nextArr < mazeArrs.size()) {
            		myPanel.setDisplayArr(mazeArrs.get(nextArr));
            		nextArr++;
                } else {
                	myStepTimer.stop();
                }
            }            
        });  
        myStepTimer.setRepeats(true);
	}
	
	/**
	 * Sets up a new timer to display the steps of the current Maze.
	 */
	private void setupSolveTimer() {
		myStepTimer = new Timer(mySlider.getValue(), new ActionListener() {       
        	
        	int nextArr = 1;

            @Override
            public void actionPerformed(final ActionEvent theEvent) {
            	if (nextArr < pathArrs.size()) {
            		myPanel.setOverlayArr(pathArrs.get(nextArr));
            		nextArr++;
                } else {
                	myStepTimer.stop();
                }
            }
            
        });   
        
        myStepTimer.setRepeats(true);
	}

	/**
	 * Updates the list of states in maze generation and solving the maze.
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof Maze) {
			Maze m = (Maze) o;
			if (!m.getBuilt()) {
				mazeArrs.add(m.getMazeArr());
			} else {
				pathArrs.add(m.getPathArr());
			}
		}
	}

}
