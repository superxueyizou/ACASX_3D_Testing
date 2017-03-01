package visualization.modeling.observer;

import visualization.modeling.uas.Proximity;

public class SimulationReturnType
{
	public Proximity miniProximity;
	public int numCollisions;
	public SimulationReturnType(Proximity miniProximity, int numCollisions)
	{
		this.miniProximity = miniProximity;
		this.numCollisions = numCollisions;
		
	}

}
