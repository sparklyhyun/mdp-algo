package mdp;

public class Exploration {
	private final Map map;	// for exploration
	private final Map realMap;	//real map 
	private final Robot robot;
    private final int coverageLimit;	//??
    private final int timeLimit;	//??
    private int areaExplored;
    private long startTime;
    private long endTime;
    private int lastCalibrate;	//??
    private boolean calibrationMode;	//??
	
    public Exploration(Map map, Map realMap, Robot robot, int coverageLimit, int timeLimit ){
    	this.map = map;
    	this.realMap = realMap;
    	this.robot = robot;
    	this.coverageLimit = coverageLimit;
    	this.timeLimit = timeLimit;
    };
    
    public void startExploration(){
    	
    }
    
    private void explore(int x, int y){
    	//loop unless robot is back to its original position || area explored > coverage limit
    	// || System.currentTimeMills() > endTime
    	while(areaExplored <= coverageLimit && System.currentTimeMillis() <= endTime){
    		moveNext();
    		
    		areaExplored = getAreaExplored();
    		
    		//??????????????
    		if(robot.getRobotPosX() == x && robot.getRobotPosY() == y ){
    			if(areaExplored >= 100){
    				break;
    			}
    		}
    	}
    	
    	returnToStartPos();
    	
    }
    
    //THE MAIN PART****************************************************************************
    private void moveNext(){	//determine next move for the robot
    	if(rightFree()){
    		//move right
    		if(frontFree()){
    			//move forward
    		}
    	}
    	else if(leftFree()){
    		//move left
    	}
    	else if(frontFree()){
    		//move forward
    	}else{
    		//move right
    		//move right??
    	}
    }
    
    private boolean rightFree(){	//look right
    	switch(robot.getRobotDir()){
    	case N: return isEastFree();
    	case E: return isSouthFree();
    	case S: return isWestFree();
    	case W: return isNorthFree();
    	default: return false;
    	}
    }
    
    private boolean leftFree(){		//look left
    	switch(robot.getRobotDir()){
    	case N:	return isWestFree();
    	case E:	return isNorthFree();
    	case S:	return isEastFree();
    	case W: return isSouthFree();
    	default: return false;
    	}
    }
    
    private boolean frontFree(){		// look in front
    	switch(robot.getRobotDir()){
    	case N:	return isNorthFree();
    	case E: return isEastFree();
    	case S:	return isSouthFree();
    	case W: return isWestFree();
    	default: return false;
    	}
    }
    
    

    
    private boolean isEastFree(){	//true if can move to east
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();
    	return (notObstacleVirtualWall(x+1,y+1) && notObstacleVirtualWall(x+1,y) && notObstacleVirtualWall(x+1, y-1));

    }
    
    private boolean isWestFree(){
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();
    	return(notObstacleVirtualWall(x-1,y+1) && notObstacleVirtualWall(x-1, y) && notObstacleVirtualWall(x-1, y-1));
    }
    
    private boolean isSouthFree(){
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();
    	return(notObstacleVirtualWall(x-1, y-1) && notObstacleVirtualWall(x, y-1) && notObstacleVirtualWall(x+1, y-1));
    }
    
    private boolean isNorthFree(){
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();
    	return(notObstacleVirtualWall(x+1, y-1) && notObstacleVirtualWall(x+1, y) && notObstacleVirtualWall(x+1, y+1));
    }
    
    private boolean notObstacleVirtualWall(int x, int y){
    	if(map.checkWithinRange(x, y)){
    		if(!map.isObstacle(x,y) && !map.isVirtualWall(x, y) && map.isExplored(x,y)){
    			return true;
    		}
    	}
		return false;
    }
    
    private int getAreaExplored(){
    	int total = 0;
    	for(int i=0; i<Constants.MAX_X ; i++){
    		for(int j=0; j<Constants.MAX_Y; j++ ){
    			if(map.getCoordinate(i, j).getIsExplored()){
    				total++;
    			}
    		}
    	}
    	return total;
    }
    
    
    
    private void returnToStartPos(){
    	//need fastest path algo!!
    }

    
}
