package mdp;

import java.util.concurrent.TimeUnit; 
import mdp.CommunicationMgr.*;
import mdp.Constants.*;
import java.lang.*;
import mdp.Constants.*;

public class Robot {
	public int robotPos_X;
	public int robotPos_Y; 
	private int speed;
	
	private final Sensor a;
	private final Sensor b;
	private final Sensor c;
	private final Sensor d;
	private final Sensor e;
	private final Sensor f;
	 
	private DIRECTION robotDir = DIRECTION.N;	// can change later
	
	
	private boolean reachedGoal;	
	private final boolean realRobot;  //use when connected to the robot
	

    public Robot(int x, int y, boolean realRobot) {
        robotPos_X = x;
        robotPos_Y = y;
        speed = Constants.ROBOT_SPEED;
        this.realRobot = realRobot;

        a = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X-1, robotPos_Y+1, "a", this.robotDir);
        b = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X, robotPos_Y+1, "b", this.robotDir);
        c = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X+1, robotPos_Y+1, "c", this.robotDir);
        d = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_D_MAX, robotPos_X, robotPos_Y+1, "d", DIRECTION.W);
        e = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X, robotPos_Y+1, "e", DIRECTION.E);
        f = new Sensor(Constants.RANGE_LONG_MIN, Constants.RANGE_LONG_MAX, robotPos_X-1, robotPos_Y, "f", DIRECTION.E);
        
        
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
    	
    	switch(robotDir){
    	case N:
    		a.setSensor(this.robotPos_X-1, this.robotPos_Y+1, this.robotDir);
    		b.setSensor(this.robotPos_X, this.robotPos_Y+1, this.robotDir);
    		c.setSensor(this.robotPos_X+1, this.robotPos_Y+1, this.robotDir);
    		d.setSensor(this.robotPos_X, this.robotPos_Y+1, dirToRotate(MOVEMENT.L));
    		e.setSensor(this.robotPos_X, this.robotPos_Y+1, dirToRotate(MOVEMENT.R));
    		f.setSensor(this.robotPos_X-1, this.robotPos_Y, dirToRotate(MOVEMENT.R));
    		break;
    	case E:
    		a.setSensor(this.robotPos_X+1, this.robotPos_Y+1, this.robotDir);
    		b.setSensor(this.robotPos_X+1, this.robotPos_Y, this.robotDir);
    		c.setSensor(this.robotPos_X+1, this.robotPos_Y-1, this.robotDir);
    		d.setSensor(this.robotPos_X+1, this.robotPos_Y, dirToRotate(MOVEMENT.L));
    		e.setSensor(this.robotPos_X+1, this.robotPos_Y, dirToRotate(MOVEMENT.R));
    		f.setSensor(this.robotPos_X, this.robotPos_Y+1, dirToRotate(MOVEMENT.R));
    		break;
    	case W:
    		a.setSensor(this.robotPos_X-1, this.robotPos_Y-1, this.robotDir);
    		b.setSensor(this.robotPos_X-1, this.robotPos_Y, this.robotDir);
    		c.setSensor(this.robotPos_X-1, this.robotPos_Y+1, this.robotDir);
    		d.setSensor(this.robotPos_X-1, this.robotPos_Y, dirToRotate(MOVEMENT.L));
    		e.setSensor(this.robotPos_X-1, this.robotPos_Y, dirToRotate(MOVEMENT.R));
    		f.setSensor(this.robotPos_X, this.robotPos_Y-1, dirToRotate(MOVEMENT.R));
    		break;
    	case S:
    		a.setSensor(this.robotPos_X+1, this.robotPos_Y-1, this.robotDir);
    		b.setSensor(this.robotPos_X, this.robotPos_Y-1, this.robotDir);
    		c.setSensor(this.robotPos_X-1, this.robotPos_Y-1, this.robotDir);
    		d.setSensor(this.robotPos_X, this.robotPos_Y-1, dirToRotate(MOVEMENT.L));
    		e.setSensor(this.robotPos_X, this.robotPos_Y-1, dirToRotate(MOVEMENT.R));
    		f.setSensor(this.robotPos_X+1, this.robotPos_Y, dirToRotate(MOVEMENT.R));
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
    }
    
    public void move(MOVEMENT m, int count, boolean toAndroid){		//add boolean send to android
    	if(!realRobot){
            try {
                TimeUnit.MILLISECONDS.sleep(speed);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in Robot.move()!");
            }
    	}

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
    		break;
    	case R: 
    		robotDir = dirToRotate(m);
    		break;
    	case CALIBRATE: break;
    	case ERROR: break;	//print error message? 
    	}
    	
    	
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
    
   
    public String sendData(MOVEMENT m){
    	CommunicationMgr comm = CommunicationMgr.getCommMgr();
    	
    	String s = MOVEMENT.print(m)+"";
    	return s;
    }
    
    
    public int[] senseDist(Map expMap, Map realMap){
    	int[] distance = new int[6];	//stores dist. of obstacles from each sensor
    	
    	if(!realRobot){
    	
    		distance[0] = a.distanceToObstacle(expMap, realMap);
    		distance[1] = b.distanceToObstacle(expMap, realMap);
    		distance[2] = c.distanceToObstacle(expMap, realMap);
    		distance[3] = d.distanceToObstacle(expMap, realMap);
    		distance[4] = e.distanceToObstacle(expMap, realMap);
    		distance[5] = f.distanceToObstacle(expMap, realMap);
    		
    	}else{
    		
    		CommunicationMgr comm = CommunicationMgr.getCommMgr();
            String msg = comm.recvMsg();	//sensor data
            
            System.out.println("sensor data received");
            
    		if(msg.equals("K")) {
    			++Constants.count2;
    			return distance;
    		}
          
            
            String msg1[] = msg.split(";");
            
            try{
           
        	for(int i = 0; i<msg1.length; i++){
        		distance[i] = new Double(msg1[i]).intValue();
        	}
            	
      
            }catch(Exception e){
            	System.out.println(e.getMessage());
            }
            
            for(int i=0; i<distance.length ; i++){
            	System.out.print("distance " + i + ": " + distance[i] + ", ");
            }
            
       
            a.findAndSetObstacleOnMap(expMap, distance[0]+Constants.PAD_A);
            b.findAndSetObstacleOnMap(expMap, distance[1]+Constants.PAD_B);
            c.findAndSetObstacleOnMap(expMap, distance[2]+Constants.PAD_C);
            d.findAndSetObstacleOnMap(expMap, distance[3]+Constants.PAD_D);
            e.findAndSetObstacleOnMap(expMap, distance[4]+Constants.PAD_E);
            f.findAndSetObstacleOnMap(expMap, distance[5]+Constants.PAD_F);
        	
        	String[] mapStrings = Map.generateMapDescriptor(expMap);
        	comm.sendMsg(mapStrings[0] + " " + mapStrings[1], CommunicationMgr.MAP_STRINGS);
        	
    	}
    	
    	
    	return distance;
    }

    
    }