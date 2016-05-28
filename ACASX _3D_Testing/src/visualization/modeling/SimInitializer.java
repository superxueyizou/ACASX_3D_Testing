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
package visualization.modeling;

import java.util.HashMap;

import visualization.configuration.Configuration;
import visualization.configuration.EncounterConfig;
import visualization.modeling.encountergenerator.EncounterGenerationFactory;
import visualization.modeling.encountergenerator.OwnshipGenerator;
import visualization.modeling.observer.CollisionDetector;
import visualization.modeling.observer.ProximityMeasurer;
import visualization.modeling.observer.RAMonitor;
import visualization.modeling.uas.UAS;
/**
 *
 * @author Xueyi Zou
 * This class is used to build/initialize the simulation.
 * Called for by SAAModelWithUI class
 */
public class SimInitializer
{	
	public static Configuration config= Configuration.getInstance();
	
	public static void generateSimulation(SAAModel state)
	{	
		UAS ownship = new OwnshipGenerator(state,"ownship",config.ownshipConfig.ownshipVy, config.ownshipConfig.ownshipGs).execute();
		state.uasBag.add(ownship);
		
		for(String intruderAlias: config.encountersConfig.keySet())
		{
			UAS intruder=EncounterGenerationFactory.generateIntruder(state, ownship, intruderAlias,config.encountersConfig.get(intruderAlias));
			state.uasBag.add(intruder);
		}		

	    ProximityMeasurer pMeasurer= new ProximityMeasurer();
	    CollisionDetector aDetector= new CollisionDetector();
	    RAMonitor raMonitor = new RAMonitor();
	    state.observerBag.add(pMeasurer);
	    state.observerBag.add(aDetector);// index is 1
	    state.observerBag.add(raMonitor);// index is 2			
	}
	
	
	public static void generateSimulation(SAAModel state, double[] genes, boolean configGlobal)
	{	
		Genes2Config(genes, configGlobal);
		generateSimulation(state);
	}
	
	public static void Genes2Config(double[] genes, boolean configGlobal)
	{
		config.ownshipConfig.ownshipVy = genes[0];
		config.ownshipConfig.ownshipGs = genes[1];
				
		HashMap<String,EncounterConfig> encountersConfig = config.encountersConfig;
		encountersConfig.clear();

		EncounterConfig encounterConfig = new EncounterConfig();
		encounterConfig.CAPY= genes[2];
		encounterConfig.CAPR= genes[3];
		encounterConfig.CAPTheta= genes[4];
		encounterConfig.CAPVy= genes[5];
		encounterConfig.CAPGs= genes[6];
		encounterConfig.CAPBearing= genes[7];
		encounterConfig.CAPT= genes[8];
		encountersConfig.put("intruder"+1, encounterConfig);
		
		if(configGlobal)
		{
			config.globalConfig.stdDevX = genes[9];
			config.globalConfig.stdDevY = genes[10];
			config.globalConfig.stdDevZ = genes[11];
		}
		else
		{
			config.globalConfig.stdDevX = 3;
			config.globalConfig.stdDevY = 3;
			config.globalConfig.stdDevZ = 3;
		}
    	
	}
	
}
