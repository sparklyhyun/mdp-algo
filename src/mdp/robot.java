package mdp;

import java.util.concurrent.TimeUnit; 
import mdp.CommunicationMgr.*;
import mdp.Constants.*;
import java.lang.*;
import mdp.Constants.*;

public class Robot {
	//private int size = Constants.ROBOT_SIZE;	
	//private int width = Constants.ROBOT_WIDTH;
	//private int startPos_X = Constants.START_X;
	//private int startPos_Y = Constants.START_Y;
	//private int goalPos_X = Constants.GOAL_X;
	//private int goalPos_Y = Constants.GOAL_Y;
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
    		//topLF_S.setSensor(this.robotPos_X-1, this.robotPos_Y+1, this.robotDir);
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
    			//(((x == 0 || (x == 1) )&& y == Constants.START_Y));    	
    }
    
    public void move(MOVEMENT m, int count, boolean toAndroid){		//add boolean send to android
    	//count >= 1, move multiple steps forward
    	if(!realRobot){
            try {
                TimeUnit.MILLISECONDS.sleep(speed);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in Robot.move()!");
            }
    	}
    	/*
    	if(count > 1){ //move multiple steps 
    		CommunicationMgr comm = CommunicationMgr.getCommMgr();// <--set communication manager
    		if (count == 10) {
                comm.sendMsg("0", CommunicationMgr.BOT_INSTR);
            } else if (count < 10) {
                comm.sendMsg(Integer.toString(count), CommunicationMgr.BOT_INSTR);
            }
   		 	//add in comm manager part or ignore 
    		
        }
    	*/
    	//System.out.print("Current robot direction : " + robotDir + "\n");
    	//System.out.println("Current robot position: " + robotPos_X + ", " + robotPos_Y);
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
    	
    	if(realRobot){
    		//sendMovement(m, toAndroid);
    		
    	} 
    	
    	//else 
    	//test
    	//System.out.print("Updated robot direction : " + robotDir + "\n");
    	//System.out.println("Updated robot position: " + robotPos_X + ", " + robotPos_Y);
    	
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
    
    /*
    private void sendMovement(MOVEMENT m, boolean toAndroid){
    	//fill in after commMgr 
    	CommunicationMgr comm = CommunicationMgr.getCommMgr();
    	
    	
    	comm.sendMsg(MOVEMENT.print(m) + "", CommunicationMgr.BOT_INSTR);
        if (m != MOVEMENT.CALIBRATE && toAndroid) {
        	//comm.sendMsg(this.getRobotPosY() + "," + this.getRobotPosX() + "," + DIRECTION.print(this.getRobotDir()), CommunicationMgr.BOT_POS);
        }

    }
    */
    public String sendData(MOVEMENT m){
    	//fill in after commMgr 
    	CommunicationMgr comm = CommunicationMgr.getCommMgr();
    	
    	String s = MOVEMENT.print(m)+"";
    	return s;
    }
    
    
    public int[] senseDist(Map expMap, Map realMap){
    	//System.out.println("sensedist entered");
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
           // System.out.println("msg split");
            //printarr(msg1);
            
            
            try{
           
        	for(int i = 0; i<msg1.length; i++){
        		//distance[i] =(int) Math.round( Float.parseFloat(msg1[i]));
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
            
        	//send msg to commMgr
        	
        	String[] mapStrings = Map.generateMapDescriptor(expMap);
        	comm.sendMsg(mapStrings[0] + " " + mapStrings[1], CommunicationMgr.MAP_STRINGS);
        	
    	}
    	
    	
    	return distance;
    }
    
    private void printarr(String[] s ){
    	for(int i=0; i<s.length; i++){
    		System.out.print("index " + i + ": " +s[i]);
    	}
    }
    
    
    }