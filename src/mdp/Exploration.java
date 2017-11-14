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
    private int explorationMode = 0; //0=normal, 1=coverage, 2=time 
    public int robotDelay;

	
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
    		System.out.println("Starting exploration");
            paintAfterSense();		//get sensor data before exploration 
   		
    	}
    	 System.out.println("Starting exploration...");

         startTime = System.currentTimeMillis();
         endTime = startTime + (timeLimit * 1000);
    	
    	
    	if(explorationMode == 0){
    		
    		if(robot.getRealRobot()){
    			//send map data
    			String descriptor = String.join(";", Map.generateMapDescriptor(map));
                CommunicationMgr.getCommMgr().sendMap(robot.getRobotPosX() + "," + robot.getRobotPosY(), descriptor, robot.getRobotDir(),null);
        	}
        	
    		System.out.println("explore");
    		explore(robot.getRobotPosX(), robot.getRobotPosY());

    	}else if(explorationMode == 1){   
    		if(robot.getRealRobot()){
    			CommunicationMgr.getCommMgr().sendMsg(null, CommunicationMgr.BOT_START);
        	}
    		exploreCL(robot.getRobotPosX(), robot.getRobotPosY());	//coverage limited
    		
    	}else if(explorationMode == 2){
    		if(robot.getRealRobot()){
    			CommunicationMgr.getCommMgr().sendMsg(null, CommunicationMgr.BOT_START);
        	}
    		exploreTL(robot.getRobotPosX(), robot.getRobotPosY());	//time limited

    	}
    	
    	System.out.println("explore function exited");
        areaExplored = getAreaExplored();
        System.out.println("Explored Area: " + areaExplored);
    	
    	Map.generateMapDescriptor(map);
        System.out.println("map desc generated");
        
        
    }
    
    private void explore(int x, int y){
    	
    	robot.setSpeed(robotDelay); //delay time in miliseconds
    	
    	while(true ){
    		moveNext(1, robot.getRealRobot());
    		if(robot.getReachedGoal() && robot.isInStartZone() || getAreaExplored() == 300){
    			System.out.println("exploration done");
    			break;
    		}
    		
       	}
    	
    	areaExplored = getAreaExplored();
		System.out.println("Area explored = " + areaExplored);
		long time = System.currentTimeMillis() - startTime;
    	System.out.println(time);
       	if(!robot.isInStartZone() || robot.getRobotDir() != DIRECTION.N){
       		returnToStartPos();
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
    	startTime = System.currentTimeMillis();   	
    	endTime = startTime + (timeLimit * 1000);

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
    
    
    private void moveNext(int count, boolean toAndroid){	//determine next move for the robot

    	System.out.println("front count = " + Constants.front);

    		if(Constants.front > 3 && !corner() && Constants.count2 == 0 && robot.getRealRobot()){
    			if(robot.getRealRobot()){
    				System.out.println("right, left" + gotWallonRight() + gotWallonLeft());
        			if(gotWallonRight()){
        				System.out.println("calibrating......................................");
        				moveRobot(Constants.MOVEMENT.CALIBRATE);
        				Constants.front = 0;
            			
        			}else if(gotWallonLeft()){
        				System.out.println("calibrating LEFT......................................");
        				moveRobot(Constants.MOVEMENT.CALIBRATEL);
        				Constants.front = 0;
        			}else{
        				Constants.front ++;
        			}
        			++Constants.count2;
    			}		
    			
    		}else
        	if(rightFree() && Constants.rightTurn <2){
        		moveRobot(Constants.MOVEMENT.R);
        		Constants.rightTurn++;
        		Constants.front++;
        		if(frontFree()){
        			moveRobot(Constants.MOVEMENT.F);
        			Constants.front++;
        			Constants.count2 = 0;
        		}
        		

        	}
        	
        	else if(frontFree()){
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
        	}
    }
    		
 
    private void robotMove(MOVEMENT m, int count, boolean toAndroid){
    	robot.move(m, count, toAndroid);
    	map.repaint();
    
    	if(robot.getRealRobot()){
            CommunicationMgr comm = CommunicationMgr.getCommMgr();
            String descriptor = String.join(";", Map.generateMapDescriptor(map));
            comm.sendMap(robot.getRobotPosX() + "," + robot.getRobotPosY(), descriptor, robot.getRobotDir(), robot.sendData(m));
    	}
    	
    	
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
                    shouldSetObstacles = false;
                    robotMove(MOVEMENT.R, 1, robot.getRealRobot());
                    robotMove(MOVEMENT.R, 1, robot.getRealRobot());
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
	    	case E: return isNorthFree();
	    	case S:	return isEastFree();
	    	case W: return isSouthFree();
	    	default: return false;
	    	}
	    }
	    
	    public boolean frontFree(){// look in front
	    	switch(robot.getRobotDir()){
	    	case N:	return isNorthFree();
	    	case E: return isEastFree();
	    	case S:	return isSouthFree();
	    	case W: return isWestFree();
	    	default: return false;
	    	}
	    }
	    
	    public boolean corner(){
	    	if((robot.getRobotPosX() == 13 && robot.getRobotPosY() == 1) || (robot.getRobotPosX() == 13 && robot.getRobotPosY() == 18) || (robot.getRobotPosX() == 1 && robot.getRobotPosY()==18)){
	    		return true;
	    	}else{
	    		return false;
	    	}
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
	    	return (notObstacleVirtualWall(x+2,y+1) && notObstacleVirtualWall(x+2,y)&& notObstacleVirtualWall(x+2,y-1));
	    	

	    }
	    
	    private boolean isWestFree(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return (notObstacleVirtualWall(x-2,y+1) && notObstacleVirtualWall(x-2,y) && notObstacleVirtualWall(x-2, y-1));
	    }
	    
	    private boolean isSouthFree(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return (notObstacleVirtualWall(x-1,y-2) && notObstacleVirtualWall(x,y-2) && notObstacleVirtualWall(x+1, y-2));
	    }
	    
	    private boolean isNorthFree(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return (notObstacleVirtualWall(x-1,y+2) && notObstacleVirtualWall(x,y+2) && notObstacleVirtualWall(x+1, y+2));
	    }
    
	    
	    private boolean notNorthFree(){
	    	//returns true if not free 
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return (gotWall(x-1,y+2) && gotWall(x,y+2) && gotWall(x+1, y+2))  ;
	    }
	    
	    private boolean notEastFree(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return (gotWall(x+2,y+1) && gotWall(x+2,y) && gotWall(x+2, y-1)) ;
	    	
	    }
	    
	    
	    private boolean notSouthFree(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return (gotWall(x,y-2) && gotWall(x+1, y-2) && gotWall(x-1, y-2));
	    	
	    }
	    
	    private boolean notWestFree(){
	    	int x = robot.getRobotPosX();
	    	int y = robot.getRobotPosY();
	    	return (gotWall(x-2,y-1) && gotWall(x-2,y) && gotWall(x-2, y+1) );
	    	
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
    
    
    private boolean gotWall(int x, int y){
    	if(!map.checkWithinRange(x, y)){
    		return true;
    	}else{
    		if(map.checkWithinRange(x, y) && map.getCoordinate(x, y).getIsExplored() && map.getCoordinate(x, y).getIsObstacle()){
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
    	System.out.println("return to start");

    	FastestPath toStart = new FastestPath(map, robot, realMap);
    	toStart.runFastestPath(Constants.START_Y, Constants.START_X);

    	//print out exploration report
    	System.out.printf("%.2f%% Coverage", (areaExplored / 300.0) * 100.0);
        System.out.println(", " + areaExplored + " Cells");
        System.out.println((System.currentTimeMillis() - startTime) / 1000 + " Seconds");


    }
    
    //trying gui
    private void paintAfterSense(){    	
    	robot.setSentors();
    	
    	if(shouldSetObstacles == true){
    		robot.senseDist(map, realMap);
    	} 
            
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