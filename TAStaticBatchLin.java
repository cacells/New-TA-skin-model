/*
 * This is the Class that call the TAGrid class and runs simulations as a batch
 * for the TA model that looks a just the lineage
 */

import java.io.*;

public class TAStaticBatchLin {

	double lineageSum[] = new double[100];
	double lineageSqrSum[] = new double[100];
	static String fileprefix = "test";
	
	int replicates =20;
	//20 experiments. Each experiment iterates 100 times.
	
    public double getMean(double sum) {
		 return sum / replicates;
	}
	
	public double getStandardDeviation(double sum, double sqrSum) {
        double mean = getMean(sum);
		double dev = (sqrSum / replicates) - (mean * mean);
		if(dev>0){
        	return Math.sqrt(dev);
		}
		return 0.0;
    }


	public void iterate(int exp, int rep){
		double frac = (double)((exp+1)/100.0);//fraction of stem cells
		int countLineage;
		int countClone;
		double avLineage;
		int lin=64*64;//max number of different cell lines equals cells in grid
		System.out.println(lin);
		int lineage[];
		TAGridStatic experiment = new TAGridStatic(64, 4, frac);
		for(int i=0; i<100; i++){
			lineage = new int[lin];
			countLineage =0;
			countClone =0;
			experiment.iterate();
			//cell count for each each cell line
			for (TACell c : experiment.tissue){
				lineage[c.lineage]++;
			}
			//lineage[0] is the spaces so not j=0
			for(int j=1; j<lin; j++){
				//countClone counts number of different cell lines (clones)
				if(lineage[j]>0)countClone++;
				//countLineage would be cell count
				countLineage+=lineage[j];
			}
			//total cell count div by number of clones
			avLineage = (double)countLineage/(countClone*1.0);
			//sum the average cells per clone at this iteration for all replicates
			lineageSum[i]+=avLineage;
			//and its square at this iteration for all replicates
			lineageSqrSum[i]+=(avLineage*avLineage);
		}
	}
	
	public void setOfRuns(){
		for(int r=0; r<replicates; r++){
			iterate(9,r);
			System.out.print("["+r+"]");
		}
		System.out.println();
		outputData();
	}
	
		
	public void outputData(){
		try{
			BufferedWriter bufLineage = new BufferedWriter(new FileWriter(fileprefix+"Lineage.txt"));
			double frac=0.0;
			bufLineage.write("iteration Clones stdev");
			bufLineage.newLine();
			for(int i=0; i< 100; i++){
				bufLineage.write(i+" "+getMean(lineageSum[i])+" "+getStandardDeviation(lineageSum[i], lineageSqrSum[i])+" ");
				System.out.println(i+" "+getMean(lineageSum[i])+" "+getStandardDeviation(lineageSum[i], lineageSqrSum[i])+" ");
				bufLineage.newLine();	
			}
			bufLineage.newLine();
			bufLineage.newLine();
			bufLineage.close();
		}catch(IOException e){
		}
	}


	
	public static void main (String args[]) {		
		TAStaticBatchLin t = new TAStaticBatchLin();
		if(args.length>0){
			fileprefix = args[0];
		}
		t.setOfRuns();
	}
}
