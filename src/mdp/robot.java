package mdp;

import java.util.concurrent.TimeUnit;
import mdp.CommunicationMgr.*;
import mdp.Constants.*;

import mdp.Constants.*;

public class Robot {
	private int size = Constants.ROBOT_SIZE;	
	private int width = Constants.ROBOT_WIDTH;
	private int startPos_X = Constants.START_X;
	private int startPos_Y = Constants.START_Y;
	private int goalPos_X = Constants.GOAL_X;
	private int goalPos_Y = Constants.GOAL_Y;
	public int robotPos_X;
	public int robotPos_Y; 
	private int speed;
	
	// change the sensor position accordingly
	/*
	private final Sensor topLF_S;
	private final Sensor topRF_S;
	private final Sensor bottomL_S;
	private final Sensor topRL_L;
	private final Sensor bottomL_S;
	private final Sensor bottomR_S;
	*/
	private final Sensor topLF_S;
	private final Sensor topRF_S;
	private final Sensor topLR_L;	//top left facing right
	private final Sensor topRL_L;	//top right facing left
	private final Sensor bottomL_S;
	private final Sensor bottomR_S;
	 
	private DIRECTION robotDir = DIRECTION.N;	// can change later
	public static final int INFINITE_COST = 9999;
	public static final int MOVE_COST = 10;                         // cost of FORWARD, BACKWARD movement
	public static final int TURN_COST = 20;                         // cost of RIGHT, LEFT movement
	
	private boolean reachedGoal;	
	private final boolean realRobot;  //use when connected to the robot
	

    public Robot(int x, int y, boolean realRobot) {
        robotPos_X = x;
        robotPos_Y = y;
        speed = Constants.ROBOT_SPEED;
        this.realRobot = realRobot;
        
        //initialize sensor here
        /*
        topLF_S = new Sensor(Constants.RANGE_LONG_MIN, Constants.RANGE_LONG_MAX, robotPos_X-1, robotPos_Y+1, "topLF_S", this.robotDir);
        topRF_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X-1, robotPos_Y, "topRF_S", DIRECTION.W);
        bottomL_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X-1, robotPos_Y-1, "bottomL_S", DIRECTION.W);
        topRL_L = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X+1, robotPos_Y+1, "topRL_L", DIRECTION.E);
        bottomL_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X+1, robotPos_Y, "bottomL_S", DIRECTION.E);
        bottomR_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X+1, robotPos_Y-1, "bottomR_S", DIRECTION.E);
    */
        topLF_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X, robotPos_Y+1, "topLF_S", this.robotDir);
        topRF_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X+1, robotPos_Y+1, "topRF_S", this.robotDir);
        topLR_L = new Sensor(Constants.RANGE_LONG_MIN, Constants.RANGE_LONG_MAX, robotPos_X, robotPos_Y+1, "topLR_L", DIRECTION.E);
        topRL_L = new Sensor(Constants.RANGE_LONG_MIN, Constants.RANGE_LONG_MAX, robotPos_X+1, robotPos_Y+1, "topRL_L", DIRECTION.W);
        bottomL_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X, robotPos_Y, "bottomL_S", DIRECTION.W);
        bottomR_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X+1, robotPos_Y, "bottomR_S", DIRECTION.E);
        
        
    
    }

    public void setRobotPos(int x, int y) {
        robotPos_X = x;
        robotPos_Y = y;
    }

    public void setDirection(DIRECTION dir){
    	this.robotDir = dir;
    }
    
    public void setSpeed(int speed){
    	this.speed = speed;
    }
    
    public void setSentors(){
    	//after adding sensor 
    	
    	switch(robotDir){
    	case N:
    		topLF_S.setSensor(this.robotPos_X, this.robotPos_Y+1, this.robotDir);
    		topRF_S.setSensor(this.robotPos_X+1, this.robotPos_Y+1, this.robotDir);
    		topLR_L.setSensor(this.robotPos_X, this.robotPos_Y+1, dirToRotate(MOVEMENT.L));
    		topRL_L.setSensor(this.robotPos_X+1, this.robotPos_Y+1, dirToRotate(MOVEMENT.R));
    		bottomL_S.setSensor(this.robotPos_X, this.robotPos_Y, dirToRotate(MOVEMENT.L));
    		bottomR_S.setSensor(this.robotPos_X+1, this.robotPos_Y+1, dirToRotate(MOVEMENT.R));
    		break;
    	case E:
    		topLF_S.setSensor(this.robotPos_X+1, this.robotPos_Y+1, this.robotDir);
    		topRF_S.setSensor(this.robotPos_X+1, this.robotPos_Y, this.robotDir);
    		topLR_L.setSensor(this.robotPos_X+1, this.robotPos_Y+1, dirToRotate(MOVEMENT.L));
    		topRL_L.setSensor(this.robotPos_X+1, this.robotPos_Y, dirToRotate(MOVEMENT.R));
    		bottomL_S.setSensor(this.robotPos_X, this.robotPos_Y+1, dirToRotate(MOVEMENT.L));
    		bottomR_S.setSensor(this.robotPos_X, this.robotPos_Y, dirToRotate(MOVEMENT.R));
    		break;
    	case W:
    		topLF_S.setSensor(this.robotPos_X-1, this.robotPos_Y, this.robotDir);
    		topRF_S.setSensor(this.robotPos_X-1, this.robotPos_Y+1, this.robotDir);
    		topLR_L.setSensor(this.robotPos_X-1, this.robotPos_Y, dirToRotate(MOVEMENT.L));
    		topRL_L.setSensor(this.robotPos_X-1, this.robotPos_Y+1, dirToRotate(MOVEMENT.R));
    		bottomL_S.setSensor(this.robotPos_X, this.robotPos_Y, dirToRotate(MOVEMENT.L));
    		bottomR_S.setSensor(this.robotPos_X, this.robotPos_Y+1, dirToRotate(MOVEMENT.R));
    		break;
    	case S:
    		topLF_S.setSensor(this.robotPos_X+1, this.robotPos_Y, this.robotDir);
    		topRF_S.setSensor(this.robotPos_X, this.robotPos_Y, this.robotDir);
    		topLR_L.setSensor(this.robotPos_X+1, this.robotPos_Y, dirToRotate(MOVEMENT.L));
    		topRL_L.setSensor(this.robotPos_X, this.robotPos_Y, dirToRotate(MOVEMENT.R));
    		bottomL_S.setSensor(this.robotPos_X+1, this.robotPos_Y+1, dirToRotate(MOVEMENT.L));
    		bottomR_S.setSensor(this.robotPos_X, this.robotPos_Y+1, dirToRotate(MOVEMENT.R));
    		break;
    	}
    	
    }
    
    public int getRobotPosX() {
        return robotPos_X;
    }

    public int getRobotPosY() {
        return robotPos_Y;
    }
    
    public DIRECTION getRobotDir(){
    	return robotDir;
    }

    public boolean getRealRobot() {
        return realRobot;
    }
    public boolean getReachedGoal() {
        return this.reachedGoal;
    }
    private void updateReachedGoal() {
        if (this.getRobotPosX() == Constants.GOAL_X && this.getRobotPosY() == Constants.GOAL_Y)
            this.reachedGoal = true;
    }
    
    public boolean isInStartZone(){
    	int x = this.robotPos_X;
    	int y = this.robotPos_Y;
    	
    	return (x<2 && x>=0 && y<2 && y>=0);
    			//(((x == 0 || (x == 1) )&& y == Constants.START_Y));    	
    }
    
    public void move(MOVEMENT m, int count, boolean toAndroid){		//add boolean send to android
    	//count >= 1, move multiple steps forward
    	if(!realRobot){
    		 // Emulate real movement by pausing execution.
            try {
                TimeUnit.MILLISECONDS.sleep(speed);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in Robot.move()!");
            }
    	}
    	
    	if(count > 1){ //move multiple steps 
    		CommunicationMgr comm = CommunicationMgr.getCommMgr();// <--set communication manager
    		if (count == 10) {
                comm.sendMsg("0", CommunicationMgr.BOT_INSTR);
            } else if (count < 10) {
                comm.sendMsg(Integer.toString(count), CommunicationMgr.BOT_INSTR);
            }
   		 	//add in comm manager part
    		
        }
    	
    	//System.out.print("Current robot direction : " + robotDir + "\n");
    	switch(m){
    	case F:
    		switch(robotDir){
    		case N: robotPos_Y += count; break;
    		case S: robotPos_Y -= count; break;
    		case E: robotPos_X += count; break;
    		case W: robotPos_X -= count; break;
    		default: break;
    		}break;
    	case B:
    		switch(robotDir){
    		case N: robotPos_Y -= count; break;
    		case S: robotPos_Y += count; break;
    		case E: robotPos_X -= count; break;
    		case W: robotPos_X += count; break;
    		default: break;
    		}break;
    	case L: 
    		
    		robotDir = dirToRotate(m); 
    		//System.out.println("Robot direction updated to : " + robotDir);
    		break;
    	case R: 
    		robotDir = dirToRotate(m);
    		//System.out.println("Robot direction updated to : " + robotDir);
    		break;
    	case CALIBRATE: break;
    	case ERROR: break;	//print error message? 
    	}
    	
    	if(realRobot) sendMovement(m, toAndroid);
    	//else 
    	//test
    	System.out.println("Move: " + m);
    	updateReachedGoal();
    }
    
    private DIRECTION dirToRotate(MOVEMENT m){ //if move right, rotate right, if left roatate left
    	if(m == MOVEMENT.R){
    		return DIRECTION.next(robotDir);	//directions in clockwise order	, rotate right
    	}else{
    		return DIRECTION.prev(robotDir);	//rotate left
    	}
    }
    
    /*overloaded function
     * to be added after android is connected
    public void move(MOVEMENT m){
    	this.move(m,true) <---- boolean android 
    }
    */
    
    /*
    public void multipleForward(int c){ //to move c steps forward (i think can merge with move function later) 
    	if(c == 1){
    		move(MOVEMENT.F);
    	}else{
    		//CommMgr comm = CommMgr.getCommMgr(); <--set communication manager
    		
    		 //add in comm manager part 
    		 
    	}
    	switch(robotDir){
    	case N: robotPos_Y += c; break;
    	case E:	robotPos_X += c; break;
    	case S: robotPos_Y -= c; break;
    	case W: robotPos_X -= c; break;
    	}
    	//commMgr send msg
    }
    */
    
    private void sendMovement(MOVEMENT m, boolean toAndroid){
    	//fill in after commMgr 
    	CommunicationMgr comm = CommunicationMgr.getCommMgr();
    	comm.sendMsg(MOVEMENT.print(m) + "", CommunicationMgr.BOT_INSTR);
        if (m != MOVEMENT.CALIBRATE && toAndroid) {
            comm.sendMsg(this.getRobotPosY() + "," + this.getRobotPosX() + "," + DIRECTION.print(this.getRobotDir()), CommunicationMgr.BOT_POS);
        }

    }
    
    public int[] senseDist(Map expMap, Map realMap){
    	int[] distance = new int[6];	//stores dist. of obstacles from each sensor
    	
    	if(!realRobot){
    		
    		distance[0] = topLF_S.distanceToObstacle(expMap, realMap);
    		//System.out.println(distance[0]);
    		distance[1] = topRF_S.distanceToObstacle(expMap, realMap);
    		//System.out.println(distance[1]);
    		distance[2] = topLR_L.distanceToObstacle(expMap, realMap);
    		//System.out.println(distance[2]);
    		distance[3] = topRL_L.distanceToObstacle(expMap, realMap);
    		//System.out.println(distance[3]);
    		distance[4] = bottomL_S.distanceToObstacle(expMap, realMap);
    		//System.out.println(distance[4]);
    		distance[5] = bottomR_S.distanceToObstacle(expMap, realMap);
    		//System.out.println(distance[5]);
    		
    	}else{
    		//comm mgr part
    		CommunicationMgr comm = CommunicationMgr.getCommMgr();
            String msg = comm.recvMsg();
            String[] msgArr = msg.split(";");
            
            if(msgArr[0].equals(CommunicationMgr.SENSOR_DATA)){
            	distance[0] = Integer.parseInt(msgArr[1].split("_")[1]);
            	distance[1] = Integer.parseInt(msgArr[2].split("_")[1]);
            	distance[2] = Integer.parseInt(msgArr[3].split("_")[1]);
            	distance[3] = Integer.parseInt(msgArr[4].split("_")[1]);
            	distance[4] = Integer.parseInt(msgArr[5].split("_")[1]);
            	distance[5] = Integer.parseInt(msgArr[6].split("_")[1]);

            }
          //set obstacles based on sensor values
        	topLF_S.findAndSetObstacleOnMap(expMap, distance[0]);
        	//System.out.println("Sensor 1 working");
        	topRF_S.findAndSetObstacleOnMap(expMap, distance[1]);
        	//System.out.println("Sensor 2 working");
        	topLR_L.findAndSetObstacleOnMap(expMap, distance[2]);
        	//System.out.println("Sensor 3 working");
        	topRL_L.findAndSetObstacleOnMap(expMap, distance[3]);
        	//System.out.println("Sensor 4 working");
        	bottomL_S.findAndSetObstacleOnMap(expMap, distance[4]);
        	//System.out.println("Sensor 5 working");
        	bottomR_S.findAndSetObstacleOnMap(expMap, distance[5]);
        	//System.out.println("Sensor 6 working");
        	
        	//send msg to commMgr
        	
        	String[] mapStrings = Map.generateMapDescriptor(expMap);
        	comm.sendMsg(mapStrings[0] + " " + mapStrings[1], CommunicationMgr.MAP_STRINGS);
        	
    	}
    	
    	
    	return distance;
    }
    
    
    }