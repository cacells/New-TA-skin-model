/*
 * This is the main part of the simulation of the TA version of the model 
 */

import java.util.*;
 
public class TAGridStatic {

	public ArrayList <TACell> tissue;// List of cells that make up the tissue
	private Random rand = new Random();
	
	public TAGridStatic(int size, int maxC, double frac) {//beth: this is the constructor
		// Create new instance of simulation with size of grid maximum TA cycle and fraction of stem cells 
	    TACell.maxCycle = maxC+1;// (see TACell)
		TABoxStatic[][] grid = new TABoxStatic[size][size];
        //beth: matrix of dimensions sizexsize containing homes, and called 'grid'
		// Temporary 2D array to hold boxes in Cartesian grid so that connections can be made
		TACell cell;//just a name to use for each cell as it is placed in a home in the grid
		tissue = new ArrayList<TACell>();// Creates the list structure for the cells that constitute the tissue
		int lineage =0;
		// Grid looped through and new TABox and TACell created for each element in the array
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
			    grid[x][y] = new TABoxStatic(x,y);// New instance of TABox created and added to 2D grid
				cell = new TACell(grid[x][y],lineage);// New instance of TACell created and given unique lineage id
				grid[x][y].occupant = cell;// The new cell is added to the TABox
				cell.type=rand.nextInt(maxC)+2;// Cell type set randomly to either TA_1,TA_2,TA_3 or TA_4
				tissue.add(cell);// Add new cell to list of cells that constitute the tissue
				lineage++;
			}
		}
		 // Calculate the number of SC required for this grid size for a given fraction
		int sc = (int)(64*64*frac);//beth: 64 hard coded.could use size
		TACell c;//beth: could use 'cell' here instead of 'c'?
		while(sc>0){ // while not enough stem cells allocated
			c = tissue.get(rand.nextInt(tissue.size())); // Pick a cell at random from tissue list
			if (c.type!=1){ // If that cell is not already a stem cell type 1  
				c.type=1;// Chance to an SC
				sc--;
			}
		}
		//beth: at this point there are no holes (type 0 cells).
		for (int x = 0; x < size; x++) { //  Loop through all the boxes in the grid 
			for (int y = 0; y < size; y++) {
		        for (int xx = x - 1; xx <= x + 1; xx++) {
			        for (int yy = y - 1; yy <= y + 1; yy++) {
						if((y!=yy)||(x!=xx)) // Form links with their 8 immediate neighbours
			            grid[x][y].addNeighbour(grid[bounds(xx,size)][bounds(yy,size)]);
						//This maintains the cartesian relationship between each of the boxes without having to maintain the array
			        }
			    }
			}
	    } 
	}//beth: end of constructor

	private int bounds(int a,int size) {  // Creates the toroidal links between top and bottom and left and right
		if (a < 0) return size + a;
		if (a >= size) return a - size;
		return a;
	}
	
	public void stain(){ // Stains all cells in the tissue list
		for (TACell c : tissue) {
		    if(c.type>0){
				c.stain=1.0;
			}
	    }
	}

	public void iterate() { // The main iterative loop of the simulation
		//beth: 
        TACell cHold;
		int numberOfGrowingCells =0;//beth: not used
		// Create a list to hold cells that are spaces or have the capacity to detach
        ArrayList<TACell> growArray = new ArrayList<TACell>();
        for (TACell c : tissue) { // loop through the tissue (ArrayList of cells)
		    c.maintain(); // Calls each cell to maintain its state re: detach and/or grow
			if(c.type==0)growArray.add(c); // If cell is a space add to grow list
			if(c.canDetach)growArray.add(c);// If cell can detach add to grow list
	    }
        //beth: go through the list and see if anything grows into those spots
		while(growArray.size()>0){ // Randomly loop through the grow list
			cHold=growArray.remove(rand.nextInt(growArray.size()));
			cHold.grow();// Test to see if cell can be replaced by new proliferation
		}
	}

}
