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
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.InitialGuess;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.SimpleBounds;
import org.apache.commons.math3.optimization.direct.BOBYQAOptimizer;

import visualization.configuration.Configuration;
import visualization.configuration.EncounterConfig;
import visualization.modeling.SAAModel;
import visualization.modeling.SimInitializer;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class LocalSearch {	
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
		
	static final int DIM = 9;
	
    public static void searchMaxAccident(long seed0) {
    	MultivariateFunction func = new AccidentRateEvaluator(seed0);
        double[] startPoint = getRandomPoint(seed0);
        double[][] boundaries = getBoundaries();
   
        System.out.println(func.getClass().getName() + " BEGIN");  
        long startTime = System.currentTimeMillis();	
        final double[] lB = boundaries[0];
        final double[] uB = boundaries[1];
        final int numIterpolationPoints = 2 * DIM + 1;
        int maxEvaluations = 2000; //Maximum number of evaluations.
        
        BOBYQAOptimizer optim = new BOBYQAOptimizer(numIterpolationPoints);
        PointValuePair result = optim.optimize(maxEvaluations, func, GoalType.MAXIMIZE, new InitialGuess(startPoint),new SimpleBounds(lB, uB));
            
        System.out.println(func.getClass().getName() + " = "+ optim.getEvaluations() + " f(");
        for (double x: result.getPoint())  
        	System.out.print(x + " ");
        
        System.out.println(") = " +  result.getValue());
 
		long endTime = System.currentTimeMillis();
		System.out.println("Local search "+seed0+" finished, total search time: "+ (endTime-startTime)/1000+"s\n");

    }
     
        
    private static double[] getRandomPoint(long seed0) {
        double[] ds = new double[DIM];       
        MersenneTwisterFast rdn = new MersenneTwisterFast(seed0);
        
		ds[0] = minSelfVy +rdn.nextDouble(true, true)*(maxSelfVy-minSelfVy);
		ds[1] = minSelfGs +rdn.nextDouble(true, true)*(maxSelfGs-minSelfGs);
		ds[2]= minCAPY +rdn.nextDouble(true, true)*(maxCAPY-minCAPY);
		ds[3]= minCAPR +rdn.nextDouble(true, true)*(maxCAPR-minCAPR);
		ds[4]= minCAPTheta +rdn.nextDouble(true, true)*(maxCAPTheta-minCAPTheta);
		ds[5]= minCAPVy +rdn.nextDouble(true, true)*(maxCAPVy-minCAPVy);
		ds[6]= minCAPGs +rdn.nextDouble(true, true)*(maxCAPGs-minCAPGs);
		ds[7]= minCAPBearing +rdn.nextDouble(true, true)*(maxCAPBearing-minCAPBearing);
		ds[8]= minCAPT +rdn.nextDouble(true, true)*(maxCAPT-minCAPT);

		return ds;
    }

    private static double[][] getBoundaries() {
        double[][] boundaries = new double[][]{
        {minSelfVy, minSelfGs, minCAPY, minCAPR, minCAPTheta, minCAPVy, minCAPGs, minCAPBearing, minCAPT},
        {maxSelfVy, maxSelfGs, maxCAPY, maxCAPR, maxCAPTheta, maxCAPVy, maxCAPGs, maxCAPBearing, maxCAPT} };
        return boundaries;
    }
	


	public static void main(String[] args)
	{
		long[] seeds = new long[]{567672542, 898946497, 679463479,884185791, 588764257};//
		for (long seed:seeds)
		{
			searchMaxAccident(seed);
		}	
	}	
	
}
