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
public class MaxAccident extends Problem implements SimpleProblemForm 
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
        double totalCost= 0;	
        boolean configGlobal=false;
		
		long seed = 785945568;
		SAAModel simState= new SAAModel(seed, false); 
		SimInitializer.generateSimulation(simState, genes, configGlobal);
		if(!isProper(simState))
        {
			((SimpleFitness)ind2.fitness).setFitness(   state,            
		            0,/// ...the fitness...
		            false);///... is the individual ideal?  Indicate here...

			EvolutionarySearch.simDataSet.add(Configuration.getInstance().toString()+0);	
	        ind2.evaluated = true;
        	return;
        }
		
		for(int t=0;t<times; t++)
        { 
			totalCost += 10000.0/(1.0+sim(seed, genes, configGlobal));
    		seed++;
        }
        
		double aveCost = totalCost/times;
		
		float fitness= (float) aveCost;      
		EvolutionarySearch.simDataSet.add(Configuration.getInstance().toString()+fitness);	
        
        if (!(ind2.fitness instanceof SimpleFitness))
            state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);
        
        ((SimpleFitness)ind2.fitness).setFitness(   state,            
										            fitness,/// ...the fitness...
										            false);///... is the individual ideal?  Indicate here...
        
        ind2.evaluated = true;

	}
	
	public static double sim(long seed, double[] genes, boolean configGlobal)
	{
		SAAModel simState= new SAAModel(seed, false); 
		if(genes!=null)
		{
			SimInitializer.generateSimulation(simState, genes, configGlobal);
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
 		
		double miniProximity = ((UAS)simState.uasBag.get(0)).getMinProximity().toValue();	
		simState.finish();
		return miniProximity;
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
