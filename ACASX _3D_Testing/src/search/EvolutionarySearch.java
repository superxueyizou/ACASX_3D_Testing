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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import visualization.configuration.Configuration;
import visualization.configuration.EncounterConfig;
import visualization.modeling.SAAModelWithUI;
import ec.EvolutionState;
import ec.Evolve;
import ec.Individual;
import ec.util.Output;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author xueyi
 * simulation with GA as harness
 *
 */
public class EvolutionarySearch
{	
	public static String problemName="MaxAccidentRate2";
	public static File parameterFile= new File("./src/search/"+problemName+".params");	
	protected static List<String> simDataSet = new ArrayList<String>(200);
	
	public static void goSearch() throws Exception
	{	
		ParameterDatabase dBase= new ParameterDatabase(parameterFile, new String[]{"-file", parameterFile.getCanonicalPath()});	
		ParameterDatabase child = new ParameterDatabase();
		child.addParent(dBase);		
		long startTime = System.currentTimeMillis();
		EvolutionState evaluatedState=GASearch(child);	
		long endTime = System.currentTimeMillis();
		System.out.println("Total search time: "+ (endTime-startTime)/1000+"s");	

		recur(evaluatedState);		
	}
	
	public static void goSearch(long rdmSeed) throws Exception
	{	
		ParameterDatabase dBase= new ParameterDatabase(parameterFile, new String[]{"-file", parameterFile.getCanonicalPath()});	
		ParameterDatabase child = new ParameterDatabase();
		child.addParent(dBase);	
		child.set(new Parameter("seed.0"), rdmSeed+"");
		long startTime = System.currentTimeMillis();
		GASearch(child);	
		long endTime = System.currentTimeMillis();
		System.out.println("Total search time for "+rdmSeed+" is: "+ (endTime-startTime)/1000+"s");	
	
	}
		
	
	public static EvolutionState GASearch(ParameterDatabase child)
	{
		long seed0 = child.getLong(new Parameter("seed.0"), null);
		EvolutionState evaluatedState=null;
		Output out = Evolve.buildOutput();					
		out.getLog(0).silent=false;//stdout
		out.getLog(1).silent=false;//stderr		
		evaluatedState= Evolve.initialize(child, 0, out);
		evaluatedState.startFresh();
		int result=EvolutionState.R_NOTDONE;	
		
		String title = "Generation,SelfGs,SelfVy,CPAT,CPAR,CPATheta,CPAY,CPAGs,CPABearing,CPAVy,Fitness,AccidentRate"+"\n";
		boolean isAppending = false;
		String logFileName = "./DataSet/Experiment2/ES/2000-30/"+problemName+"_ES_" +seed0+ "_Dataset.csv";
		
		int i=0;		
		while(result == EvolutionState.R_NOTDONE)
		{
			result=evaluatedState.evolve();
			evaluatedState.output.println("Generation "+i +" finished :)", 0);
			if(simDataSet.size()>=200)
			{  		
				UTILS.writeDataSet2CSV(logFileName, title, simDataSet,isAppending);
				isAppending =true;
				simDataSet.clear();
			}
			++i;
		}
		UTILS.writeDataSet2CSV(logFileName, title, simDataSet,isAppending);
		simDataSet.clear();
		return evaluatedState;
	}
	
	public static void recur(EvolutionState evaluatedState)
	{
		Object[] options= new Object[]{"Recurrence","Close"};
		int confirmationResult = JOptionPane.showOptionDialog(null, "choose the next step", "What's next", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE, null, options, 0);
		
		if (confirmationResult == 0 )
		{
			Individual[] inds = evaluatedState.population.subpops[0].individuals; 
		    evaluatedState.output.println("\nRecurrenceWithGUI. There are "+inds.length + " individuals",0);	    
		    Evolve.cleanup(evaluatedState);	
		    SAAModelWithUI.recur(inds);
		}		
		else
		{
			Evolve.cleanup(evaluatedState);	
		}
	}
	

	public static void main(String[] args) throws Exception
	{
//		goSearch();
		
		//324185792, 54896327, 567672542, 588764357, 884185771, 175369824

//		long[] seeds = new long[]{324185792, 54896327};
//		long[] seeds = new long[]{567672542, 588764357};
		long[] seeds = new long[]{884185771};

		for (long seed:seeds)
		{
			goSearch(seed);
			
		}
	}


	public static void genomeString2Config(String genomeString)
	{
		genomeString = genomeString.trim();
		String[] pArr= genomeString.split("\\s+|,");
		
		Configuration config = Configuration.getInstance();
		
		config.ownshipConfig.ownshipGs = Double.parseDouble(pArr[0]);
		config.ownshipConfig.ownshipVy = Double.parseDouble(pArr[1]);

				
		config.encountersConfig.clear();
	
		EncounterConfig encounterConfig = new EncounterConfig();
		encounterConfig.CPAT= Double.parseDouble(pArr[2]);

		encounterConfig.CPAR= Double.parseDouble(pArr[3]);
		encounterConfig.CPATheta= Double.parseDouble(pArr[4]);
		encounterConfig.CPAY= Double.parseDouble(pArr[5]);		

		encounterConfig.CPAGs= Double.parseDouble(pArr[6]);
		encounterConfig.CPABearing= Double.parseDouble(pArr[7]);
		encounterConfig.CPAVy= Double.parseDouble(pArr[8]);

		config.encountersConfig.put("intruder"+1, encounterConfig); 
	}
			
}
