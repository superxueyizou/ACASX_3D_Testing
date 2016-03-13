/**
 * 
 */
package visualization.saa;

import java.util.Map;
import java.util.Map.Entry;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Double2D;
import visualization.configuration.Configuration;
import visualization.modeling.SAAModel;
import visualization.modeling.observer.RAMonitor;
import visualization.modeling.uas.UAS;
import acasx3d.ACASX3D_SingleThreat;
import acasx3d.generation.DTMC;
import acasx3d.generation.MDP;

/**
 * @author Xueyi
 *
 */
public class ACASX3DProxy implements Steppable
{	
	private static final long serialVersionUID = 1L;
	
	private static int COORDINATION_SENSE =0; //-1-->do not climb; 0-->no restriction; 1-->do not descend
	
	private SAAModel state; 
	private UAS hostUAS;
	private ACASX3D_SingleThreat acas;
	private int lastRA=0;
	
	public ACASX3DProxy(SimState simstate, UAS uas) 
	{
		state = (SAAModel) simstate;
		hostUAS = uas;	
		acas=new ACASX3D_SingleThreat();
	}	
	
	
	public void step(SimState simState)
	{
		if(hostUAS.activeState == 0)
		{	
			execute();			
		}		 
	}
	
	
	public void execute()
	{
		int numUAS=state.uasBag.size();
		double maxRisk = Double.MIN_VALUE;
		double tempRisk;
		UAS urgentestIntruder=null;
		for(int i=0; i<numUAS; i++)
		{
			UAS uas= (UAS)state.uasBag.get(i);
			if(uas == hostUAS)
			{
				continue;
			}
			else
			{
				Double2D vctDistance = new Double2D(uas.getLoc().x-hostUAS.getLoc().x, uas.getLoc().z-hostUAS.getLoc().z);
				double r=vctDistance.length();
				double h=(uas.getLoc().y-uas.getLoc().y);
		
				if(Math.abs(h)<=MDP.UPPER_H && r<=DTMC.UPPER_R)
				{
					tempRisk =calculateCollisionRisk(uas);
					if(tempRisk>maxRisk)
					{
						maxRisk = tempRisk;
						urgentestIntruder=uas;
					}
				}				
			}			
		
		}
		
		int newRA;
		if(urgentestIntruder!=null)
		{
			if(hostUAS.getAlias()=="ownship")
			{
				acas.update(hostUAS.getLoc(), hostUAS.getVel(), urgentestIntruder.getLoc(), urgentestIntruder.getVel(),lastRA, 0);	
				newRA=acas.execute();	
				if(newRA>0 && Configuration.getInstance().globalConfig.coordinationEnabler)
				{
					if(newRA%2==0)
						COORDINATION_SENSE = 1;
					else
						COORDINATION_SENSE =-1;						
				}
				else
				{
					COORDINATION_SENSE =0;
				}	
				
				if( (lastRA==1||lastRA==3)&& newRA==5
						|| (lastRA==2||lastRA==4)&& newRA==6)
				{//strengthening
					((RAMonitor)state.observerBag.get(2)).ownshipCostyRAs.add('S');
				}
				
				if( (lastRA==1 || lastRA==3 || lastRA==5)&& newRA==4 
						|| (lastRA==2 || lastRA==4 || lastRA==6)&& newRA==3 )
				{//reversal
					((RAMonitor)state.observerBag.get(2)).ownshipCostyRAs.add('R');
				}
			}
			else
			{
				acas.update(hostUAS.getLoc(), hostUAS.getVel(), urgentestIntruder.getLoc(), urgentestIntruder.getVel(),lastRA, COORDINATION_SENSE);	
				newRA=acas.execute();	
			}					

		}
		else
		{
			newRA=-1;
		}
		hostUAS.getAp().setActionCode(newRA);
		lastRA= (newRA==-1? 0: newRA);
		
	}

	public double calculateCollisionRisk(UAS uas)
	{
		Map<Integer, Double> entryTimeDistribution=acas.calculateEntryTimeDistributionDTMC(hostUAS.getLoc(), hostUAS.getVel(), uas.getLoc(), uas.getVel());
		double aveTime=0;
		for(Entry<Integer, Double> entry :entryTimeDistribution.entrySet())
		{	
			aveTime+=entry.getKey()*entry.getValue();
		}
		return 1.0/(1+aveTime);
	}


	public double getActionA(int actionCode) {
		return acas.getActionA(actionCode);
	}


	public double getActionV(int actionCode) {
		return acas.getActionV(actionCode);
	}
	
	
}
