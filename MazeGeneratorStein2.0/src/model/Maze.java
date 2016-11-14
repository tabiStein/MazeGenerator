/*	Tabitha Stein 	*
 *	Maze Generator	*/

package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Random;
import java.util.Set;


/**
 * A Maze generating and solving class that uses a graph with adjacency lists,
 * Kruskals or Prim's algorithm, and Depth First or Breadth First Search. By default, the
 * entrance is at the top left and the exit is at the bottom right. Also
 * contains a bonus constructor that allows the user to specify the location of
 * the entrance and exit and to indicate how they are marked.
 * 
 * @author Tabitha Stein
 * @version 2.0
 */
public class Maze extends Observable {

	/**A 2D array holding the display of this Maze.*/
	private final char[][] mazeArr;
	
	/**A 2D array holding a path through this Maze*/
	private char[][] pathArr;
	
	/**The number of vertices in the vertical direction.*/
	private final int numRows; 
	
	/**The number of vertices in the horizontal direction.*/
	private final int numColumns;
	
	/**A list of all vertices in this Maze, ordered from the top left to the bottom right.*/
	private final ArrayList<Vertex> vertices;
	
	/**A reference to the entrance to this Maze.*/
	private final Vertex start;
	
	/**A reference to the exit to this Maze.*/
	private final Vertex exit;
	
	/**Character to represent walls in the display.*/
	public static final char WALL = 'X';
	
	/**Character to represent the path taken in the display.*/
	public static final char PATH = '•';
	
	/**Character to represent the steps taken in the path in the display.*/
	public static final char TENTATIVE = 'o';
	
	/**Character to represent vertices that have been visited by Prim's algorithm.*/
	public static final char VISITED = 'V';
	
	/**The character to mark the entrance to the maze.*/
	private final char entranceMarker;
	
	/**The character to mark the exit to the maze.*/
	private final char exitMarker;
	
	/**Generates random numbers for selecting neighbors in both Prim's (since weighting 
	 * isn't used) and DFS, and random trees in Kruskal's.*/
	private static final Random RANDY = new Random();
	
	/**When true, generation algorithms are disabled.*/
	private boolean isBuilt;
	
	/**
	 * Generates a maze of the given dimensions (m rows, n columns) using Prim's algorithm.
	 * @param m the number of rows
	 * @param n the number of columns
	 */
	public Maze(int m, int n) {
		this(m, n, 0, 0, ' ', ' ');		
	}
	
	/**
	 * An alternative constructor allowing user to specify the location where the entrance
	 * and exit are located. The value for the entrance begins at the top left corner and
	 * the value for the exit begins at the bottom right corner, both wrapping around the
	 * perimeter clockwise.
	 * Additionally, as the start and finish may be anywhere, it accepts characters for
	 * marking which is which.
	 * @param m the number of rows
	 * @param n the number of columns
	 * @param entranceLoc the location around the parameter from the top left where the
	 *        entrance is located. 
	 * @param exitLoc the location around the parameter from the bottom right where the
	 *        exit is located. 
	 * @param entMark the character for displaying the entrance
	 * @param exMark the character for displaing the exit
	 */
	public Maze(int m, int n, int entranceLoc, int exitLoc, char entMark, char exMark) {
		isBuilt = false;
		
		entranceMarker = entMark;
		exitMarker = exMark;
		numRows = Math.abs(m); //Protects against negative dimensions
		numColumns = Math.abs(n);
		mazeArr = new char[(numRows * 2) + 1][(numColumns * 2) + 1];
		for (char[] cArr : mazeArr) {
			Arrays.fill(cArr, WALL);
		}
		
		pathArr = new char[mazeArr.length][mazeArr[0].length];
				
		vertices = new ArrayList<Vertex>(numRows*numColumns);
		
		if (numRows > 0 && numColumns > 0) {
			for (int i = 0; i < numRows; i++) {
				int row = i * 2 + 1;
				for (int j = 0; j < numColumns; j++) {
					int column = j * 2 + 1;
					vertices.add(new Vertex(row, column));
					mazeArr[row][column] = ' '; // Put a space where vertices
												// are placed
				}
			}

			start = vertices.get(getDoorIndex(entranceLoc));
			clearDoorway(start, entranceMarker);

			exit = vertices.get(getDoorIndex(exitLoc + numColumns + numRows));
			clearDoorway(exit, exitMarker);

			buildLattice();
		} else { //Protects against algorithms running on empty mazes
			start = exit = null;
		}
		
	}
	
	/**Returns the index where the start or exit should be within the vertices array.*/
	private int getDoorIndex(int distance) {
		int perimeter = 2 * (numRows + numColumns);
		int side = Math.abs(distance) % perimeter;
		int index = 0;
		if (side < numColumns) { index = side; }
		else if (side < perimeter / 2) { index = ((side - numColumns) * numColumns) + numColumns - 1; }
		else if (side < perimeter - numRows) { index = vertices.size() - 1 - (side - numColumns - numRows); }
		else { index = (perimeter - (side + 1)) * numColumns; }
		return index;
	}
	
	/**Replaces the character "behind" the given Vertex with the given character, iff the
	 * Vertex is on the border of the mazeArr.*/
	private void clearDoorway(Vertex door, char marker) {
		if (door.row == 1) { //at top
			mazeArr[door.row - 1][door.column] = marker;
		} else if (door.row == mazeArr.length - 2) { //at bottom
			mazeArr[door.row + 1][door.column] = marker;
		} else if (door.column == 1) { //left side
			mazeArr[door.row][door.column-1] = marker;
		} else if (door.column == mazeArr[0].length - 2) { //right side
			mazeArr[door.row][door.column+1] = marker;
		}
	}
	
	
	/**Connects each vertex in Maze to the vertex above, below, left, and right, for each 
	 * that exists.*/
	private void buildLattice() {
		//Add neighbors to each vertex
		for (int i = 0; i < vertices.size(); i++) {
			Vertex current = vertices.get(i);
		
			int above = i - numColumns; //if it's >= 0, there is a vertex above this one		
			if (above >= 0) { current.addNeighbor(vertices.get(above)); }
			
			int below = i + numColumns; //if it's < vertices.size(), there is a vertex below this one	
			if (below < vertices.size()) { current.addNeighbor(vertices.get(below)); }
			
			int right = i + 1; //must be on same row: to check for this, test if you are 
							   //in last column right is divisible by numColumns
			if (right % numColumns != 0) { current.addNeighbor(vertices.get(right)); }
			
			int left = i - 1;  //If you can't go left, you are in the first column. You are 
							   //in the first column if i is divisible by numColumns
			if (i % numColumns != 0) { current.addNeighbor(vertices.get(left)); }
		}
		
	}
	
	/**
	 * Returns true if this Maze is finished being built; false otherwise.
	 * @return
	 */
	public boolean getBuilt() {
		return isBuilt;
	}
	
	/**
	 * My implementation of Kruskal's Algorithm using disjoint sets to represent
	 * connected subgraphs without cycles, and a hashmap that for any given
	 * vertex reports the subgraph it is a member of. A list of disjoint sets is
	 * maintained, and the sets are joined in a random order by one edge until
	 * there is only one set in the list containing all the vertices.
	 */
	public void kruskal() {
		if (!isBuilt) {
			ArrayList<HashSet<Vertex>> forest = new ArrayList<HashSet<Vertex>>(vertices.size());
			HashMap<Vertex, HashSet<Vertex>> treeMembership = new HashMap<Vertex,HashSet<Vertex>>();
			for (int i = 0; i < vertices.size(); i++) {
				HashSet<Vertex> tree_i = new HashSet<Vertex>();
				tree_i.add(vertices.get(i));
				forest.add(tree_i);
				treeMembership.put(vertices.get(i), tree_i); 						//record membership of this vertex
			}
			/*One tree in forest means the minimum spanning tree is complete*/
			while (forest.size() > 1) {				
				HashSet<Vertex> treeA = forest.get(RANDY.nextInt(forest.size()));	//random tree			
				Iterator<Vertex> treeAVertexIterator = treeA.iterator();
				boolean notJoined = true;											
				while (notJoined && treeAVertexIterator.hasNext()) {				//stop after union or no more vertices
					Vertex vertA = treeAVertexIterator.next();					
					Iterator<Vertex> neighbors_it = vertA.neighbors.iterator();		//find a neighbor not in tree A
					while (notJoined && neighbors_it.hasNext()) {
						Vertex vertB = neighbors_it.next();
						if (!treeA.contains(vertB)) { 								//adding edge will not create cycle							
							HashSet<Vertex> treeB = treeMembership.get(vertB);					
							treeA.addAll(treeB); 									//treeA = treeA UNION treeB	
							for (Vertex v : treeB) {
								treeMembership.put(v, treeA);
							}
							forest.remove(treeB);
							addEdge(vertA, vertB);				
							notJoined = false;
							setChanged();
							notifyObservers();
						}
					}
				}
			}						
			isBuilt = true;
			purgeVisitedMarkings();
		}
		
	}

	/**
	 * Uses an implementation of Prim's algorithm to build a path among neighboring 
	 * vertices by finding a minimum spanning tree, stored implicitly within each Vertex
	 * using a second adjacency list. Branches out from the starting vertex by adding an edge
	 * to a random unvisited vertex adjacent to one we've visited, called the "frontier." The
	 * frontier can contain duplicates, so there is higher probability of picking a Vertex adjacent
	 * to multiple visited vertices.
	 */
	public void prim() {
		if (!isBuilt) {
			Set<Vertex> visited = new HashSet<Vertex>(vertices.size());
			visited.add(start);
			
			while (visited.size() < vertices.size()) {				
				setChanged();
				notifyObservers();				
				
				ArrayList<Vertex> frontier = new ArrayList<Vertex>();	//All unvisited neighbors of visited Vertices (includes duplicates)
				for (Vertex v : visited) {
					for (Vertex neighbor : v.neighbors) {
						if (!visited.contains(neighbor)) {
							frontier.add(neighbor);
						}
					}
				}
				
				Vertex randV = frontier.get(RANDY.nextInt(frontier.size()));
				ArrayList<Vertex> parents = new ArrayList<Vertex>();
				for (Vertex v: randV.neighbors) {
					if (visited.contains(v)) {
						parents.add(v);
					}
				}
				
				Vertex randParent = parents.get(RANDY.nextInt(parents.size()));	//Randomly pick one of the visited parents
				addEdge(randParent, randV);
				visited.add(randV);
				mazeArr[randV.row][randV.column] = VISITED;				
			}
			setChanged();
			notifyObservers();
						
			isBuilt = true;
			purgeVisitedMarkings();
			}
	}
	

	
	/**Removes the visited markings from mazeArr.*/
	private void purgeVisitedMarkings() {
		for (int m = 0; m < mazeArr.length; m++) {
			for (int n = 0; n < mazeArr[m].length; n++) {	
				if (mazeArr[m][n] == VISITED) {
					mazeArr[m][n] = ' ';
				} else {
					mazeArr[m][n] = mazeArr[m][n];
				}
			}	
		}
	}

	/**Adds each of the given vertices to the path neighbors list of the other, while 
	 * tearing down the wall between them in the maze array.*/
	private void addEdge(Vertex a, Vertex b) {
		a.addPathTo(b);
		b.addPathTo(a);
		
		int row = (a.row + b.row) / 2;			//Wall is at row and column indices between a and b
		int column = (a.column + b.column) / 2;
		mazeArr[row][column] = ' ';				//"paves" a path between the vertices in the mazeArr

	}	

	/**
	 * Solve the Maze using depth first search, which navigates a path until
	 * reaching a dead end, then backtracks until it can try a different path.
	 * Remembers paths that didn't work and doesn't revisit them.
	 */
	public void depthFirstSearch() {	
		
		if (isBuilt) {
			pathArr = new char[mazeArr.length][mazeArr[0].length];			
			Set<Vertex> visited = new HashSet<Vertex>();
			LinkedList<Vertex> pathStack = new LinkedList<Vertex>();					//Steps from start to finish
			visited.add(start);
			pathStack.push(start);
			pathArr[start.row][start.column] = TENTATIVE;
			setChanged();
			notifyObservers();
			
			while (pathStack.peek() != exit) { 											//Navigate until the end is visited
				ArrayList<Vertex> neighbors = pathStack.peek().getPathNeighbors(); 	
				neighbors.removeAll(visited);											//Holds adjacent unvisited vertices
				if (!neighbors.isEmpty()) {
					Vertex randNeighbor = neighbors.get(RANDY.nextInt(neighbors.size()));
					visited.add(randNeighbor);
					pathStack.push(randNeighbor);
					pathArr[randNeighbor.row][randNeighbor.column] = TENTATIVE;					
					setChanged();
					notifyObservers();					
				} else {
					pathStack.pop();													//Dead end; backtrack one step
				}
			}
			while (!pathStack.isEmpty()) {
				Vertex v = pathStack.removeLast();										//Ordered exit to start, so step
				pathArr[v.row][v.column] = PATH;										//through backwards
				setChanged();
				notifyObservers();				
			}	
		}
	}
	
	/**
	 *Solves the Maze using breadth first search, finding the shortest solution. 
	 *Makes a queue of vertices to visit starting with all adjacent to the entrance.
	 *Then goes through each of those in the order enqueued, tacking their unvisited
	 *neighbors to the end of the queue. This is repeated until the exit is reached.
	 *As we go, the child-parent relationship is stored in a hashmap, where a parent is
	 *the first adjacent Vertex from which we arrived to the child. Thus the parent stored 
	 *represents the shortest path to backtrack to the start. By backtracking the parent 
	 *from the exit vertex all the way to the start vertex, we then get the shortest path.*/
	public void breadthFirstSearch() {
		if (isBuilt) {			
			pathArr = new char[mazeArr.length][mazeArr[0].length];	

			LinkedList<Vertex> levelQueue = new LinkedList<Vertex>();			//Holds unchecked, visited vertices in level-order
			
			HashMap<Vertex, Vertex> visitedFrom = new HashMap<Vertex, Vertex>();//Stores visited child-parent pairs.
			levelQueue.add(start);
			visitedFrom.put(start, null);
						
			while(!visitedFrom.containsKey(exit)) {								//Navigate by level until exit is reached
				Vertex parent = levelQueue.poll();
				pathArr[parent.row][parent.column] = TENTATIVE;
				setChanged();
				notifyObservers();
				ArrayList<Vertex> unvisitedChildren = parent.getPathNeighbors();
				unvisitedChildren.removeAll(visitedFrom.keySet());
				for (Vertex v : unvisitedChildren) {
					levelQueue.add(v);
					visitedFrom.put(v, parent);
				}
			}
			
			pathArr[exit.row][exit.column] = TENTATIVE;
			setChanged();
			notifyObservers();
			
			
			LinkedList<Vertex> pathQueue = new LinkedList<Vertex>();
			pathQueue.offer(exit);
			while(pathQueue.peekLast() != start) {
				pathQueue.offer(visitedFrom.get(pathQueue.peekLast()));
			}
			
			while (!pathQueue.isEmpty()) {
				Vertex v = pathQueue.removeLast();
				pathArr[v.row][v.column] = PATH;				
				setChanged();
				notifyObservers();
				
			}
			
		}
	}
	
	
	/**Displays the maze to console for debugging. If the maze has been solved, the solution path is printed.*/
	public void display() {
			StringBuilder sb = new StringBuilder();
			sb.append("\t");
			for (int c = 0; c < pathArr[0].length; c++) {
				sb.append(c + " "); //columns for debug
			}
			sb.append('\n');
			for (int m = 0; m < pathArr.length; m++) {
				if (m > 0) {
					sb.append('\n');
				}
				sb.append(m + ")\t"); //row for debug
				for (int n = 0; n < pathArr[m].length; n++) {	
					char cell = pathArr[m][n];
					if (cell == 0) {
						cell = mazeArr[m][n];
					}
						sb.append(cell + " ");
				}	
			}
			System.out.println(sb.toString());
			System.out.println();
	}
	
	/**
	 * Returns a representation of the Maze as a 2D array. If it is being built, {@value #VISITED}
	 * is used to mark a cell where a wall has been knocked down to form a path.
	 */
	public char[][] getMazeArr() {
		char[][] copy = new char[mazeArr.length][mazeArr[0].length];
		for (int m = 0; m < mazeArr.length; m++) {
			for (int n = 0; n < mazeArr[m].length; n++) {	
				copy[m][n] = mazeArr[m][n];
			}	
		}
		return copy;
	}
	
	/** 
	 * Returns a representation of the path through this Maze as a 2D array. 
	 * The definitive steps in the path are marked by {@value #PATH}, while
	 * steps being tested out are marked by {@value #TENTATIVE}.
	 */
	public char[][] getPathArr() {
		char[][] copy = new char[pathArr.length][pathArr[0].length];
		for (int m = 0; m < pathArr.length; m++) {
			for (int n = 0; n < pathArr[m].length; n++) {
				copy[m][n] = pathArr[m][n];
			}
		}
		return copy;		
	}


	/**
	 * Represents a graph vertex using adjacency lists. It has two adjacency lists: the
	 * first represents its neighbors at the start of the Maze when it is just a lattice.
	 * The second represents its neighbors in the minimal spanning tree found by one of the
	 * selected algorithms.
	 */
	private class Vertex {

		/**A list of adjacent vertices*/
		private final ArrayList<Vertex> neighbors;

		/**A list of adjacent vertices who share a path with this one*/
		private final ArrayList<Vertex> pathNeighbors;

		/**The row of this Maze where this Vertex is located.*/
		final int row;
		
		/**The column of this Maze where this Vertex is located.*/
		final int column;

		private Vertex(int r, int c) {
			row = r;
			column = c;
			neighbors = new ArrayList<Vertex>();
			pathNeighbors = new ArrayList<Vertex>();
		}

		/**Add a Vertex to this Vertex's adjacency list*/
		void addNeighbor(Vertex v) {
			if (!neighbors.contains(v)) { 
				neighbors.add(v);
			}
		}

		/**Adds the given vertex to this vertex's list of neighbors not divided by a wall.
		 *if the given vertex is not in this vertex's adjacency list, it will not be added.*/
		void addPathTo(Vertex v) {
			if (neighbors.contains(v) && !pathNeighbors.contains(v)) {
				pathNeighbors.add(v);
			}
		}

		/**Returns a clone of the list of this Vertex's neighboring Vertices connected by a path.*/
		ArrayList<Vertex> getPathNeighbors() {		
			return (ArrayList<Vertex>) pathNeighbors.clone();
		}

	}


}
