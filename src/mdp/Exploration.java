package mdp;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import mdp.Constants.*;


public class Exploration {
    private boolean shouldSetObstacles = true;
	public final Map map;	// for exploration
	private final Map realMap;	//real map 
	private final Robot robot;
    private final int coverageLimit;	
    public int timeLimit;	
    private int areaExplored;
    private long startTime;
    private long endTime = 3600;
    private int calCount = 0;	
    private boolean calibrationMode;	
    private boolean checkptRightTurn = false;
    private int explorationMode = 0; //0=normal, 1=coverage, 2=time 
    public int robotDelay;
    private int[][] previousCoord;
    private boolean expStarted = false;
    //private boolean waypointMode = true;
    
    private int calibrate = 0;
    
    
	
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
    		String msg;
    		//for testing
    		
    		/*
    		while(true){
    			msg = CommunicationMgr.getCommMgr().recvMsg();
    			if(msg.equals("E")){	
    				break;
    			}
    		}
    		*/
            paintAfterSense();	//to sense before exploration 
            
             		
    	}
    	 System.out.println("Starting exploration...");

         startTime = System.currentTimeMillis();
         endTime = startTime + (timeLimit * 1000);
    	
    	
    	if(explorationMode == 0){
    		
    		if(robot.getRealRobot()){
    			//CommunicationMgr.getCommMgr().sendMsg(null, CommunicationMgr.BOT_START);  //no need to send any ack on my side? 
    			//paintAfterSense();	//to sense before exploration 
    			
    			//send map data
    			String descriptor = String.join(";", Map.generateMapDescriptor(map));
                CommunicationMgr.getCommMgr().sendMap(robot.getRobotPosX() + "," + robot.getRobotPosY(), descriptor, robot.getRobotDir(),null);
        	}
        	
    		System.out.println("explore");
    		explore(robot.getRobotPosX(), robot.getRobotPosY());
        	//paintAfterSense();	//paint after exploration done problem 
        	
        	//print out area calculated??
    	}else if(explorationMode == 1){   
    		if(robot.getRealRobot()){
    			CommunicationMgr.getCommMgr().sendMsg(null, CommunicationMgr.BOT_START);
        	}
    		exploreCL(robot.getRobotPosX(), robot.getRobotPosY());	//coverage limited
        	//paintAfterSense();
    	}else if(explorationMode == 2){
    		if(robot.getRealRobot()){
    			CommunicationMgr.getCommMgr().sendMsg(null, CommunicationMgr.BOT_START);
        	}
    		exploreTL(robot.getRobotPosX(), robot.getRobotPosY());	//time limited
        	//paintAfterSense();
    	}
    	
    	System.out.println("explore function exited");
        areaExplored = getAreaExplored();
        System.out.println("Explored Area: " + areaExplored);
    	
    	Map.generateMapDescriptor(map);
        System.out.println("map desc generated");
        
        
    }
    
    private void explore(int x, int y){

    	//loop unless robot is back to its original position 
    	//System.out.println("explore entered");
    	//System.out.println("robotDelay = " + robotDelay);
    	
    	robot.setSpeed(robotDelay); //<-delay time in miliseconds
    	
    	//paintAfterSense(); //update map using sensor value before exploring 
    	
    	while(true /*getAreaExplored() != 300 && System.currentTimeMillis() <= endTime*/ ){
    		moveNext(1, robot.getRealRobot());
    		if(robot.getReachedGoal() && robot.isInStartZone() /*|| getAreaExplored() == 300)*/){
    			System.out.println("exploration done");
    			break;
    		}
    		
       	}
    	areaExplored = getAreaExplored();
		System.out.println("Area explored = " + areaExplored);
		long time = System.currentTimeMillis() - startTime;
    	System.out.println(time);
       	if(!robot.isInStartZone() || robot.getRobotDir() != DIRECTION.N){
        	//returnToStartPos();
       	}
        
       	rotateRobot(DIRECTION.N);
       	
        try {
            Thread.sleep(1000);
            //calibrate at the end
        } catch (InterruptedException ex) {
            Logger.getLogger(Exploration.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       	moveRobot(MOVEMENT.CALIBRATES);
       	System.out.println(time);
       	System.out.println("return to start position");
    	
    }
    
    private void exploreCL(int x, int y){
    	//System.out.println("coverage limited exploration: = " + coverageLimit);
    	robot.setSpeed(robotDelay); //<-delay time in miliseconds
    	while(getAreaExplored() <= coverageLimit){
    		moveNext(1, robot.getRealRobot());
    		
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
    		
    		moveNext(1, robot.getRealRobot());
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

    	

    	//clearBox();
    	
    	System.out.println("calCount = " + Constants.count2);
    	/*
    	if(corner() && calCount == 0){
    		Constants.rightTurn = 0;
    		moveRobot(Constants.MOVEMENT.CALIBRATE);
    		System.out.println("calibrating......................................");
    		calCount++;
    		Constants.front = 0;
    		
    	}else{
    	*/
    		if(Constants.front > 4 /*&& !corner()*/ /*&& Constants.count2 == 0*/){
    			
    			if(gotWallonRight()){
    				System.out.println("calibrating......................................");
    				moveRobot(Constants.MOVEMENT.CALIBRATE);
        			
    			}else if(gotWallonLeft()){
    				System.out.println("calibrating LEFT......................................");
    				moveRobot(Constants.MOVEMENT.CALIBRATEL);
    			}
    			
    			++Constants.count2;
    			
    			Constants.front = 0;
    			
    		}else
    		
    		/*
    		if(frontFreeD()){
        		Constants.rightTurn = 0;
        		moveRobot(Constants.MOVEMENT.L);
        		Constants.front = 0;
        		calCount=0;
        	}else*/
        	if(rightFree() && Constants.rightTurn <2){
        		if(gotWallonLeft()){
    				System.out.println("calibrating LEFT......................................");
    				moveRobot(Constants.MOVEMENT.CALIBRATEL);
    			}
        		moveRobot(Constants.MOVEMENT.R);
        		Constants.rightTurn++;
        		Constants.front++;
        		if(frontFree()){
        			moveRobot(Constants.MOVEMENT.F);
        			Constants.front++;
        		}
        		

        	}/*else if(frontFreeC()){
        		moveRobot(Constants.MOVEMENT.R);
        		moveRobot(Constants.MOVEMENT.R);
        		Constants.rightTurn=0;
        		Constants.rightTurn2=0;

        	}*/
        	
        	else if(frontFree() /*&& Constants.front < 3*/){
        		moveRobot(Constants.MOVEMENT.F);
        		Constants.rightTurn=0;
        		Constants.rightTurn2=0;
        		Constants.front++;
        		Constants.count2 = 0;
        	}    	
        	else if(leftFree()){
        		moveRobot(Constants.MOVEMENT.L);
        		Constants.rightTurn=0;
        		Constants.rightTurn2=0;
        		Constants.front++;

        	} 
        	
        	else if(rightFree() && Constants.rightTurn2<2){
        		//System.out.println("rightfree = " + rightFree());
        		if(gotWallonLeft()){
    				System.out.println("calibrating LEFT......................................");
    				moveRobot(Constants.MOVEMENT.CALIBRATEL);
    			}
        		moveRobot(Constants.MOVEMENT.R);
        		Constants.front++;



        		if(frontFree()){
        			moveRobot(Constants.MOVEMENT.F);
        			Constants.front++;
        		}
        		
        		Constants.rightTurn2++;
        		Constants.rightTurn++;
        	}
        	else{
        		moveRobot(Constants.MOVEMENT.R);
        		moveRobot(Constants.MOVEMENT.R);
        		Constants.rightTurn=0;
        		Constants.rightTurn2=0;
        		Constants.front++;
        		

    	
        	}}
    		
    	 
    	
    	
    	
    	


    	
    
    private void robotMove(MOVEMENT m, int count, boolean toAndroid){
    	robot.move(m, count, toAndroid);
    	map.repaint();
    
    	if(robot.getRealRobot()){
            CommunicationMgr comm = CommunicationMgr.getCommMgr();
            String descriptor = String.join(";", Map.generateMapDescriptor(map));
            comm.sendMap(robot.getRobotPosX() + "," + robot.getRobotPosY(), descriptor, robot.getRobotDir(), robot.sendData(m));
    	}
    	
    	
    }
    
    private void calibrateBot(DIRECTION targetDir) {
		DIRECTION dir = robot.getRobotDir();
		rotateRobot(targetDir);
		robotMove(MOVEMENT.CALIBRATE, 1, robot.getRealRobot());		//see if i need to change boolean
		rotateRobot(dir); 
		
	}

	private void rotateRobot(DIRECTION targetDir) {
		int turns = Math.abs(robot.getRobotDir().ordinal()-targetDir.ordinal());
		if(turns>2){	//if multiple turns, decide whether to rotate left or right 
			turns = turns %2;
		}
		
		if(turns == 1){	//after modulus
			if(DIRECTION.next(robot.getRobotDir()) == targetDir){	//if clockwise
				robotMove(MOVEMENT.R, 1, robot.getRealRobot());
			}else{
				robotMove(MOVEMENT.L, 1, robot.getRealRobot());
			}
		}else if(turns == 2){	//if turns 2, left 2 turns and right 2 turns are the same
			//robotMove(MOVEMENT.U, 1, robot.getRealRobot());
                    shouldSetObstacles = false;
                    robotMove(MOVEMENT.R, 1, robot.getRealRobot());
                    robotMove(MOVEMENT.R, 1, robot.getRealRobot());
		}
	
		
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
	    
	    public void clearBox(){
	    	if(!isNorthFree() && !isEastFree() && !isSouthFree() && !isWestFree()){
	    		int x = robot.getRobotPosX();
	    		int y = robot.getRobotPosY();
	    		
	    		//north
	    		map.getCoordinate(x+1, y+2).setUnExplored();
	    		map.getCoordinate(x, y+2).setUnExplored();
	    		map.getCoordinate(x-1, y+2).setUnExplored();
	    		
	    		//east
	    		map.getCoordinate(x+2, y+1).setUnExplored();
	    		map.getCoordinate(x+2, y).setUnExplored();
	    		map.getCoordinate(x+2, y-1).setUnExplored();
	    		
	    		//west
	    		map.getCoordinate(x-2, y+1).setUnExplored();
	    		map.getCoordinate(x-2, y).setUnExplored();
	    		map.getCoordinate(x-2, y-1).setUnExplored();
	    		
	    		//south
	    		map.getCoordinate(x+1, y-2).setUnExplored();
	    		map.getCoordinate(x, y-2).setUnExplored();
	    		map.getCoordinate(x-1, y-2).setUnExplored();
	    	}
	    }
	    
	    public boolean frontFreeB(){// look in front
	    	//System.out.print("Currently checking : Frontfree\n");
	    	//System.out.print("Current Direction : " + robot.getRobotDir()+"\n");
	    	switch(robot.getRobotDir()){
	    	case N:	return isNorthFreeB();
	    	case E: return isEastFreeB();
	    	case S:	return isSouthFreeB();
	    	case W: return isWestFreeB();
	    	default: return false;
	    	}
	    }
	    
	  
	    public boolean frontFreeC(){// look in front
	    	//System.out.print("Currently checking : Frontfree\n");
	    	//System.out.print("Current Direction : " + robot.getRobotDir()+"\n");
	    	switch(robot.getRobotDir()){
	    	case N:	return isNorthFreeC();
	    	case E: return isEastFreeC();
	    	case S:	return isSouthFreeC();
	    	case W: return isWestFreeC();
	    	default: return false;
	    	}
	    }
	    
	 
	    
	    public boolean frontFreeD(){// look in front
	    	//System.out.print("Currently checking : Frontfree\n");
	    	//System.out.print("Current Direction : " + robot.getRobotDir()+"\n");
	    	switch(robot.getRobotDir()){
	    	case N:	return !isNorthFreeD();
	    	case E: return !isEastFreeD();
	    	case S:	return !isSouthFreeD();
	    	case W: return !isWestFreeD();
	    	default: return false;
	    	}
	    	
	    	
	    }
	    
	    public boolean corner(){
	    	
	    	if((robot.getRobotPosX() == 13 && robot.getRobotPosY() == 1) || (robot.getRobotPosX() == 13 && robot.getRobotPosY() == 18) || (robot.getRobotPosX() == 1 && robot.getRobotPosY()==18)){
	    		return true;
	    	}/*
	    	else if(robot.getRobotPosX()!= 1 && robot.getRobotPosY()!= 1){
	    		System.out.println("robot pos entered" );
	    		
	    		switch(robot.getRobotDir()){
		    	case N: System.out.println("north: " + notNorthFree() + ", east: " + notEastFree() );
		    		return notEastFree() && notNorthFree();
		    		
		    	case E: System.out.println("east: " + notEastFree() + ", south: " + notSouthFree() );
		    		return notEastFree() && notSouthFree();
		    	case S: System.out.println("south: " + notSouthFree() + ", west: " + notWestFree() );
		    		return notSouthFree() && notWestFree();
		    	case W: System.out.println("west: " + notWestFree() + ", north: " + notNorthFree() );
		    		return notWestFree() && notNorthFree();
		    	default: return false;
		    	}
	    	}*/else{
	    		return false;
	    	}
	    	
	    	
	    	/*
	    	if((robot.getRobotPosX() == 13 && robot.getRobotPosY() == 1) || (robot.getRobotPosX() == 13 && robot.getRobotPosY() == 18) || (robot.getRobotPosX() == 1 && robot.getRobotPosY()==18)){
	    		return true;
	    	}
	    	return false;*/
	    	//return false;
	    }
	   
	    public boolean gotWallonRight(){
	    	switch(robot.getRobotDir()){
	    	case N: return notEastFree();
	    	case E: return notSouthFree();
	    	case S: return notWestFree();
	    	case W: return notNorthFree();
	    	default: return false;
	    	}
	    }
	    public boolean gotWallonLeft(){
	    	switch(robot.getRobotDir()){
	    	case N: return notWestFree();
	    	case E: return notNorthFree();
	    	case S: return notEastFree();
	    	case W: return notSouthFree();
	    	default: return false;
	    	}
	    }
	    
	    private boolean isEastFree(){	//true if can move to east
	    	
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	//System.out.println("Is x+2 y+1 free : " + notObstacleVirtualWall(x+2,y+1));
	    	//System.out.println("Is x+2 y free : " + notObstacleVirtualWall(x+2,y));
	    	//System.out.println("Is x+2 y-1 free : " + notObstacleVirtualWall(x+2,y-1));
	    	
	    	//2X2
	    	//return (notObstacleVirtualWall(x+2,y+1) && notObstacleVirtualWall(x+2,y);
	    	
	    	//3X3
	    	return (notObstacleVirtualWall(x+2,y+1) && notObstacleVirtualWall(x+2,y)&& notObstacleVirtualWall(x+2,y-1));
	    	

	    }
	    
	    private boolean isWestFree(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	//System.out.println("x-1 y+1 free = " + notObstacleVirtualWall(x-1,y+1));
	    	//System.out.println("x-1 y free = " + notObstacleVirtualWall(x-1, y));
	    	
	    	//2X2
	    	//return(notObstacleVirtualWall(x-1,y+1) && notObstacleVirtualWall(x-1, y));
	    	//return (notObstacleVirtualWall(x-2,y+1) && notObstacleVirtualWall(x-2,y));
	    	
	    	//3X3
	    	return (notObstacleVirtualWall(x-2,y+1) && notObstacleVirtualWall(x-2,y) && notObstacleVirtualWall(x-2, y-1));
	    }
	    
	    private boolean isSouthFree(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	
	    	//2X2
	    	//return(notObstacleVirtualWall(x, y-1) && notObstacleVirtualWall(x+1, y-1));
	    	//return (notObstacleVirtualWall(x,y-1) && notObstacleVirtualWall(x-1,y-1));
	    	
	    	//3X3
	    	return (notObstacleVirtualWall(x-1,y-2) && notObstacleVirtualWall(x,y-2) && notObstacleVirtualWall(x+1, y-2));
	    }
	    
	    private boolean isNorthFree(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	//System.out.println("Current Robot x position : " + x);
	    	//System.out.println("Current Robot y position : " + y);
	    	//System.out.println("Is x+1 y+2 true : " + notObstacleVirtualWall(x+1, y+2));
	    	//System.out.println("Is x+0 y+2 true : " + notObstacleVirtualWall(x, y+2));
	    	//System.out.println("Is x-1 y+2 true : " + notObstacleVirtualWall(x-1, y+2));
	    	
	    	//2X2
	    	//return(notObstacleVirtualWall(x+1, y+2) && notObstacleVirtualWall(x, y+2));
	    	//return(notObstacleVirtualWall(x,y+2) && notObstacleVirtualWall(x-1, y+2));
	    	
	    	//3X3
	    	return (notObstacleVirtualWall(x-1,y+2) && notObstacleVirtualWall(x,y+2) && notObstacleVirtualWall(x+1, y+2));
	    }
    

	    private boolean isNorthFreeB(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	boolean inner = (notObstacleVirtualWall(x-1,y+2) && notObstacleVirtualWall(x,y+2) && notObstacleVirtualWall(x+1, y+2));
	    	boolean outer = (notObstacleVirtualWall(x-1,y+3) && notObstacleVirtualWall(x,y+3) && notObstacleVirtualWall(x+1, y+3));
	    	
	    	
	    	return inner && outer;  
	    }
	    
	    private boolean isEastFreeB(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	boolean inner = (notObstacleVirtualWall(x+2,y-1) && notObstacleVirtualWall(x+2,y) && notObstacleVirtualWall(x+2, y+1));
	    	boolean outer = (notObstacleVirtualWall(x+3,y-1) && notObstacleVirtualWall(x+3,y) && notObstacleVirtualWall(x+3, y+1));
	    	
	    	
	    	return inner && outer;  
	    }
	    
	    
	    private boolean isSouthFreeB(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	boolean inner = (notObstacleVirtualWall(x-1,y-2) && notObstacleVirtualWall(x,y-2) && notObstacleVirtualWall(x+1, y-2));
	    	boolean outer = (notObstacleVirtualWall(x-1,y-3) && notObstacleVirtualWall(x,y-3) && notObstacleVirtualWall(x+1, y-3));
	    	
	    	
	    	return inner && outer ;  
	    }
	    
	    private boolean isWestFreeB(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	boolean inner = (notObstacleVirtualWall(x-2,y-1) && notObstacleVirtualWall(x-2,y) && notObstacleVirtualWall(x-2, y+1));
	    	boolean outer = (notObstacleVirtualWall(x-3,y-1) && notObstacleVirtualWall(x-3,y) && notObstacleVirtualWall(x-3, y+1));
	    	
	    	
	    	return inner && outer;  
	    }
	    
	    ////
	    private boolean isNorthFreeC(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	boolean inner = (notObstacleVirtualWall(x-1,y+2) && notObstacleVirtualWall(x,y+2) && notObstacleVirtualWall(x+1, y+2));
	    	boolean outer = (notObstacleVirtualWall(x-1,y+3) && notObstacleVirtualWall(x,y+3) && notObstacleVirtualWall(x+1, y+3));
	    	boolean outer2 = (!notObstacleVirtualWall(x-1,y+4) || !notObstacleVirtualWall(x,y+4) || !notObstacleVirtualWall(x+1, y+4));
	    	boolean sides = ((!notObstacleVirtualWall(x-2,y+2) || !notObstacleVirtualWall(x-2,y+3)) && ((!notObstacleVirtualWall(x+2, y+2) || !notObstacleVirtualWall(x+2, y+3))));
	    	
	    	return inner && outer && outer2 && sides;  
	    }
	    
	    private boolean isEastFreeC(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	boolean inner = (notObstacleVirtualWall(x+2,y-1) && notObstacleVirtualWall(x+2,y) && notObstacleVirtualWall(x+2, y+1));
	    	boolean outer = (notObstacleVirtualWall(x+3,y-1) && notObstacleVirtualWall(x+3,y) && notObstacleVirtualWall(x+3, y+1));
	    	boolean outer2 = (!notObstacleVirtualWall(x+4,y-1) || !notObstacleVirtualWall(x+4,y) || !notObstacleVirtualWall(x+4, y+1));
	    	boolean sides = ((!notObstacleVirtualWall(x+2,y-2) || !notObstacleVirtualWall(x+3,y-2)) && (!notObstacleVirtualWall(x+2, y+2) || !notObstacleVirtualWall(x+3, y+2)));
	    	
	    	return inner && outer && outer2 && sides;  
	    }
	    
	    
	    private boolean isSouthFreeC(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	boolean inner = (notObstacleVirtualWall(x-1,y-2) && notObstacleVirtualWall(x,y-2) && notObstacleVirtualWall(x+1, y-2));
	    	boolean outer = (notObstacleVirtualWall(x-1,y-3) && notObstacleVirtualWall(x,y-3) && notObstacleVirtualWall(x+1, y-3));
	    	boolean outer2 = (!notObstacleVirtualWall(x-1,y-4) || !notObstacleVirtualWall(x,y-4) || !notObstacleVirtualWall(x+1, y-4));
	    	boolean sides = ((!notObstacleVirtualWall(x-2,y-2) || !notObstacleVirtualWall(x-2,y-3)) && (!notObstacleVirtualWall(x+2, y-2) || !notObstacleVirtualWall(x+2, y-3)));
	    	
	    	return inner && outer && outer2 && sides;  
	    }
	    
	    private boolean isWestFreeC(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	boolean inner = (notObstacleVirtualWall(x-2,y-1) && notObstacleVirtualWall(x-2,y) && notObstacleVirtualWall(x-2, y+1));
	    	boolean outer = (notObstacleVirtualWall(x-3,y-1) && notObstacleVirtualWall(x-3,y) && notObstacleVirtualWall(x-3, y+1));
	    	boolean outer2 = (!notObstacleVirtualWall(x-4,y-1) || !notObstacleVirtualWall(x-4,y) || !notObstacleVirtualWall(x-4, y+1));
	    	boolean sides = ((!notObstacleVirtualWall(x-2,y+2) || !notObstacleVirtualWall(x-3,y+2)) && (!notObstacleVirtualWall(x-2, y-2) || !notObstacleVirtualWall(x-3, y-3)));
	    	
	    	return inner && outer && outer2 && sides;  
	    }
	    
	    private boolean isNorthFreeD(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return (map.checkWithinRange(x-1,y+2) && map.checkWithinRange(x,y+2) && map.checkWithinRange(x+1, y+2));

	    }
	    
	    private boolean isEastFreeD(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return (map.checkWithinRange(x+2,y-1) && map.checkWithinRange(x+2,y) && map.checkWithinRange(x+2, y+1));
	    	
	    }
	    
	    
	    private boolean isSouthFreeD(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return (map.checkWithinRange(x-1,y-2) && map.checkWithinRange(x,y-2) && map.checkWithinRange(x+1, y-2));
	    	
	    	
	    }
	    
	    private boolean isWestFreeD(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return (map.checkWithinRange(x-2,y-1) && map.checkWithinRange(x-2,y) && map.checkWithinRange(x-2, y+1));
	    	
	    }
	    
	    private boolean notNorthFree(){
	    	//returns true if not free 
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return (isCorner(x-1,y+2) && isCorner(x,y+2) && isCorner(x+1, y+2)) || (isCorner(x-1,y+3) && isCorner(x,y+3) && isCorner(x+1, y+3)) || (isCorner(x-1,y+4) && isCorner(x,y+4) && isCorner(x+1, y+4)) ;

	    }
	    
	    private boolean notEastFree(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return (isCorner(x+2,y+1) && isCorner(x+2,y) && isCorner(x+2, y-1)) || (isCorner(x+3,y+1) && isCorner(x+3,y) && isCorner(x+3, y-1)) ||  (isCorner(x+4,y+1) && isCorner(x+4,y) && isCorner(x+4, y-1));
	    	
	    }
	    
	    
	    private boolean notSouthFree(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return (isCorner(x,y-2) && isCorner(x+1, y-2) && isCorner(x-1, y-2)) || (isCorner(x,y-3) && isCorner(x+1, y-3) && isCorner(x-1, y-3)) || (isCorner(x,y-4) && isCorner(x+1, y-4) && isCorner(x-1, y-4));
	    	
	    	
	    }
	    
	    private boolean notWestFree(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return (isCorner(x-2,y-1) && isCorner(x-2,y) && isCorner(x-2, y+1)) || (isCorner(x-3,y-1) && isCorner(x-3,y) && isCorner(x-3, y+1))|| (isCorner(x-4,y-1) && isCorner(x-4,y) && isCorner(x-4, y+1));
	    	
	    }
	        
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
    
    private boolean isCorner(int x, int y){
    	//returns true if its a wall     	
    	if(!map.checkWithinRange(x, y)){
    		System.out.println("not within range true");
    		return true;
    	}/*else if(!notObstacleVirtualWall(x,y) ){
    		System.out.println("is obstacle");
    		return true;
    	}*/else
    		if(map.checkWithinRange(x, y) && map.getCoordinate(x, y).getIsExplored() && map.getCoordinate(x, y).getIsObstacle()){
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
    	
    	//System.out.println("return to start entered2");
    	FastestPath toStart = new FastestPath(map, robot, realMap);
    	System.out.println("fastest path initialized");

    	toStart.runFastestPath(Constants.START_Y, Constants.START_X);
    	//exploration complete if return to starting position
    	System.out.println("runFastestPath function done");

    	//print out exploration report
    	System.out.printf("%.2f%% Coverage", (areaExplored / 300.0) * 100.0);
        System.out.println(", " + areaExplored + " Cells");
        System.out.println((System.currentTimeMillis() - startTime) / 1000 + " Seconds");

        /* calibration done on the robot side 
        if (robot.getRealRobot()) {
        	rotateRobot(DIRECTION.W);
        	robotMove(MOVEMENT.CALIBRATE,1,true);
            rotateRobot(DIRECTION.S);
            robotMove(MOVEMENT.CALIBRATE,1,true);
            rotateRobot(DIRECTION.W);
            robotMove(MOVEMENT.CALIBRATE,1,true);
        }*/
        
        //rotateRobot(DIRECTION.N);
        
        
        
    }
    
    //trying gui
    private void paintAfterSense(){
    	//System.out.println("paintAfterSense entered");
    	
    	robot.setSentors();
    	
    	//System.out.println("setSensors exited");
    	if(shouldSetObstacles == true) 
            robot.senseDist(map, realMap);
    	
    	//System.out.println("sensedist exited");
    	
    	map.repaint();
    }
    
    public void moveRobot(MOVEMENT m){
    	
    	robot.move(m, 1, robot.getRealRobot()); 		//for the time being
    	if(robot.getRealRobot() && m != MOVEMENT.CALIBRATE){
            CommunicationMgr comm = CommunicationMgr.getCommMgr();
            
            String descriptor = String.join(";", Map.generateMapDescriptor(map));
            comm.sendMap(robot.getRobotPosX() + "," + robot.getRobotPosY(), descriptor, robot.getRobotDir(), robot.sendData(m));
        	
    	}
    	
    	
    	
    	map.repaint();
    	
    	if(m!= MOVEMENT.CALIBRATE){
    		paintAfterSense();
    	}else if (m == MOVEMENT.CALIBRATE){
    		//calibration command 
    		
    		CommunicationMgr comm = CommunicationMgr.getCommMgr();
        	comm.sendMsg("K", CommunicationMgr.BOT_INSTR);
        	paintAfterSense();
    	}else if(m == MOVEMENT.CALIBRATEL){
    		CommunicationMgr comm = CommunicationMgr.getCommMgr();
        	comm.sendMsg("J", CommunicationMgr.BOT_INSTR);
        	paintAfterSense();
    	}else if (m == MOVEMENT.CALIBRATES){
    		CommunicationMgr comm = CommunicationMgr.getCommMgr();
        	comm.sendMsg("S", CommunicationMgr.BOT_INSTR);
        	paintAfterSense();
    	}
    	
    	
    }

    
}