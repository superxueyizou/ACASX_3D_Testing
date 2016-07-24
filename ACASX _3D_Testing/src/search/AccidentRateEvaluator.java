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

import org.apache.commons.math3.analysis.MultivariateFunction;

import visualization.configuration.Configuration;
import visualization.configuration.EncounterConfig;
import visualization.modeling.SAAModel;
import visualization.modeling.SimInitializer;

public class AccidentRateEvaluator implements MultivariateFunction{
		
	private int TIMES =100; 
	private long seed0 =785945568;
	
	public AccidentRateEvaluator(long seed0)
	{
		this.seed0=seed0;
	}
	
	public double value(double[] x) 
	{       
        int numAccident= 0;	
        generateConfig(x);	
        long seed = seed0;
		SAAModel simState= new SAAModel(seed, false); 
		SimInitializer.generateSimulation(simState);
		if(!MaxAccidentRate.isProper(simState))
        {
        	return 0;
        }
		
		for(int t=0;t<TIMES; t++)
        { 
			numAccident += MaxAccidentRate.sim(seed, null).numCollisions;
    		seed++;
        }		
		
		return numAccident*1.0/TIMES;	
    }
	
	private void generateConfig(double[] x)
	{		
		Configuration config = Configuration.getInstance();
		
		config.ownshipConfig.ownshipVy = x[0];
		config.ownshipConfig.ownshipGs = x[1];
				
		config.encountersConfig.clear();
	
		EncounterConfig encounterConfig = new EncounterConfig();
		encounterConfig.CAPY= x[2];
		encounterConfig.CAPR= x[3];
		encounterConfig.CAPTheta= x[4];
		encounterConfig.CAPVy= x[5];
		encounterConfig.CAPGs= x[6];
		encounterConfig.CAPBearing= x[7];
		encounterConfig.CAPT= x[8];
		config.encountersConfig.put("intruder"+1, encounterConfig); 
	}

	public static void main(String[] args)
	{
		int seed0 = Integer.parseInt(args[0]);
		System.out.println("seed: "+seed0);
		AccidentRateEvaluator ar= new AccidentRateEvaluator(seed0);
//		System.out.println(ar.value(new double[]{-20.989741163217072, 272.2465710598757,3.7742079335627325, 451.48856734110234,146.77909700384535, -62.281706466173446,220.86904622326824, 173.84717774373235, 21.141048729227403})+"    expected: "+36.19540322910341);	
	}	
}
