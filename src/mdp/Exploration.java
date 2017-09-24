package mdp;
import java.util.*;

import mdp.Constants.*;

public class Exploration {
	private final Map map;	// for exploration
	private final Map realMap;	//real map 
	private final Robot robot;
    private final int coverageLimit;	//??
    private final int timeLimit;	//??
    private int areaExplored;
    private long startTime;
    private long endTime;
    private int lastCalibrate;	//no of steps before calibration
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
    		moveNext(1, false);
    		
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
    private void moveNext(int count, boolean toAndroid){	//determine next move for the robot
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
    
    private void robotMove(MOVEMENT m, int count, boolean toAndroid){
    	robot.move(m, count, toAndroid);
    	/*
    	 * update gui
    	 * 
    	 */
    	
    	if(robot.getRealRobot() && !calibrationMode){
    		calibrationMode = true;
    		if(canCalibrate(robot.getRobotDir())){
    			lastCalibrate = 0;
    			robotMove(MOVEMENT.CALIBRATE,count, toAndroid);		//count doesn't matter here
    		}else{
    			lastCalibrate++;
                if (lastCalibrate >= 5) {
                    DIRECTION targetDir = calibrateTargetDirection();
                    if (targetDir != null) {
                        lastCalibrate = 0;
                        calibrateBot(targetDir);
                    	}
                }
    		}
    		calibrationMode = false; //calibrated
    	}
    }
    
    private void calibrateBot(DIRECTION targetDir) {
		DIRECTION dir = robot.getRobotDir();
		rotateRobot(targetDir);
		robotMove(MOVEMENT.CALIBRATE, 1, false);		//see if i need to change boolean
		rotateRobot(dir); //????????????
		
	}

	private void rotateRobot(DIRECTION targetDir) {
		int turns = Math.abs(robot.getRobotDir().ordinal()-targetDir.ordinal());
		if(turns>2){	//if multiple turns, decide whether to rotate left or right 
			turns = turns %2;
		}else if(turns == 2){	//rotate right twice (direction in clockwise)
			robotMove(MOVEMENT.R, 1, false);
			robotMove(MOVEMENT.R, 1, false);
		}
		
		if(turns == 1){	//after modulus
			if(DIRECTION.next(robot.getRobotDir()) == targetDir){	//if clockwise
				robotMove(MOVEMENT.R, 1, false);
			}else{
				robotMove(MOVEMENT.L, 1, false);
			}
		}else if(turns == 2){	//if turns 2, left 2 turns and right 2 turns are the same
			robotMove(MOVEMENT.R, 1, false);
			robotMove(MOVEMENT.R, 1, false);
		}
	
		
	}

	private DIRECTION calibrateTargetDirection() {	//generate target direction
		DIRECTION dir = robot.getRobotDir();
		DIRECTION newDir;
		
		newDir = DIRECTION.next(dir);
		if(canCalibrate(dir)){	//turn clockwise
			return newDir;
		}
		
		newDir = DIRECTION.prev(dir);
		if(canCalibrate(dir)){	//turn anticlockwise
			return newDir;
		}
		
		newDir = DIRECTION.next(newDir);
		if(canCalibrate(dir)){ //turn behind 180 degrees
			return newDir;
		}
		
		
		
		
		return null;
	}

	private boolean canCalibrate(DIRECTION robotDir){
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();
    	
    	switch(robotDir){ //need to change, depend on the sensor direction
    	case N: return notObstacleVirtualWall(x,y); // add more later
    	case E: return notObstacleVirtualWall(x,y);
    	case S: return notObstacleVirtualWall(x,y);
    	case W: return notObstacleVirtualWall(x,y);
    	}
    	
    	return false;
    	
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
    	//**********************************need to change********************************
    	if(!robot.getReachedGoal() && coverageLimit == 300 && timeLimit == 3600){// need to change
    		FastestPath toGoal = new FastestPath(map,robot ,realMap);
    		//toGoal.runFastestPath(Constants.GOAL_X, Constants.GOAL_Y);
    	}
    	
    	FastestPath toStart = new FastestPath(map, robot, realMap);
    	//toStart.runFastestPath(Constants.START_X, Constants.START_Y);
    	//exploration complete if return to starting position
    	
    	//print out exploration report
    	System.out.printf("%.2f%% Coverage", (areaExplored / 300.0) * 100.0);
        System.out.println(", " + areaExplored + " Cells");
        System.out.println((System.currentTimeMillis() - startTime) / 1000 + " Seconds");

        /*?????????????????????????????????????????????
        if (robot.getRealBot()) {
            turnBotDirection(DIRECTION.WEST);
            moveBot(MOVEMENT.CALIBRATE);
            turnBotDirection(DIRECTION.SOUTH);
            moveBot(MOVEMENT.CALIBRATE);
            turnBotDirection(DIRECTION.WEST);
            moveBot(MOVEMENT.CALIBRATE);
        }
        turnBotDirection(DIRECTION.NORTH);
        */
    }
    
    //trying gui
    private void paintAfterSense(){
    	robot.setSentors();
    	robot.senseDist(map, realMap);
    	map.repaint();
    }
    
    private void moveRobot(MOVEMENT m){
    	robot.move(m, 1, false); 		//for the time being
    	map.repaint();
    	if(m!= MOVEMENT.CALIBRATE){
    		paintAfterSense();
    	}else{
    		//set commMgr
    	}
    	if(robot.getRealRobot() && !calibrationMode){
    		calibrationMode = true;
    		
    		if(canCalibrate(robot.getRobotDir())){
    			lastCalibrate = 0;
    			moveRobot(MOVEMENT.CALIBRATE);    		
    		}else{
    			lastCalibrate++;
    			if(lastCalibrate>=5){
    				DIRECTION target = calibrateTargetDirection();
    				if(target != null){
    					lastCalibrate = 0;
    					calibrateBot(target);
    				}
    				
    			}
    		}
    		calibrationMode = false;
    	}
    }

    
}
