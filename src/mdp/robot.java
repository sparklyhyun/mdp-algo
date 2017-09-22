package mdp;

import java.util.concurrent.TimeUnit;

import mdp.Constants.*;

public class Robot {
	int size = Constants.ROBOT_SIZE;	
	int width = Constants.ROBOT_WIDTH;
	int startPos_X = Constants.START_X;
	int startPos_Y = Constants.START_Y;
	int goalPos_X = Constants.GOAL_X;
	int goalPos_Y = Constants.GOAL_Y;
	int robotPos_X;
	int robotPos_Y;	
	
	/*
	private final Sensor sensora;
	private final Sensor sensorb;
	private final Sensor sensorc;
	private final Sensor sensord;
	private final Sensor sensore;
	private final Sensor sensorf;
	 */
	private DIRECTION robotDir = DIRECTION.N;	// can change later
	public static final int INFINITE_COST = 9999;
	public static final int MOVE_COST = 10;                         // cost of FORWARD, BACKWARD movement
	public static final int TURN_COST = 20;                         // cost of RIGHT, LEFT movement
	
	private boolean reachedGoal;	
	private final boolean realRobot;  //use when connected to the robot
	

    public Robot(int x, int y, boolean realRobot) {
        robotPos_X = x;
        robotPos_Y = y;
        this.realRobot = realRobot;
    }

    public void setRobotPos(int x, int y) {
        robotPos_X = x;
        robotPos_Y = y;
    }

    public void setDirection(DIRECTION dir){
    	this.robotDir = dir;
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
    
    public void move(MOVEMENT m){		//add boolean send to android
    	if(!realRobot){
    		 // Emulate real movement by pausing execution.
            try {
                TimeUnit.MILLISECONDS.sleep(speed);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in Robot.move()!");
            }
    	}
    	
    }
    }
