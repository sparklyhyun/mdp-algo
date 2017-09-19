package mdp;

public class Sensor {
	private int minRange;
	private int maxRange;
	private int x;	//x coordinate of sensor
	private int y;	//y coordinate of sensor
	private String id;
	public enum DIRECTION{N,S,E,W};	
	
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
	
}
