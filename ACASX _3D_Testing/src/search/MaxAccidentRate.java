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
/**
 * 
 */
package search;

import visualization.configuration.Configuration;
import visualization.configuration.GlobalConfig;
import visualization.modeling.SAAModel;
import visualization.modeling.SimInitializer;
import visualization.modeling.observer.AccidentDetector;
import visualization.modeling.observer.SimulationReturnType;
import visualization.modeling.uas.Proximity;
import visualization.modeling.uas.UAS;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.DoubleVectorIndividual;

/**
 * @author Xueyi Zou
 *
 */
public class MaxAccidentRate extends Problem implements SimpleProblemForm 
{
	private static final long serialVersionUID = 1L;

	@Override
	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) 
	{
		if (ind.evaluated) return;

        if (!(ind instanceof DoubleVectorIndividual))
            state.output.fatal("Whoa!  It's not a DoubleVectorIndividual!!!",null);        
      
        DoubleVectorIndividual ind2 = (DoubleVectorIndividual)ind;
        double[] genes = ind2.genome; 
   
        int times =100;   	
        double totalFitness= 0;	
        int numAccidents=0;
		
		long seed = 785945568;
		SAAModel simState= new SAAModel(seed, false); 
		SimInitializer.generateSimulation(simState, genes);
		if(!isProper(simState))
        {
			((SimpleFitness)ind2.fitness).setFitness(   state,            
		            0,/// ...the fitness...
		            false);///... is the individual ideal?  Indicate here...

			EvolutionarySearch.simDataSet.add(Configuration.getInstance().toString()+0+","+0.0);	
	        ind2.evaluated = true;
        	return;
        }
		
		for(int t=0;t<times; t++)
        { 
			SimulationReturnType result = sim(seed, genes);
			totalFitness += 10000.0/(1.0+result.miniProximity.toValue());
			numAccidents += result.numCollisions;
    		seed++;
        }
		
		float fitness= (float) (totalFitness/times);    
		double accidentRate = numAccidents*1.0/times;
		EvolutionarySearch.simDataSet.add(Configuration.getInstance().toString()+fitness+","+accidentRate);	
        
        if (!(ind2.fitness instanceof SimpleFitness))
            state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);
        
        ((SimpleFitness)ind2.fitness).setFitness(   state,            
										            fitness,/// ...the fitness...
										            false);///... is the individual ideal?  Indicate here...
        
        ind2.evaluated = true;

	}
	

	public static SimulationReturnType sim(long seed, double[] genes)
	{
		SAAModel simState= new SAAModel(seed, false); 
		if(genes!=null)
		{
			SimInitializer.generateSimulation(simState, genes);
		}
		else
		{
			SimInitializer.generateSimulation(simState);
		}
    	
		simState.start();	
		do
		{
			if (!simState.schedule.step(simState))
			{
				break;
			}
		} while(simState.schedule.getSteps()<= 60);	
 		
		Proximity miniProximity = ((UAS)simState.uasBag.get(0)).getMinProximity();	
		int numCollisions = ((AccidentDetector)simState.observerBag.get(1)).getNumCollisions();
		simState.finish();
		return new SimulationReturnType(miniProximity, numCollisions);
	}
	
	public static boolean isProper(SAAModel simState)
	{			
		UAS ownship = (UAS)simState.uasBag.get(0);
		UAS uas;
	    for(int i=1; i<simState.uasBag.size(); ++i)// i=0 to exclude ownship
		{		    	
			uas= (UAS)simState.uasBag.get(i);							
			if(ownship.getLoc().distanceSq(uas.getLoc())<GlobalConfig.PROPERDISTANCE*GlobalConfig.PROPERDISTANCE)
			{
				return false;
			}		
		}	    
	       
		return true;
	}

}
