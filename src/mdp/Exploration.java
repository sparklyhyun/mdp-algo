package mdp;
import java.io.IOException;
import java.util.*;

import mdp.Constants.*;


public class Exploration {
	public final Map map;	// for exploration
	private final Map realMap;	//real map 
	private final Robot robot;
    private final int coverageLimit;	
    public int timeLimit;	
    private int areaExplored;
    private long startTime;
    private long endTime;
    private int lastCalibrate;	//no of steps before calibration
    private boolean calibrationMode;	
    private boolean checkptRightTurn = false;
    private int explorationMode = 0; //0=normal, 1=coverage, 2=time 
    public int robotDelay;
    private int[][] previousCoord;
    private boolean expStarted = false;
    //private boolean waypointMode = true;
	
    public Exploration(Map map, Map realMap, Robot robot, int coverageLimit, int timeLimit, int explorationMode, int robotDelay ){
    	this.map = map;
    	this.realMap = realMap;
    	this.robot = robot;
    	this.coverageLimit = coverageLimit;
    	this.timeLimit = timeLimit;
    	this.explorationMode = explorationMode;
    	if(robotDelay == 0){
    		this.robotDelay = 100;
    	}else{
    		this.robotDelay = robotDelay;
    	}
    };
    
    public void startExploration() throws IOException{
    	if(robot.getRealRobot()){	
    		System.out.println("Starting calibration");
            CommunicationMgr.getCommMgr().recvMsg();
            
    		if(robot.getRealRobot()){
    			
    			robot.move(MOVEMENT.L,1, false);
                 CommunicationMgr.getCommMgr().recvMsg();
                 robot.move(MOVEMENT.CALIBRATE,1, false);
                 CommunicationMgr.getCommMgr().recvMsg();
                 robot.move(MOVEMENT.L,1, false);
                 CommunicationMgr.getCommMgr().recvMsg();
                 robot.move(MOVEMENT.CALIBRATE,1, false);
                 CommunicationMgr.getCommMgr().recvMsg();
                 robot.move(MOVEMENT.R,1, false);
                 CommunicationMgr.getCommMgr().recvMsg();
                 robot.move(MOVEMENT.CALIBRATE,1, false);
                 CommunicationMgr.getCommMgr().recvMsg();
                 robot.move(MOVEMENT.R,1, false);
                 
    		}
    		
    		
    		while(true){
        		//print out communication message
    			 System.out.println("Waiting for EX_START");
                 String msg = CommunicationMgr.getCommMgr().recvMsg();
                 String[] msgArr = msg.split(";");
                 if (msgArr[0].equals(CommunicationMgr.EX_START)) break;
    		}
    		
    	
    		
    	}
    	 System.out.println("Starting exploration...");

         startTime = System.currentTimeMillis();
         endTime = startTime + (timeLimit * 1000);
    	//start, rotate the robot
    	
    	if(!expStarted){
    		moveRobot(Constants.MOVEMENT.R);
        	moveRobot(Constants.MOVEMENT.R);
        	moveRobot(Constants.MOVEMENT.R);
        	moveRobot(Constants.MOVEMENT.R);
        	expStarted = false;
    	}
    	
    	
    	map.readMapDesc();
    	if(explorationMode == 0){
    		
    		if(robot.getRealRobot()){
    			CommunicationMgr.getCommMgr().sendMsg(null, CommunicationMgr.BOT_START);
    			//send map data
    			CommunicationMgr.getCommMgr().sendMsg(null, CommunicationMgr.MAP_STRINGS);
        	}
        	
    		System.out.println("explore");
    		explore(robot.getRobotPosX(), robot.getRobotPosY());
        	paintAfterSense();
        	
        	//print out area calculated??
    	}else if(explorationMode == 1){   
    		
    		if(robot.getRealRobot()){
    			CommunicationMgr.getCommMgr().sendMsg(null, CommunicationMgr.BOT_START);
        	}
    		
    		
    		
    		exploreCL(robot.getRobotPosX(), robot.getRobotPosY());	//coverage limited
        	paintAfterSense();
    	}else if(explorationMode == 2){
    		
    		if(robot.getRealRobot()){
    			CommunicationMgr.getCommMgr().sendMsg(null, CommunicationMgr.BOT_START);
        	}
    		
    		exploreTL(robot.getRobotPosX(), robot.getRobotPosY());	//time limited
        	paintAfterSense();
        	
    	}
    	
    	System.out.println("explore function exited");
    	

        areaExplored = getAreaExplored();
        System.out.println("Explored Area: " + areaExplored);
    	
    	//map.genMapDescAfter();
    	Map.generateMapDescriptor(map);
        System.out.println("map desc generated");
    }
    
    private void explore(int x, int y){

    	//loop unless robot is back to its original position 
    	//System.out.println("explore entered");
    	//System.out.println("robotDelay = " + robotDelay);
    	
    	robot.setSpeed(robotDelay); //<-delay time in miliseconds
    	while(true){
    		moveNext(1, false);
    		if(robot.getReachedGoal() && robot.isInStartZone()){
    			System.out.println("exploration done");
    			break;
    		}
    		
       	}
    	//areaExplored = getAreaExplored();
		//System.out.println("Area explored = " + areaExplored);
       	returnToStartPos();
       	System.out.println("return to start position");
    	
    }
    
    private void exploreCL(int x, int y){
    	//System.out.println("coverage limited exploration: = " + coverageLimit);
    	robot.setSpeed(robotDelay); //<-delay time in miliseconds
    	while(getAreaExplored() <= coverageLimit){
    		moveNext(1, false);
    		
    		System.out.println("area explored" + getAreaExplored());
    		if(robot.getReachedGoal() && robot.isInStartZone()){
    			System.out.println("exploration done");
    			break;
    		}
    		
    	}
    	areaExplored = getAreaExplored();
		System.out.println("Area explored = " + areaExplored);
		returnToStartPos();
    }
    
    private void exploreTL(int x, int y){
    	//System.out.println("exploreTL entered");
    	startTime = System.currentTimeMillis();   	
    	endTime = startTime + (timeLimit * 1000);
    	//System.out.println(endTime);
    	
    	//System.out.println("time limited exploration: " + endTime);
    	robot.setSpeed(robotDelay); //<-delay time in miliseconds
    	while(System.currentTimeMillis() <= endTime){
    		moveNext(1, false);
    		int area = getAreaExplored();
    		if(robot.isInStartZone() && area >= 70){
    			break;
    		}
    	}
    	long time = System.currentTimeMillis() - startTime;
    	System.out.println(time);
    	returnToStartPos();
    }
    
    
    //THE MAIN PART****************************************************************************
    private void moveNext(int count, boolean toAndroid){	//determine next move for the robot

    	
    	//System.out.println("rightfree = " + rightFree());
    	//System.out.println("frontfree = " + frontFree());
    	//System.out.println("leftfree = " + leftFree());
    
    	/*
    	if(rightFree()){
    		moveRobot(Constants.MOVEMENT.R);
    		if(frontFree()){
    			moveRobot(Constants.MOVEMENT.F);   		    		 	
    		}
    		rightTurn++;
      	}else if(frontFree()){
    		moveRobot(Constants.MOVEMENT.F);
    		rightTurn = 0;
    	}else if(leftFree()){
    		moveRobot(Constants.MOVEMENT.L);
    		rightTurn = 0;
    	}   	
    	else{
    		moveRobot(Constants.MOVEMENT.R);
    		moveRobot(Constants.MOVEMENT.R);
    		rightTurn = 0;
    	}
    	if(rightFree2() || frontFree2() || leftFree2()){
    		if(rightFree2() || rightFree3()){
    			moveRobot(Constants.MOVEMENT.R);
        		if(frontFree2() || frontFree3()){
        			moveRobot(Constants.MOVEMENT.F);   		    		 	
        		}
        		rightTurn++;
    		}else if(frontFree2() || frontFree3()){
    			moveRobot(Constants.MOVEMENT.F); 
    			rightTurn = 0;
    		}else if(leftFree2() || leftFree3()){
    			moveRobot(Constants.MOVEMENT.L);
    			rightTurn = 0;
    		}else{
    			moveRobot(Constants.MOVEMENT.R);
        		moveRobot(Constants.MOVEMENT.R);
        		rightTurn = 0;
    		}
    	}
    	*/
    		
    	
    	
    	/*
    	if(rightFree2() || leftFree2() || frontFree2() ){
    		if(rightFree2() && rightFree3() && Constants.rightTurn <2){
    			moveRobot(Constants.MOVEMENT.R);
    			if(frontFree3()){
    				moveRobot(Constants.MOVEMENT.F);
    			}
    			Constants.rightTurn++;
    			System.out.println("rightTurn = " + Constants.rightTurn);
    		}else if(rightFree2() && !rightFree3()){
    			moveRobot(Constants.MOVEMENT.R);
    			Constants.rightTurn = 0;
    			System.out.println("rightTurn = " + Constants.rightTurn);
    		}else if(frontFree2() && frontFree3()){
    			moveRobot(Constants.MOVEMENT.F);
    			Constants.rightTurn = 0;
    			System.out.println("rightTurn = " + Constants.rightTurn);
    		}else if(frontFree2() && !frontFree3()){
    			moveRobot(Constants.MOVEMENT.R);
    			Constants.rightTurn = 0;
    			System.out.println("rightTurn = " + Constants.rightTurn);
    		}else if(leftFree2() && leftFree3()){
    			moveRobot(Constants.MOVEMENT.L);
    			Constants.rightTurn = 0;
    			System.out.println("rightTurn = " + Constants.rightTurn);
    		}else if(leftFree2() && !leftFree3()){
    			moveRobot(Constants.MOVEMENT.R);
    			Constants.rightTurn = 0;
    			System.out.println("rightTurn = " + Constants.rightTurn);
    		}else if(rightFree2() && rightFree3()){
        			moveRobot(Constants.MOVEMENT.R);
        			if(frontFree3()){
        				moveRobot(Constants.MOVEMENT.F);
        			}
        			Constants.rightTurn++;
        			System.out.println("rightTurn = " + Constants.rightTurn);
    		}
    		else{
    			moveRobot(Constants.MOVEMENT.R);
    			moveRobot(Constants.MOVEMENT.R);
    			Constants.rightTurn = 0;
    			System.out.println("rightTurn = " + Constants.rightTurn);
    		}
    	}else if(rightFree() || leftFree() || frontFree()){
    		if(rightFree() && Constants.rightTurn <2){
        		moveRobot(Constants.MOVEMENT.R);
        		if(frontFree()){
        			moveRobot(Constants.MOVEMENT.F);   		    		
        		}
        		Constants.rightTurn++;
        		System.out.println("rightTurn = " + Constants.rightTurn);
        	}else if(frontFree()){
        		moveRobot(Constants.MOVEMENT.F);
        		Constants.rightTurn = 0;
        		System.out.println("rightTurn = " + Constants.rightTurn);
        	}else if(leftFree()){
        		moveRobot(Constants.MOVEMENT.L);
        		Constants.rightTurn = 0;
        		System.out.println("rightTurn = " + Constants.rightTurn);
        	}else if(rightFree() && Constants.rightTurn <2){
        		moveRobot(Constants.MOVEMENT.R);
        		if(frontFree()){
        			moveRobot(Constants.MOVEMENT.F);   		    		
        		}
        		Constants.rightTurn++;
        		System.out.println("rightTurn = " + Constants.rightTurn);   	
        	}else{
        		moveRobot(Constants.MOVEMENT.R);
        		moveRobot(Constants.MOVEMENT.R);
        		Constants.rightTurn = 0;
        		System.out.println("rightTurn = " + Constants.rightTurn);
        	}
    	}else{
    		moveRobot(Constants.MOVEMENT.R);
    		moveRobot(Constants.MOVEMENT.R);
    		Constants.rightTurn = 0;
    	}
    	}
    	
    
    	/*
    	if(robot.getRobotPosX()==1){
    		switch(robot.getRobotDir()){
    		case N:
    			if(frontFree2()&& frontFree3()){
     			moveRobot(Constants.MOVEMENT.F);
    			}break;
    		case E:
    			if(leftFree2() && leftFree3()){
        			moveRobot(Constants.MOVEMENT.L);
        			if(frontFree2()&& frontFree3()){
             			moveRobot(Constants.MOVEMENT.F);
            			}
        		}break;
    		case W:
    			if(rightFree2() && rightFree3()){
        			moveRobot(Constants.MOVEMENT.R);
        			if(frontFree2() && frontFree3()){
        				moveRobot(Constants.MOVEMENT.F);
        			}
    			}break;
    		case S:
    			moveRobot(Constants.MOVEMENT.R);
    			moveRobot(Constants.MOVEMENT.R);
    		}
    	}
    	if(rightFree() || rightFree2()){
    		if(rightFree()&& rightFree3()){
    			moveRobot(Constants.MOVEMENT.R);
    			if(frontFree() && frontFree3()){
    				moveRobot(Constants.MOVEMENT.F);
    			}
    		}else if(rightFree2() && rightFree3()){
    			moveRobot(Constants.MOVEMENT.R);
    			if(frontFree2() && frontFree3()){
    				moveRobot(Constants.MOVEMENT.F);
    			}
    		}
    	}else if(frontFree() || frontFree2()){
    		 if(frontFree()&& frontFree3()){
     			moveRobot(Constants.MOVEMENT.F);
     		}
    		 else if(frontFree2() && frontFree3()){
    			moveRobot(Constants.MOVEMENT.F);
    		}
    		
    	}else if(leftFree() || leftFree2()){
    		if(leftFree()){
    			moveRobot(Constants.MOVEMENT.L);
    		}else if(leftFree2() && leftFree3()){
    			moveRobot(Constants.MOVEMENT.L);
    		}
    	}else{
    		moveRobot(Constants.MOVEMENT.R);
    		moveRobot(Constants.MOVEMENT.R);
    	}*/
    	/*
    	if(blindSpotsL()){
    		System.out.println("blindspotL entered");
    		switch(robot.getRobotDir()){
    		case N:
    			moveRobot(Constants.MOVEMENT.R);
    			moveRobot(Constants.MOVEMENT.R);
    			break;
    		case E:
    			moveRobot(Constants.MOVEMENT.R);
    			moveRobot(Constants.MOVEMENT.R);
    			break;
    		case S:
    			moveRobot(Constants.MOVEMENT.R);
    			moveRobot(Constants.MOVEMENT.R);
    			break;
    		case W:
    		}
    		moveRobot(Constants.MOVEMENT.L);
        	moveRobot(Constants.MOVEMENT.L);
        	moveRobot(Constants.MOVEMENT.L);
        	moveRobot(Constants.MOVEMENT.L);
        	
    	}else if(blindSpotsR()){
    		System.out.println("blindspotR entered");
    		switch(robot.getRobotDir()){
    		case N:
    		case E:
    		case S:
    		case W:
    		}
    		moveRobot(Constants.MOVEMENT.L);
        	moveRobot(Constants.MOVEMENT.L);
        	moveRobot(Constants.MOVEMENT.L);
        	moveRobot(Constants.MOVEMENT.L);
        	
    	}else if(blindSpotsN()){
    		System.out.println("blindspotN entered");
    		switch(robot.getRobotDir()){
    		case N:
    		case E:
    		case S:
    		case W:
    		}
    		moveRobot(Constants.MOVEMENT.L);
        	moveRobot(Constants.MOVEMENT.L);
        	moveRobot(Constants.MOVEMENT.L);
        	moveRobot(Constants.MOVEMENT.L);
        	
    	}else if(blindSpotsS()){
    		System.out.println("blindspotS entered");
    		switch(robot.getRobotDir()){
    		case N:
    		case E:
    		case S:
    		case W:
    		}
    		moveRobot(Constants.MOVEMENT.L);
        	moveRobot(Constants.MOVEMENT.L);
        	moveRobot(Constants.MOVEMENT.L);
        	moveRobot(Constants.MOVEMENT.L);
        	
    	}
    	*/
    	/*
    	if(blindSpotsL()||blindSpotsR()||blindSpotsS()||blindSpotsN()){
    		moveRobot(Constants.MOVEMENT.R);
			moveRobot(Constants.MOVEMENT.R);
			moveRobot(Constants.MOVEMENT.R);
			moveRobot(Constants.MOVEMENT.R);
    	}
    	*/
    	if(rightFree() && Constants.rightTurn <2){
    		System.out.println("Right Turn : " + Constants.rightTurn);
    		System.out.println("rightfree = " + rightFree());
    		moveRobot(Constants.MOVEMENT.R);
    		Constants.rightTurn++;
    		if(frontFree()){
    			moveRobot(Constants.MOVEMENT.F);   		    		
    		}
    		
    	}else if(frontFree()){
    		moveRobot(Constants.MOVEMENT.F);
    		Constants.rightTurn=0;
    		Constants.rightTurn2=0;
    	}else if(leftFree()){
    		moveRobot(Constants.MOVEMENT.L);
    		Constants.rightTurn=0;
    		Constants.rightTurn2=0;
    	}  
    	else if(rightFree() && Constants.rightTurn2<2){
    		System.out.println("rightfree = " + rightFree());
    		moveRobot(Constants.MOVEMENT.R);

    		if(frontFree()){
    			moveRobot(Constants.MOVEMENT.F);   		    		
    		}
    		Constants.rightTurn2++;
    		Constants.rightTurn++;
    	}
    	else{
    		moveRobot(Constants.MOVEMENT.R);
    		moveRobot(Constants.MOVEMENT.R);
    		Constants.rightTurn=0;
    		Constants.rightTurn2=0;

    	}
    }

    	
    
    private void robotMove(MOVEMENT m, int count, boolean toAndroid){
    	robot.move(m, count, toAndroid);
    	map.repaint();
    	
    	if(m != MOVEMENT.CALIBRATE){
    		paintAfterSense();
    	}else{
    		CommunicationMgr commMgr = CommunicationMgr.getCommMgr();
            commMgr.recvMsg();
    	}
    	
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
		}
		/*else if(turns == 2){	//rotate right twice (direction in clockwise)
			robotMove(MOVEMENT.R, 1, false);
			robotMove(MOVEMENT.R, 1, false);
		}*/
		
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
	    	case N: 
	    		return isEastFree(); 
	    	case E: 
	    		//System.out.print("South Free\n");
	    		return isSouthFree(); 
	    	
	    	case S: 
	    		//System.out.print("West Free\n");
	    		return isWestFree(); 
	    	case W: 
	    		//System.out.print("North Free\n");
	    		return isNorthFree(); 
	    	default: return false;
	    	}
	    }
	    
	    private boolean leftFree(){		//look left
	    	switch(robot.getRobotDir()){
	    	case N:	return isWestFree();
	    	case E:
	    		//System.out.println("northFree" + isNorthFree());
	    		return isNorthFree();
	    	case S:	return isEastFree();
	    	case W: return isSouthFree();
	    	default: return false;
	    	}
	    }
	    
	    public boolean frontFree(){// look in front
	    	//System.out.print("Currently checking : Frontfree\n");
	    	//System.out.print("Current Direction : " + robot.getRobotDir()+"\n");
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
	    	//System.out.println("Is x+2 y+1 free : " + notObstacleVirtualWall(x+2,y+1));
	    	//System.out.println("Is x+2 y free : " + notObstacleVirtualWall(x+2,y));
	    	//System.out.println("Is x+2 y-1 free : " + notObstacleVirtualWall(x+2,y-1));
	    	return (notObstacleVirtualWall(x+2,y+1) && notObstacleVirtualWall(x+2,y)&& notObstacleVirtualWall(x+2,y+1));

	    }
	    
	    private boolean isWestFree(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	System.out.println("x-1 y+1 free = " + notObstacleVirtualWall(x-1,y+1));
	    	System.out.println("x-1 y free = " + notObstacleVirtualWall(x-1, y));
	    	return(notObstacleVirtualWall(x-1,y+1) && notObstacleVirtualWall(x-1, y));
	    	//return (notObstacleVirtualWall(x-2,y+1) && notObstacleVirtualWall(x-2,y));
	    }
	    
	    private boolean isSouthFree(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return(notObstacleVirtualWall(x, y-1) && notObstacleVirtualWall(x+1, y-1));
	    	//return (notObstacleVirtualWall(x,y-1) && notObstacleVirtualWall(x-1,y-1));
	    }
	    
	    private boolean isNorthFree(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	//System.out.println("Current Robot x position : " + x);
	    	//System.out.println("Current Robot y position : " + y);
	    	System.out.println("Is x+1 y+2 true : " + notObstacleVirtualWall(x+1, y+2));
	    	System.out.println("Is x+0 y+2 true : " + notObstacleVirtualWall(x, y+2));
	    	//System.out.println("Is x-1 y+2 true : " + notObstacleVirtualWall(x-1, y+2));
	    	return(notObstacleVirtualWall(x+1, y+2) && notObstacleVirtualWall(x, y+2));
	    	//return(notObstacleVirtualWall(x,y+2) && notObstacleVirtualWall(x-1, y+2));
	    }
    

	/*
    private boolean rightFree(){	//look right
    	switch(robot.getRobotDir()){
    	case N: 
    		return isEastFree(); 
    	case E: 
    		//System.out.print("South Free\n");
    		return isSouthFree(); 
    	
    	case S: 
    		//System.out.print("West Free\n");
    		return isWestFree(); 
    	case W: 
    		//System.out.print("North Free\n");
    		return isNorthFree(); 
    	default: return false;
    	}
    }
    
    private boolean leftFree(){		//look left
    	switch(robot.getRobotDir()){
    	case N:	return isWestFree();
    	case E:
    		//System.out.println("northFree" + isNorthFree());
    		return isNorthFree();
    	case S:	return isEastFree();
    	case W: return isSouthFree();
    	default: return false;
    	}
    }
    
    public boolean frontFree(){// look in front
    	//System.out.print("Currently checking : Frontfree\n");
    	//System.out.print("Current Direction : " + robot.getRobotDir()+"\n");
    	switch(robot.getRobotDir()){
    	case N:	return isNorthFree();
    	case E: return isEastFree();
    	case S:	return isSouthFree();
    	case W: return isWestFree();
    	default: return false;
    	}
    }
    
    private boolean rightFree2(){	//for 2x2 outiside
    	switch(robot.getRobotDir()){
    	case N: return isEastFree2(); 
    	case E: return isSouthFree2(); 
    	case S: return isWestFree2(); 
    	case W: return isNorthFree2(); 
    	default: return false;
    	}
    }
    
    private boolean leftFree2(){		//for 2x2 outiside
    	switch(robot.getRobotDir()){
    	case N:	return isWestFree2();
    	case E: return isNorthFree2();
    	case S:	return isEastFree2();
    	case W: return isSouthFree2();
    	default: return false;
    	}
    }
    
    public boolean frontFree2(){	//for 2x2 outiside
    	switch(robot.getRobotDir()){
    	case N:	return isNorthFree2();
    	case E: return isEastFree2();
    	case S:	return isSouthFree3();
    	case W: return isWestFree3();
    	default: return false;
    	}
    }
    
    private boolean rightFree3(){	//for 2x2 inside
    	switch(robot.getRobotDir()){
    	case N: return isEastFree2(); 
    	case E: return isSouthFree3(); 
    	case S: return isWestFree3(); 
    	case W: return isNorthFree2(); 
    	default: return false;
    	}
    }
    
    private boolean leftFree3(){		//for 2x2 inside
    	switch(robot.getRobotDir()){
    	case N:	return isWestFree3();
    	case E: return isNorthFree2();
    	case S:	return isEastFree2();
    	case W: return isSouthFree3();
    	default: return false;
    	}
    }
    
    public boolean frontFree3(){	//for 2x2 inside
    	switch(robot.getRobotDir()){
    	case N:	return isNorthFree2();
    	case E: return isEastFree2();
    	case S:	return isSouthFree3();
    	case W: return isWestFree3();
    	default: return false;
    	}
    }
    
*/
    /*
    private boolean isEastFree(){	//true if can move to east
    	
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();
    	System.out.println("Is x+2 y+1 free : " + notObstacleVirtualWall(x+2,y+1));
    	System.out.println("Is x+2 y free : " + notObstacleVirtualWall(x+2,y));
    	System.out.println("Is x+2 y-1 free : " + notObstacleVirtualWall(x+2,y-1));
    	return (notObstacleVirtualWall(x+2,y+1) && notObstacleVirtualWall(x+2,y) && notObstacleVirtualWall(x+2,y-1));
    }
    
    private boolean isWestFree(){
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();
    	System.out.println("x-1 y+1 free = " + notObstacleVirtualWall(x-1,y+1));
    	System.out.println("x-1 y free = " + notObstacleVirtualWall(x-1, y));
    	return(notObstacleVirtualWall(x-2,y+1) && notObstacleVirtualWall(x-2, y) && notObstacleVirtualWall(x-2,y-1));
    	
    }
    
    private boolean isSouthFree(){
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();
    	return(notObstacleVirtualWall(x, y-2) && notObstacleVirtualWall(x+1, y-2) && notObstacleVirtualWall(x-1, y-2));
    	//return (notObstacleVirtualWall(x,y-1) && notObstacleVirtualWall(x-1,y-1));
    }
    
    private boolean isNorthFree(){
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();
    	//System.out.println("Current Robot x position : " + x);
    	//System.out.println("Current Robot y position : " + y);
    	//System.out.println("Is x+1 y+2 true : " + notObstacleVirtualWall(x+1, y+2));
    	//System.out.println("Is x+0 y+2 true : " + notObstacleVirtualWall(x, y+2));
    	//System.out.println("Is x-1 y+2 true : " + notObstacleVirtualWall(x-1, y+2));
    	return(notObstacleVirtualWall(x+1, y+2) && notObstacleVirtualWall(x, y+2) && notObstacleVirtualWall(x-1, y+2));
    	//return(notObstacleVirtualWall(x,y+2) && notObstacleVirtualWall(x-1, y+2));
    }
    
private boolean isEastFree2(){	//for 2x2, outside
    	
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();
    	return (notObstacleVirtualWall(x+2,y+1) && notObstacleVirtualWall(x+2,y) );
    }
    
    private boolean isWestFree2(){	//for 2x2, outside
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();
    	return(notObstacleVirtualWall(x-2,y+1) && notObstacleVirtualWall(x-2, y));
    	
    }
    
    private boolean isSouthFree2(){	//for 2x2, outside
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();
    	return(notObstacleVirtualWall(x, y-2) && notObstacleVirtualWall(x+1, y-2) );
    }
    
    private boolean isNorthFree2(){	//for 2x2, outside
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();
    	return(notObstacleVirtualWall(x+1, y+2) && notObstacleVirtualWall(x, y+2));
    }
    
    
    private boolean isWestFree3(){	//for 2x2, inside
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();
    	return(notObstacleVirtualWall(x-1,y+1) && notObstacleVirtualWall(x-1, y));
    	
    }
    
    private boolean isSouthFree3(){	//for 2x2, inside
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();
    	return(notObstacleVirtualWall(x, y-1) && notObstacleVirtualWall(x+1, y-1));
    }
    */
 
    
    private boolean notObstacleVirtualWall(int x, int y){
    	
    	if(map.checkWithinRange(x, y)){
    		if(!map.isExplored(x,y)){
    			return false;
    		}
    		if(!map.isObstacle(x,y) &&  map.isExplored(x,y)){
    			return true;
    		}
    	}
		return false;
    }
    
    private boolean blindSpotsL(){
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();

    		if(unexp(x-2, y+1)){
    			return true;
    		}
    	return false;
    }
    
    private boolean blindSpotsR(){
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();

    		if(unexp(x+2, y+1)){
    			return true;
    		}
    	return false;
    }
    private boolean blindSpotsN(){
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();

    		if(unexp(x+1, y+2)){
    			return true;
    		}
    	return false;
    }
    private boolean blindSpotsS(){
    	int x = robot.getRobotPosX();
    	int y = robot.getRobotPosY();

    		if(unexp(x-1, y-2) ){
    			return true;
    		}
    	return false;
    }
    
    private boolean unexp(int x, int y){
    	if(map.checkWithinRange(x, y)){
    		if(!map.isExplored(x, y)){
    			return true;
    		}
    	}
    	return false;
    }
    
    
    
    private boolean blocked(int x, int y){ // for checkpoint
    	if(map.checkWithinRange(x, y)){
    	if((map.isObstacle(x, y) && map.isExplored(x, y)) /*|| isUnExp(x,y)*/){
    		return true;
    	}
    	}
    	return false;
    }
    
    private boolean virtualWalls(int x, int y){
    	if(map.checkWithinRange(x, y)){
    		if(map.isVirtualWall(x, y) && map.isExplored(x, y)){
    			return true;
    		}
    	}
    	return false;
    }
    
    private int getAreaExplored(){
    	int total = 0;
    	for(int i=0; i<Constants.MAX_Y ; i++){
    		for(int j=0; j<Constants.MAX_X; j++ ){
    			if(map.getCoordinate(j, i).getIsExplored()){
    				total++;
    			}
    		}
    	}
    	return total;
    }
    
    
    
    private void returnToStartPos(){
    	System.out.println("return to start entered");
    	//**********************************need to change********************************
    	/*
    	if(!robot.getReachedGoal()){	//stopped halfway, go to goal before returning
    		FastestPath toGoal = new FastestPath(map,robot,null );
    		toGoal.runFastestPath(Constants.GOAL_X, Constants.GOAL_Y);
    	}
    	*/
    	
    	System.out.println("return to start entered2");
    	FastestPath toStart = new FastestPath(map, robot, realMap);
    	System.out.println("fastest path initialized");

    	toStart.runFastestPath(Constants.START_Y, Constants.START_X);
    	//exploration complete if return to starting position
    	System.out.println("runFastestPath function done");

    	//print out exploration report
    	System.out.printf("%.2f%% Coverage", (areaExplored / 300.0) * 100.0);
        System.out.println(", " + areaExplored + " Cells");
        System.out.println((System.currentTimeMillis() - startTime) / 1000 + " Seconds");

        
        if (robot.getRealRobot()) {
        	rotateRobot(DIRECTION.W);
        	robotMove(MOVEMENT.CALIBRATE,1,true);
            rotateRobot(DIRECTION.S);
            robotMove(MOVEMENT.CALIBRATE,1,true);
            rotateRobot(DIRECTION.W);
            robotMove(MOVEMENT.CALIBRATE,1,true);
        }
        rotateRobot(DIRECTION.N);
        
    }
    
    //trying gui
    private void paintAfterSense(){
    	
    	robot.setSentors();
    	
    	robot.senseDist(map, realMap);
    	
    	map.repaint();
    }
    
    private void moveRobot(MOVEMENT m){
    	//MOVEMENT.F
    	robot.move(m, 1, false); 		//for the time being
    	map.repaint();
    	
    	if(m!= MOVEMENT.CALIBRATE){
    		
    		paintAfterSense();
    		//System.out.println("testing");
    	}else{
    		CommunicationMgr comm = CommunicationMgr.getCommMgr();
    		comm.recvMsg();
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