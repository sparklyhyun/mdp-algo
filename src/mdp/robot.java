package mdp;

import java.util.concurrent.TimeUnit;

import mdp.Constants.*;

public class Robot {
	private int size = Constants.ROBOT_SIZE;	
	private int width = Constants.ROBOT_WIDTH;
	private int startPos_X = Constants.START_X;
	private int startPos_Y = Constants.START_Y;
	private int goalPos_X = Constants.GOAL_X;
	private int goalPos_Y = Constants.GOAL_Y;
	private int robotPos_X = Constants.START_X;
	private int robotPos_Y = Constants.START_Y; //initial position	
	private int speed;
	
	// change the sensor position accordingly
	private final Sensor topL_L;
	private final Sensor midL_S;
	private final Sensor bottomL_S;
	private final Sensor topR_S;
	private final Sensor midR_S;
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
        topL_L = new Sensor(Constants.RANGE_LONG_MIN, Constants.RANGE_LONG_MAX, robotPos_X-1, robotPos_Y+1, "topL_L", this.robotDir);
        midL_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X-1, robotPos_Y, "midL_S", DIRECTION.W);
        bottomL_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X-1, robotPos_Y-1, "bottomL_S", DIRECTION.W);
        topR_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X+1, robotPos_Y+1, "topR_S", DIRECTION.E);
        midR_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X+1, robotPos_Y, "midR_S", DIRECTION.E);
        bottomR_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X+1, robotPos_Y-1, "bottomR_S", DIRECTION.E);
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
    		topL_L.setSensor(this.robotPos_X-1, this.robotPos_Y+1, this.robotDir);
    		midL_S.setSensor(this.robotPos_X-1, this.robotPos_Y, dirToRotate(MOVEMENT.L));
    		bottomL_S.setSensor(this.robotPos_X-1, this.robotPos_Y-1, dirToRotate(MOVEMENT.L));
    		topR_S.setSensor(this.robotPos_X+1, this.robotPos_Y+1, dirToRotate(MOVEMENT.R));
    		midR_S.setSensor(this.robotPos_X+1, this.robotPos_Y, dirToRotate(MOVEMENT.R));
    		bottomR_S.setSensor(this.robotPos_X+1, this.robotPos_Y-1, dirToRotate(MOVEMENT.R));
    	case E:
    		topL_L.setSensor(this.robotPos_X+1, this.robotPos_Y+1, this.robotDir);
    		midL_S.setSensor(this.robotPos_X, this.robotPos_Y+1, dirToRotate(MOVEMENT.L));
    		bottomL_S.setSensor(this.robotPos_X-1, this.robotPos_Y+1, dirToRotate(MOVEMENT.L));
    		topR_S.setSensor(this.robotPos_X+1, this.robotPos_Y-1, dirToRotate(MOVEMENT.R));
    		midR_S.setSensor(this.robotPos_X, this.robotPos_Y-1, dirToRotate(MOVEMENT.R));
    		bottomR_S.setSensor(this.robotPos_X-1, this.robotPos_Y-1, dirToRotate(MOVEMENT.R));
    	case W:
    		topL_L.setSensor(this.robotPos_X-1, this.robotPos_Y-1, this.robotDir);
    		midL_S.setSensor(this.robotPos_X, this.robotPos_Y-1, dirToRotate(MOVEMENT.L));
    		bottomL_S.setSensor(this.robotPos_X+1, this.robotPos_Y-1, dirToRotate(MOVEMENT.L));
    		topR_S.setSensor(this.robotPos_X-1, this.robotPos_Y+1, dirToRotate(MOVEMENT.R));
    		midR_S.setSensor(this.robotPos_X, this.robotPos_Y+1, dirToRotate(MOVEMENT.R));
    		bottomR_S.setSensor(this.robotPos_X+1, this.robotPos_Y+1, dirToRotate(MOVEMENT.R));
    	case S:
    		topL_L.setSensor(this.robotPos_X+1, this.robotPos_Y-1, this.robotDir);
    		midL_S.setSensor(this.robotPos_X+1, this.robotPos_Y, dirToRotate(MOVEMENT.L));
    		bottomL_S.setSensor(this.robotPos_X+1, this.robotPos_Y+1, dirToRotate(MOVEMENT.L));
    		topR_S.setSensor(this.robotPos_X-1, this.robotPos_Y-1, dirToRotate(MOVEMENT.R));
    		midR_S.setSensor(this.robotPos_X-1, this.robotPos_Y, dirToRotate(MOVEMENT.R));
    		bottomR_S.setSensor(this.robotPos_X-1, this.robotPos_Y+1, dirToRotate(MOVEMENT.R));
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
    		//CommMgr comm = CommMgr.getCommMgr(); <--set communication manager
   		 	//add in comm manager part
    	}
    	
    	switch(m){
    	case F:
    		switch(robotDir){
    		case N : robotPos_Y += count; break;
    		case S: robotPos_Y -= count; break;
    		case E: robotPos_X += count; break;
    		case W: robotPos_X -= count; break;
    		default: break;
    		}break;
    	case B:
    		switch(robotDir){
    		case N : robotPos_Y -= count; break;
    		case S: robotPos_Y += count; break;
    		case E: robotPos_X -= count; break;
    		case W: robotPos_X += count; break;
    		default: break;
    		}break;
    	case L: robotDir = dirToRotate(m); break;
    	case R: robotDir = dirToRotate(m); break;
    	case CALIBRATE: break;
    	case ERROR: break;	//print error message? 
    	}
    	
    	//if(realRobot) sendMovement(m, toAndroid); <----------- need to change later
    	
    	//else 
    	//test
    	System.out.println("Move: " + m);
    	updateReachedGoal();
    }
    
    private DIRECTION dirToRotate(MOVEMENT m){ //if move right, rotate right, if left roatate left
    	if(m == MOVEMENT.R){
    		System.out.println("rotate R");
    		return DIRECTION.next(robotDir);	//directions in clockwise order	, rotate right
    	}else{
    		System.out.print("rotate L");
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
    }
    
    public int[] senseDist(Map expMap, Map realMap){
    	int[] distance = new int[6];	//stores dist. of obstacles from each sensor
    	if(!realRobot){
    		
    		distance[0] = topL_L.distanceToObstacle(expMap, realMap);
    		distance[1] = midL_S.distanceToObstacle(expMap, realMap);
    		distance[2] = bottomL_S.distanceToObstacle(expMap, realMap);
    		distance[3] = topR_S.distanceToObstacle(expMap, realMap);
    		distance[4] = midR_S.distanceToObstacle(expMap, realMap);
    		distance[5] = bottomR_S.distanceToObstacle(expMap, realMap);
    		
    	}else{
    		//CommMgr comm = new CommMgr();
    		/*
    		 * comm manager part
    		 */
    	}
    	//set obstacles based on sensor values
    	//sensor1.findAndSetObstacleOnMap(expMap, distance[0]);
    	//sensor2.findAndSetObstacleOnMap(expMap, distance[0]);
    	//sensor3.findAndSetObstacleOnMap(expMap, distance[0]);
    	//sensor4.findAndSetObstacleOnMap(expMap, distance[0]);
    	//sensor5.findAndSetObstacleOnMap(expMap, distance[0]);
    	//sensor6.findAndSetObstacleOnMap(expMap, distance[0]);
    	
    	//send msg to commMgr
    	
    	return distance;
    }
    
    
    }
