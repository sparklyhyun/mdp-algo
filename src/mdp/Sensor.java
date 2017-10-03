package mdp;
import mdp.Constants.*;

public class Sensor {
	private int minRange;
	private int maxRange;
	private int x;	//x coordinate of sensor
	private int y;	//y coordinate of sensor
	private String id;

	
	private DIRECTION dir = DIRECTION.N;	// can change later
	
	
	public Sensor(int minRange, int maxRange, int x, int y, String id, DIRECTION dir){
		this.minRange = minRange;
		this.maxRange = maxRange;
		this.x = x;
		this.y = y;
		this.id = id;
		this.dir = dir;
	}
	
	public void setSensor(int x, int y, DIRECTION dir){
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
	
	public int distanceToObstacle(Map exploredMap, Map realMap){
		switch(dir){
		case N:	return (findObstacle(exploredMap, realMap, 0, 1));	// see forward
		case E:	return (findObstacle(exploredMap, realMap, 1, 0));	// see right
		case S: return (findObstacle(exploredMap, realMap, 0, -1));	// see south
		case W: return (findObstacle(exploredMap, realMap, -1, 0));	// see left
		default: return -1;
		}
	}
	
	private int findObstacle(Map exploredMap, Map realMap, int xInc, int yInc){
		for(int i=this.minRange; i<=this.maxRange; i++){
			int x = this.x + (xInc * i);
			int y = this.y + (yInc * i);
			
			if(!exploredMap.checkWithinRange(x, y)){ 
				return i;	//seeing outside maze
			}
			exploredMap.getCoordinate(x, y).setExplored();		//now seen by the sensor		
			
			if(realMap.getCoordinate(x,y).getIsObstacle()){
				exploredMap.getCoordinate(x, y).setObstacle();	//obstacle detected
				return i;
			}
		}		
		return -1;
	}
	
	public void findAndSetObstacleOnMap(Map exploredMap, int sensorVal ){
		
		switch(dir){
		case N: 
			//System.out.println(sensorVal);
			sensorValFindObstacles(exploredMap, sensorVal, 0, 1); break; //see forward
		case E: 
			//System.out.println(sensorVal);
			sensorValFindObstacles(exploredMap, sensorVal, 1, 0); break; //see right
		case S: 
			//System.out.println(sensorVal);
			sensorValFindObstacles(exploredMap, sensorVal, 0, -1); break; //see south
		case W: 
			//System.out.println(sensorVal);
			sensorValFindObstacles(exploredMap, sensorVal, -1, 0); break; //see left
		}
		
	}
	
	private void sensorValFindObstacles(Map exploredMap, int sensorVal, int xInc, int yInc){
		if(sensorVal == 0) {
			//System.out.println("sensor too close");
			return; //obstacle too close to sensor
		}
		
		for(int i=this.minRange; i<=this.maxRange; i++){
			int x = this.x + (xInc * i);
			int y = this.y + (yInc * i);
			
			if(!exploredMap.checkWithinRange(x, y)){ 
				//System.out.println("testing");
				return;	//seeing outside maze
			}
			exploredMap.getCoordinate(x, y).setExplored();		//now seen by the sensor		
			//System.out.println("testing");
			
			
			
			if(sensorVal == i){		//obstacle detected by the real sensor
				exploredMap.setObstacles(x, y);
				return;
			}
			
			
		}
		
		/*
		for(int i=this.minRange; i<=this.maxRange; i++){
			int x = this.x + (xInc * i);
			int y = this.y + (yInc * i);
			
			if(!exploredMap.checkWithinRange(x, y)){ 
				continue;	//seeing outside maze
			}
			exploredMap.getCoordinate(x, y).setExplored();		//now seen by the sensor		
			
			if(sensorVal == i){		//obstacle detected by the real sensor
				exploredMap.setObstacles(x, y);
				break;
			}
			
			if(exploredMap.getCoordinate(x, y).getIsObstacle()){
				
			}
			*/
			

				


		
	}
	
}
