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
		//System.out.println("findAndSetObstacle entered");
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
	
	private void sensorValFindObstacles(Map exploredMap, int sensorValue, int xInc, int yInc){
		//System.out.println("sensorvalfindobs entered");
		int sensorVal;
		//int sensorVal = sensorValue/10;	//scaling to one digit number
		
		int remainder = sensorValue % 10;
		if(remainder < 5){
			sensorVal = sensorValue/10;
		}else{
			sensorVal = sensorValue/10 + 1;
		}
		 
		
		
		if(sensorVal == 0) {
			System.out.println("sensor too close");
			return; //obstacle too close to sensor
		}
		
		for(int i=this.minRange; i<=this.maxRange; i++){
			int x = this.x + (xInc * i);
			int y = this.y + (yInc * i);
			
			if(!exploredMap.checkWithinRange(x, y)){ 
				//System.out.println("testing1");
				return;	//seeing outside maze
			}
			
			exploredMap.getCoordinate(x, y).setExplored();		//now seen by the sensor
			
			
			if(this.id == "a" || this.id == "b" || this.id == "c" ){
				exploredMap.getCoordinate(x, y).setFrontS();
				//System.out.println("coordinate locked: " + x + ", " + y);
			}
			
			
			
			//System.out.println("testing2");
			if(sensorVal == i){		//obstacle detected by the real sensor
				//exploredMap.setObstacles(x, y);
				
				
				if(!exploredMap.getCoordinate(x, y).getFrontS()){
					exploredMap.setObstacles(x, y);
					//System.out.println("sensor " + this.id + "position: " + this.x + ", " + this.y);
					//System.out.println("senfor " + this.id + "direction: " + this.dir);
					//System.out.println("coordinate: " + x + "," + y + " triggered by sensor: " + this.id + ", " + "sensor value: " + sensorValue);
				}else{
					if(this.id == "a" || this.id == "b" || this.id == "c"){
						exploredMap.setObstacles(x, y);
					}
				}				
				
				
				/*
				if(this.id == "a" || this.id == "b" || this.id == "c"){
					exploredMap.getCoordinate(x, y).setObstacle();
				}
				*/
				//System.out.println("sensor " + this.id + "position: " + this.x + ", " + this.y);
				//System.out.println("senfor " + this.id + "direction: " + this.dir);
				//System.out.println("coordinate: " + x + "," + y + " triggered by sensor: " + this.id + ", " + "sensor value: " + sensorValue);
				
				return;
			}
			
			//if obstacle is set but not correct, remove
			if(exploredMap.getCoordinate(x, y).getIsObstacle() && i != sensorVal /*&& this.id != "b"*/){
				if(/*this.id == "d"*/ this.id == "a" || this.id == "b" || this.id == "c" ){
					/*
					if(!exploredMap.getCoordinate(x, y).getFrontS()){
						exploredMap.getCoordinate(x, y).removeObstacle();
					}*/
					
					exploredMap.getCoordinate(x, y).removeObstacle();
				}
				
				/*else{
					if(exploredMap.getCoordinate(x, y).getFrontS()){
						return;
					}*/
					
					
					
					//exploredMap.getCoordinate(x, y).removeObstacle();
					System.out.println("coordinate: " + x + "," + y + " removed by sensor: " + this.id + ", " + "sensor value: " + sensorValue);
				}
				
			}
			
		}
		
		
		//update map according to sensor value 
		/*
		for(int i=this.minRange; i<=this.maxRange; i++){
			int x = this.x + (xInc * i);
			int y = this.y + (yInc * i);
			//System.out.println("testing3");
			if(!exploredMap.checkWithinRange(x, y)){ 
				System.out.println("testing outside maze");
				continue;	//seeing outside maze
			}
			exploredMap.getCoordinate(x, y).setExplored();		//now seen by the sensor		
			
			if(sensorVal == i){		//obstacle detected by the real sensor
				exploredMap.setObstacles(x, y);
				break;
			}			
		}*/
			
			// Override previous obstacle value if front sensors detect no obstacle.
			/*
			if(exploredMap.getCoordinate(x, y).getIsObstacle()){
				if (id.equals("topLF_S") || id.equals("topMF_S") || id.equals("topRF_S")) {
                    exploredMap.setObstacleCell(row, col, false);
                } else {
                    break;
			}*/
			
		

				


		
	}

