/**
 * 
 */
package search;

import java.util.ArrayList;

import visualization.configuration.Configuration;
import visualization.modeling.SAAModel;
import visualization.modeling.SimInitializer;
import visualization.modeling.observer.CollisionDetector;
import visualization.modeling.observer.RAMonitor;
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
public class MaxGap extends Problem implements SimpleProblemForm 
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
        double[] totalCost= new double[]{0.0, 0.0};	
        boolean configGlobal=true;
		for(int i=0; i<2; ++i)
		{
			long seed = 785945568;
			configGlobal=!configGlobal;
			for(int t=0;t<times; t++)
	        { 
				double cost=sim(seed, genes, configGlobal);
				totalCost[i]+=cost;         
	    		seed++;
	        }
			
		}
        double aveGap = Math.abs(totalCost[1]-totalCost[0])/times;
        EvolutionarySearch.simDataSet.add(Configuration.getInstance().toString()+aveGap);	
		float fitness= (float) aveGap;      
        
        if (!(ind2.fitness instanceof SimpleFitness))
            state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);
        
        ((SimpleFitness)ind2.fitness).setFitness(   state,            
										            fitness,/// ...the fitness...
										            false);///... is the individual ideal?  Indicate here...
        
        ind2.evaluated = true;

	}
	
	public static double sim(long seed, double[] genes, boolean configGlobal)
	{
		double totalCost=0;
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

		boolean hasAccident = ((CollisionDetector)simState.observerBag.get(1)).hasAccident();// index is 1
	    
		ArrayList<Character> undesiredRAs = ((RAMonitor)simState.observerBag.get(2)).ownshipCostyRAs;// index is 2	
		
		if(hasAccident) 
			totalCost +=10000;
		else 
			totalCost += 10000.0/(1+miniProximity);
		
    	for(Character c : undesiredRAs)
    	{
    		if(c=='R')
    			totalCost += 1000;
    		else
    			totalCost += 500;
    	}	
		simState.finish();
		return totalCost;
	}

}
