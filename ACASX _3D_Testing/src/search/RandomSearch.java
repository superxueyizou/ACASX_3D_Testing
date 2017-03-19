/*******************************************************************************
 *  Copyright (C) Xueyi Zou - All Rights Reserved
 *  Written by Xueyi Zou <xz972@york.ac.uk>, 2015
 *  You are free to use/modify/distribute this file for whatever purpose!
 *  -----------------------------------------------------------------------
 *  |THIS FILE IS DISTRIBUTED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 *  |WARRANTY. THE USER WILL USE IT AT HIS/HER OWN RISK. THE ORIGINAL
 *  |AUTHORS AND COPPELIA ROBOTICS GMBH WILL NOT BE LIABLE FOR DATA LOSS,
 *  |DAMAGES, LOSS OF PROFITS OR ANY OTHER KIND OF LOSS WHILE USING OR
 *  |MISUSING THIS SOFTWARE.
 *  ------------------------------------------------------------------------
 *******************************************************************************/
package search;

import java.util.ArrayList;
import java.util.List;

import visualization.configuration.Configuration;
import visualization.configuration.EncounterConfig;
import visualization.modeling.SAAModel;
import visualization.modeling.SimInitializer;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class RandomSearch {	
	static ParameterDatabase dBase= null;
	static
	{
		try
		{			
			dBase =	new ParameterDatabase(EvolutionarySearch.parameterFile, new String[]{"-file", EvolutionarySearch.parameterFile.getCanonicalPath()});

		}
		catch(Exception e)
		{
			System.err.println("error in getting ParameterDatabase!");
			System.exit(1);
		}
	}
	
	static int numSamplePoints=dBase.getInt(new Parameter("pop.subpop.0.size"), null)*dBase.getInt(new Parameter("generations"), null);


	static double minSelfGs = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.0"), null);
	static double maxSelfGs = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.0"), null);
	static double minSelfVy = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.1"), null);
	static double maxSelfVy = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.1"), null);
	
	static double minCAPT = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.2"), null);
	static double maxCAPT = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.2"), null);	

	static double minCAPR = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.3"), null);
	static double maxCAPR = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.3"), null);
	static double minCAPTheta = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.4"), null);
	static double maxCAPTheta = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.4"), null);
	static double minCAPY = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.5"), null);
	static double maxCAPY = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.5"), null);
	

	static double minCAPGs = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.6"), null);
	static double maxCAPGs = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.6"), null);
	static double minCAPBearing = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.7"), null);
	static double maxCAPBearing = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.7"), null);
	static double minCAPVy = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.8"), null);
	static double maxCAPVy = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.8"), null);

	
	static int TIMES =100; 
	
	public static void searchMaxAccident(long seed0) 
	{			
		List<String> simDataSet = new ArrayList<String>(200);
		String csvFileName = "./DataSet/Experiment1/RDM/MaxAccident_RDM_" +seed0+ "_Dataset.csv";
		String title = "SelfGs,SelfVy,CPAT,CPAR,CPATheta,CPAY,CPAGs,CPABearing,CPAVy,AccidentRate"+"\n";
		boolean isAppending = false;
	
		int sampleCount=0;
		long startTime = System.currentTimeMillis();		
		do
		{				 	
	        int numAccidents= 0;	
	        generateRandomConfig(seed0++);	
	        long seed = 785945568;
			SAAModel simState= new SAAModel(seed, false); 
			SimInitializer.generateSimulation(simState);
			
			double accidentRate=0;
			if(!MaxAccidentRate.isProper(simState))
	        {
				accidentRate=0;
	        }
			else
			{
				for(int t=0;t<TIMES; t++)
		        { 
					numAccidents += MaxAccidentRate.sim(seed, null).numCollisions;
		    		seed++;
		        }		
				
				accidentRate=numAccidents*1.0/TIMES;	
			}
			
			
						
        	simDataSet.add(Configuration.getInstance().toString()+accidentRate);			
			if(simDataSet.size()>=200)
			{  	
				
				UTILS.writeDataSet2CSV(csvFileName, title, simDataSet,isAppending);
				isAppending =true;
				simDataSet.clear();
			}
			++sampleCount;
			
		} while(sampleCount<numSamplePoints);
		
		UTILS.writeDataSet2CSV(csvFileName, title, simDataSet,isAppending);
		simDataSet.clear();
		
		long endTime = System.currentTimeMillis();
		System.out.println("Random search "+seed0+" finished, total search time: "+ (endTime-startTime)/1000+"s");
	}
	
	public static void main(String[] args)
	{
		//324185792, 54896327, 567672542, 588764357, 884185771
		long[] seeds = new long[]{175369824};//175369824
		for (long seed:seeds)
		{
			searchMaxAccident(seed);
		}
	}
	
	public static void generateRandomConfig(long seed)
	{
		MersenneTwisterFast rdn = new MersenneTwisterFast(seed);
		
		Configuration config = Configuration.getInstance();
		
		config.ownshipConfig.ownshipGs = minSelfGs +rdn.nextDouble(true, true)*(maxSelfGs-minSelfGs);
		config.ownshipConfig.ownshipVy = minSelfVy +rdn.nextDouble(true, true)*(maxSelfVy-minSelfVy);

				
		config.encountersConfig.clear();
	
		EncounterConfig encounterConfig = new EncounterConfig();
		encounterConfig.CPAT= minCAPT +rdn.nextDouble(true, true)*(maxCAPT-minCAPT);

		encounterConfig.CPAR= minCAPR +rdn.nextDouble(true, true)*(maxCAPR-minCAPR);
		encounterConfig.CPATheta= minCAPTheta +rdn.nextDouble(true, true)*(maxCAPTheta-minCAPTheta);
		encounterConfig.CPAY= minCAPY +rdn.nextDouble(true, true)*(maxCAPY-minCAPY);

		encounterConfig.CPAGs= minCAPGs +rdn.nextDouble(true, true)*(maxCAPGs-minCAPGs);		
		encounterConfig.CPABearing= minCAPBearing +rdn.nextDouble(true, true)*(maxCAPBearing-minCAPBearing);
		encounterConfig.CPAVy= minCAPVy +rdn.nextDouble(true, true)*(maxCAPVy-minCAPVy);

		config.encountersConfig.put("intruder"+1, encounterConfig); 
	}	


}
