/*
 * This is the Class that call the TAGrid class and runs simulations as a batch
 * for the TA model 
 */

import java.io.*;

public class TAStaticBatch {

    double typesSum[][] = new double[50][7];
    double typesSqrSum[][] = new double[50][7];
	double typesProlifSum[][] = new double[50][7];
    double typesProlifSqrSum[][] = new double[50][7];
	double stasisSum[][] = new double[50][7];
    double stasisSqrSum[][] = new double[50][7];
	double stainSum[][] = new double[50][7];
    double stainSqrSum[][] = new double[50][7];
	double retainSum[][] = new double[50][7];
    double retainSqrSum[][] = new double[50][7];
	double proliferationSum[] = new double[50];
	double proliferationSqrSum[] = new double[50];
	
	int replicates =20; // 20 replicates for each experiment
	
	static String fileprefix = "test";
	
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
		double frac = (double)((exp+1)/100.0); // calculate fraction of SC
		double  avProliferations;
		double avTypes;
		double avTypeProliferations;
		double avStains;
		int proliferations=0;
		int [] typeProliferations = new int[7];
		int [] types = new int[7];
		double [] stains = new double[7];
		TAGridStatic experiment = new TAGridStatic(64, 4, frac);
		for(int i=0; i<100; i++){
			if(i==89)experiment.stain();// stain all cells 10 iterations before end
			experiment.iterate();
		}
		//now, after 100 iterations:
		for (TACell c : experiment.tissue){
			types[c.type]++; // count types
			stains[c.type]+=c.stain; // calculate stain
		}
		//beth? another iteration?
		experiment.iterate();
		for (TACell c : experiment.tissue){
			if(c.proliferated){
				// count proliferations in final iteration
				proliferations++;
				//beth? why are the type 3 counted with the type 2?
				if(c.type<3){
					typeProliferations[c.type]++;// count the type as it was in the iteration before the final iteration
				}else{
					typeProliferations[c.type-1]++;// as above
				}
			}
		}
		avProliferations = (double) proliferations/(64.0*64.0);// Calculate average proliferations
		proliferationSum[exp]+=avProliferations; // add average to the array of experiments
		proliferationSqrSum[exp]+=(avProliferations*avProliferations); // add standard deviations to the array of experiments
 		for(int i=0; i<6; i++){
			if(types[i]>0){
				avTypeProliferations = (double)(typeProliferations[i])/(types[i]*1.0);
			}else{
				avTypeProliferations=0.0;
			}
			avTypes = (double)(types[i])/(64.0*64.0);
			avStains = (double)(stains[i])/(64.0*64.0);
			// again add results to the array of experiments
			stainSum[exp][i]+=avStains;
			stainSqrSum[exp][i]+=(avStains*avStains);
			
			typesSum[exp][i]+=avTypes;
			typesSqrSum[exp][i]+=(avTypes*avTypes);
			
			typesProlifSum[exp][i]+=avTypeProliferations;
			typesProlifSqrSum[exp][i]+=(avTypeProliferations*avTypeProliferations);
		}
	}
	
	public void setOfRuns(){
		for(int i=0; i<50; i++){// 50 sets of  experiments
			System.out.print(i);
			for(int r=0; r<replicates; r++){
				iterate(i,r); 
				System.out.print("["+r+"]");
			}
			System.out.println();
		}
		outputData();
	}
	
		
	public void outputData(){
		// Loop through all of the array of experiments and print them out 
		try{
			//creates the data files for the experiments
			BufferedWriter bufProlif = new BufferedWriter(new FileWriter(fileprefix+"Proliferation.txt"));
			BufferedWriter bufTypes = new BufferedWriter(new FileWriter(fileprefix+"Types.txt"));
			BufferedWriter bufTypeProlif = new BufferedWriter(new FileWriter(fileprefix+"TypeProliferation.txt"));
			BufferedWriter bufStain = new BufferedWriter(new FileWriter(fileprefix+"StainProliferation.txt"));
			String[] headers = {"Space","SC","TA1","TA2","TA3","TA4","TA5"};
			double frac=0.0;
			bufProlif.write("SCFraction Av_proliferations stdev");
			bufProlif.newLine();
			for(int i=0; i< 50; i++){
				frac = (double)((i+1)/100.0);
				bufProlif.write(frac+" "+getMean(proliferationSum[i])+" "+getStandardDeviation(proliferationSum[i], proliferationSqrSum[i])+" ");
				bufProlif.newLine();	
			}
			bufProlif.newLine();
			bufProlif.newLine();
			bufProlif.close();
			for(int i=0; i<6; i++){
				bufTypes.write("SCFraction "+headers[i]+" sdtev");
				bufTypes.newLine();	
				bufTypeProlif.write("SCFraction "+headers[i]+" sdtev");
				bufTypeProlif.newLine();	
				bufStain.write("SCFraction "+headers[i]+" sdtev");
				bufStain.newLine();	
				for(int j=0; j< 50; j++){
					frac = (double)((j+1)/100.0);
					bufTypes.write(frac+" "+getMean(typesSum[j][i])+" "+getStandardDeviation(typesSum[j][i],typesSqrSum[j][i])+" ");
					bufTypes.newLine();	
					bufTypeProlif.write(frac+" "+getMean(typesProlifSum[j][i])+" "+getStandardDeviation(typesProlifSum[j][i],typesProlifSqrSum[j][i])+" ");
					bufTypeProlif.newLine();	
					bufStain.write(frac+" "+getMean(stainSum[j][i])+" "+getStandardDeviation(stainSum[j][i],stainSqrSum[j][i])+" ");
					bufStain.newLine();	
				}
				bufTypes.newLine();	
				bufTypes.newLine();
				bufTypeProlif.newLine();	
				bufTypeProlif.newLine();
				bufStain.newLine();	
				bufStain.newLine();
			}
			bufTypes.close();
			bufTypeProlif.close();
			bufStain.close();
		}catch(IOException e){
		}
	}


	
	public static void main (String args[]) {		
		TAStaticBatch t = new TAStaticBatch();
		// the argument is the directory and the first part of the data name
		if(args.length>0){
			fileprefix = args[0];
		}
		t.setOfRuns();
	}
}
