/*	Tabitha Stein 	*
 *	Maze Generator	*/

package view;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A panel from which the user can select options for generating a new Maze.
 * @author Tabitha Stein
 * @version 1.0
 */
public class MazeCustomizationPanel extends JPanel {

	private final static int INIT_DIM = 5;
	private final static int MAX_DIM = 30;
	public final static String KRUSKAL = "Kruskal's Algorithm";
	public final static String PRIM = "Prim's Algorithm";
	
	/**Holds the latest selection on Maze width.*/
	private int mazeWid;
	
	/**Holds the latest selection on Maze height.*/
	private int mazeHei;
	
	/**Holds the latest selection on the algorithm to use in building maze.*/
	private String mazeAlg;
	
	/**
	 * Constructions a new MazeCustomizationPanel and calls for it to be set up.
	 */
	public MazeCustomizationPanel() {
		setup();
	}
	
	private void setup() {
		setLayout(new GridBagLayout());
		
		mazeWid = INIT_DIM;
		mazeHei = INIT_DIM;	
		mazeAlg = KRUSKAL;

		
		JLabel dimensionsLabel = new JLabel("Dimensions:");
		JPanel dimPanel = setUpDimensionPanel();		
		GridBagConstraints dimC = new GridBagConstraints();
		dimC.gridwidth = 2;
		dimC.gridy = 0;
		add(dimensionsLabel, dimC);		
		GridBagConstraints dimPanelC = new GridBagConstraints();
		dimPanelC.gridwidth = 2;
		dimPanelC.gridy = 1;
		add(dimPanel, dimPanelC);
		
		JPanel algPanel = setUpAlgPanel();
		GridBagConstraints algC = new GridBagConstraints();
		algC.gridwidth = 2;
		algC.gridy = 2;
		add(algPanel, algC);		
	}
	
	/**
	 * Sets up the panel with a combo box for selecting the algorithm for building
	 * the maze.
	 * @return The panel is returned.
	 */
	private JPanel setUpAlgPanel() {
		JPanel algPanel = new JPanel();
		JLabel algLabel = new JLabel("Select Build Algorithm:");
		algPanel.setLayout(new GridLayout(2, 2));
		String[] algStrings = {KRUSKAL, PRIM};
		JComboBox<String> algList = new JComboBox<String>(algStrings);
		algList.setSelectedIndex(0);
		algList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mazeAlg = (String)algList.getSelectedItem();
			}
			
		});
		algPanel.add(algLabel);
		algPanel.add(algList);
		return algPanel;
	}
	
	/**
	 * Sets up the panel with spinners for customizing width and height of Maze.
	 * @return The panel is returned.
	 */
	private JPanel setUpDimensionPanel() {
		JPanel dimPanel = new JPanel();
		dimPanel.setLayout(new GridLayout(1, 4));
		
		JLabel widLab = new JLabel("Width:");
		SpinnerNumberModel widMod = new SpinnerNumberModel(INIT_DIM, 1, MAX_DIM, 1);		
		JLabel heiLab = new JLabel("Height:");
		SpinnerNumberModel heiMod = new SpinnerNumberModel(INIT_DIM, 1, MAX_DIM, 1);
		
		//Set up change listeners
		JSpinner width = new JSpinner(widMod);
		width.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				mazeWid = (int) widMod.getNumber();
			}
		});
		
		JSpinner height = new JSpinner(heiMod);
		height.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				mazeHei = (int) heiMod.getNumber();
			}
		});
		
		dimPanel.add(widLab);
		dimPanel.add(width);
		dimPanel.add(heiLab);
		dimPanel.add(height);
		
		return dimPanel;
	}
	
	public int getMazeWid() {
		return mazeWid;
	}
	
	public int getMazehei() {
		return mazeHei;
	}
	
	public String getMazeAlg() {
		return mazeAlg;
	}
	
	
}
