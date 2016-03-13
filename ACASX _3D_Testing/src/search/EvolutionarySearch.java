/**
 * 
 */
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
import ec.util.ParameterDatabase;

/**
 * @author xueyi
 * simulation with GA as harness
 *
 */
public class EvolutionarySearch
{	
	public static String problemName="MaxGap";//"MaxGap" , "MaxAccident"
	public static File parameterFile= new File("./src/search/"+problemName+".params");	
	protected static List<String> simDataSet = new ArrayList<>(200);
	
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
		
	
	public static EvolutionState GASearch(ParameterDatabase child)
	{
		EvolutionState evaluatedState=null;
		Output out = Evolve.buildOutput();					
		out.getLog(0).silent=false;//stdout
		out.getLog(1).silent=false;//stderr		
		evaluatedState= Evolve.initialize(child, 0, out);
		evaluatedState.startFresh();
		int result=EvolutionState.R_NOTDONE;	
		
		String title = null;//"SelfVy,SelfGs,CAPY,CAPR,CAPTheta,CAPVy,CAPGS,CAPBearing,CAPT,stdX, stdY, stdZ"+"\n";
		boolean isAppending = false;
		
		int i=0;		
		while(result == EvolutionState.R_NOTDONE)
		{
			result=evaluatedState.evolve();
			evaluatedState.output.println("Generation "+i +" finished :)", 0);
			if(simDataSet.size()>=200)
			{  				
				UTILS.writeDataSet2CSV(problemName+"_ES_" + "Dataset.csv", title, simDataSet,isAppending);
				isAppending =true;
				simDataSet.clear();
			}
			++i;
		}
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
		goSearch();
	}


	public static void genomeString2Config(String genomeString)
	{
		genomeString = genomeString.trim();
		String[] pArr= genomeString.split("\\s+|,");
		int len = pArr.length;
		for(String s:pArr)
		{
			System.out.println(s);
		}
		
		Configuration config = Configuration.getInstance();
		
		config.ownshipConfig.ownshipVy = Double.parseDouble(pArr[0]);
		config.ownshipConfig.ownshipGs = Double.parseDouble(pArr[1]);
				
		config.encountersConfig.clear();
	
		EncounterConfig encounterConfig = new EncounterConfig();
		encounterConfig.CAPY= Double.parseDouble(pArr[2]);
		encounterConfig.CAPR= Double.parseDouble(pArr[3]);
		encounterConfig.CAPTheta= Double.parseDouble(pArr[4]);
		encounterConfig.CAPVy= Double.parseDouble(pArr[5]);
		encounterConfig.CAPGs= Double.parseDouble(pArr[6]);
		encounterConfig.CAPBearing= Double.parseDouble(pArr[7]);
		encounterConfig.CAPT= Double.parseDouble(pArr[8]);
		config.encountersConfig.put("intruder"+1, encounterConfig); 
		
		if(len>9)
		{
			config.globalConfig.stdDevX = Double.parseDouble(pArr[9]);
			config.globalConfig.stdDevY = Double.parseDouble(pArr[10]);
			config.globalConfig.stdDevZ = Double.parseDouble(pArr[11]);
		}
		else
		{
			config.globalConfig.stdDevX = 3;
			config.globalConfig.stdDevY = 3;
			config.globalConfig.stdDevZ = 3;
		}
	}
			
}
