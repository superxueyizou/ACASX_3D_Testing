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
import visualization.configuration.GlobalConfig;
import visualization.modeling.SAAModel;
import visualization.modeling.SimInitializer;
import visualization.modeling.uas.UAS;
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
	
	static long seed0 = dBase.getLong(new Parameter("seed.0"), null);
	
	static double minSelfVy = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.0"), null);
	static double maxSelfVy = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.0"), null);
	static double minSelfGs = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.1"), null);
	static double maxSelfGs = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.1"), null);
	
	static double minCAPY = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.2"), null);
	static double maxCAPY = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.2"), null);
	static double minCAPR = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.3"), null);
	static double maxCAPR = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.3"), null);
	static double minCAPTheta = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.4"), null);
	static double maxCAPTheta = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.4"), null);
	static double minCAPVy = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.5"), null);
	static double maxCAPVy = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.5"), null);
	static double minCAPGs = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.6"), null);
	static double maxCAPGs = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.6"), null);
	static double minCAPBearing = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.7"), null);
	static double maxCAPBearing = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.7"), null);
	static double minCAPT = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.8"), null);
	static double maxCAPT = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.8"), null);
	
	static double minStdX = 3;
	static double maxStdX = 3;
	static double minStdY = 3;
	static double maxStdY = 3;
	static double minStdZ = 3;
	static double maxStdZ = 3;
	
	static int TIMES =100; 
	
	public static void searchMaxAccident() 
	{			
		int numSamplePoints=dBase.getInt(new Parameter("pop.subpop.0.size"), null)*dBase.getInt(new Parameter("generations"), null);
		
		List<String> simDataSet = new ArrayList<>(200);
		String csvFileName = "MaxAccident_RDM_" +seed0+ "_Dataset.csv";
		String title = null;//"SelfVy,SelfGs,CAPY,CAPR,CAPTheta,CAPVy,CAPGS,CAPBearing,CAPT,stdX, stdY, stdZ"+"\n";
		boolean isAppending = false;
	
		int sampleCount=0;
		long startTime = System.currentTimeMillis();		
		do
		{				 	
	        double totalCost= 0;	
	        generateRandomConfig(seed0++, false);	
	        long seed = 785945568;
			SAAModel simState= new SAAModel(seed, false); 
			SimInitializer.generateSimulation(simState);
			if(!MaxAccident.isProper(simState))
	        {
	        	continue;
	        }
			
			for(int t=0;t<TIMES; t++)
	        { 
				totalCost += 10000.0/(1.0+MaxAccident.sim(seed, null, false));
	    		seed++;
	        }		
			
			double aveCost=totalCost/TIMES;	
						
        	simDataSet.add(Configuration.getInstance().toString()+aveCost);			
			if(simDataSet.size()>=200)
			{  	
				
				UTILS.writeDataSet2CSV(csvFileName, title, simDataSet,isAppending);
				isAppending =true;
				simDataSet.clear();
			}
			++sampleCount;
			
		} while(sampleCount<numSamplePoints);
		
		long endTime = System.currentTimeMillis();
		System.out.println("Random search finished, total search time: "+ (endTime-startTime)/1000+"s");
	}
	
	public static void searchMaxGap()
	{	
		minStdX = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.9"), null);
		maxStdX = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.9"), null);
		minStdY = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.10"), null);
		maxStdY = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.10"), null);
		minStdZ = dBase.getDouble(new Parameter("pop.subpop.0.species.min-gene.11"), null);
		maxStdZ = dBase.getDouble(new Parameter("pop.subpop.0.species.max-gene.11"), null);
		
		int numSamplePoints=dBase.getInt(new Parameter("pop.subpop.0.size"), null)*dBase.getInt(new Parameter("generations"), null);
	
		List<String> simDataSet = new ArrayList<>(200);
		String csvFileName = "MaxGap_RDM_" +seed0+ "_Dataset.csv";
		String title = null;//"SelfVy,SelfGs,CAPY,CAPR,CAPTheta,CAPVy,CAPGS,CAPBearing,CAPT,stdX, stdY, stdZ"+"\n";
		boolean isAppending = false;
	
		int sampleCount=0;
		long startTime = System.currentTimeMillis();		
		do
		{	
			double[] totalCost= new double[]{0.0, 0.0};	
	        boolean configGlobal=true;
			for(int i=0; i<2; ++i)
			{
				configGlobal=!configGlobal;
				generateRandomConfig(seed0++, configGlobal);
				long seed = 785945568;
				SAAModel simState= new SAAModel(seed, false); 
				SimInitializer.generateSimulation(simState);
				if(!MaxAccident.isProper(simState))
		        {
					continue;
		        }
				
				for(int t=0;t<TIMES; t++)
		        { 
						
					double cost=MaxGap.sim(seed, null, configGlobal);
					totalCost[i]+=cost;         
		    		seed++;
		        }
				
			}
			
			double aveGap= Math.abs(totalCost[1]-totalCost[0])/TIMES;;	
						
        	simDataSet.add(Configuration.getInstance().toString()+aveGap);			
			if(simDataSet.size()>=200)
			{  				
				UTILS.writeDataSet2CSV(csvFileName, title, simDataSet,isAppending);
				isAppending =true;
				simDataSet.clear();
			}
			++sampleCount;
			
		} while(sampleCount<numSamplePoints);
		
		long endTime = System.currentTimeMillis();
		System.out.println("Random search finished, total search time: "+ (endTime-startTime)/1000+"s");
	}
	
	public static void main(String[] args)
	{
		if(EvolutionarySearch.problemName=="MaxAccident")
		{
			searchMaxAccident();
		}
		else if(EvolutionarySearch.problemName=="MaxGap")
		{
			searchMaxGap();
		}
		else
		{
			System.err.println("Unknown search problem name!");
		}
	}
	
	public static void generateRandomConfig(long seed, boolean configGlobal)
	{
		MersenneTwisterFast rdn = new MersenneTwisterFast(seed);
		
		Configuration config = Configuration.getInstance();
		
		config.ownshipConfig.ownshipVy = minSelfVy +rdn.nextDouble(true, true)*(maxSelfVy-minSelfVy);
		config.ownshipConfig.ownshipGs = minSelfGs +rdn.nextDouble(true, true)*(maxSelfGs-minSelfGs);
				
		config.encountersConfig.clear();
	
		EncounterConfig encounterConfig = new EncounterConfig();
		encounterConfig.CAPY= minCAPY +rdn.nextDouble(true, true)*(maxCAPY-minCAPY);
		encounterConfig.CAPR= minCAPR +rdn.nextDouble(true, true)*(maxCAPR-minCAPR);
		encounterConfig.CAPTheta= minCAPTheta +rdn.nextDouble(true, true)*(maxCAPTheta-minCAPTheta);
		encounterConfig.CAPVy= minCAPVy +rdn.nextDouble(true, true)*(maxCAPVy-minCAPVy);
		encounterConfig.CAPGs= minCAPGs +rdn.nextDouble(true, true)*(maxCAPGs-minCAPGs);
		encounterConfig.CAPBearing= minCAPBearing +rdn.nextDouble(true, true)*(maxCAPBearing-minCAPBearing);
		encounterConfig.CAPT= minCAPT +rdn.nextDouble(true, true)*(maxCAPT-minCAPT);
		config.encountersConfig.put("intruder"+1, encounterConfig); 
		
		if(configGlobal)
		{
			config.globalConfig.stdDevX = minStdX +rdn.nextDouble(true, true)*(maxStdX-minStdX);
			config.globalConfig.stdDevY = minStdY +rdn.nextDouble(true, true)*(maxStdY-minStdY);
			config.globalConfig.stdDevZ = minStdZ +rdn.nextDouble(true, true)*(maxStdZ-minStdZ);
		}
		else
		{
			config.globalConfig.stdDevX = 3;
			config.globalConfig.stdDevY = 3;
			config.globalConfig.stdDevZ = 3;
		}
	}	


}
