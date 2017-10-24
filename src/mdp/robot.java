package mdp;

import java.util.concurrent.TimeUnit;
import mdp.CommunicationMgr.*;
import mdp.Constants.*;
import java.lang.*;
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
	/*
	private final Sensor topLF_S;
	private final Sensor topRF_S;
	private final Sensor topLR_L;	//top left facing right
	private final Sensor topRL_L;	//top right facing left
	private final Sensor bottomL_S;
	private final Sensor bottomR_S;
	*/
	
	/*
	private final Sensor topLF_S; //c
	private final Sensor topMF_L; //a
	private final Sensor topRF_S;  //e
	private final Sensor topMR_S;	//top middle facing right d
	//private final Sensor MidLL_S; //mid right facing right f
	private final Sensor MidLR_L; //mid right facing left b
	
	private final Sensor topMF_S;
*/
	
	private final Sensor a;
	private final Sensor b;
	private final Sensor c;
	private final Sensor d;
	private final Sensor e;
	private final Sensor f;
	 
	private DIRECTION robotDir = DIRECTION.N;	// can change later
	public static final int INFINITE_COST = 9999;
	public static final int VIRTUAL_COST = 1000;
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
        //2X2
        /*
        topLF_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X, robotPos_Y+1, "topLF_S", this.robotDir);
        topRF_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X+1, robotPos_Y+1, "topRF_S", this.robotDir);
        topLR_L = new Sensor(Constants.RANGE_LONG_MIN, Constants.RANGE_LONG_MAX, robotPos_X, robotPos_Y+1, "topLR_L", DIRECTION.E);
        topRL_L = new Sensor(Constants.RANGE_LONG_MIN, Constants.RANGE_LONG_MAX, robotPos_X+1, robotPos_Y+1, "topRL_L", DIRECTION.W);
        bottomL_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X, robotPos_Y, "bottomL_S", DIRECTION.W);
        bottomR_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X+1, robotPos_Y, "bottomR_S", DIRECTION.E);
        */
        //3X3
        
        /*
        topLF_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X-1, robotPos_Y+1, "topLF_S", this.robotDir);
        //topMF_L = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X, robotPos_Y+1, "topMF_L", this.robotDir);	//slightly to the left
        topMF_L = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X, robotPos_Y+1, "topMF_L", DIRECTION.W);
        
        topRF_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X+1, robotPos_Y+1, "topRF_S", this.robotDir);
        topMR_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_D_MAX, robotPos_X, robotPos_Y+1, "topMR_S", DIRECTION.E);
        //MidLL_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X-1, robotPos_Y, "MidLL_S", DIRECTION.W);
        topMF_S = new Sensor(Constants.RANGE_SHORT_MIN, Constants.RANGE_SHORT_MAX, robotPos_X, robotPos_Y+1, "MidLL_S", this.robotDir);
        
        MidLR_L = new Sensor(Constants.RANGE_LONG_MIN, Constants.RANGE_B_MAX, robotPos_X-1, robotPos_Y, "MidLR_L", DIRECTION.E);
        */
        
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
    	//after adding sensor 
    	
    	//2X2
    	/*
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
    	}*/
    	
    	//3X3
    	
    	//System.out.println("set sensors entered");
    	/*
    	switch(robotDir){
    	case N:
    		topLF_S.setSensor(this.robotPos_X-1, this.robotPos_Y+1, this.robotDir);
    		//topMF_L.setSensor(this.robotPos_X, this.robotPos_Y+1, this.robotDir);
    		topMF_L.setSensor(this.robotPos_X, this.robotPos_Y+1,dirToRotate(MOVEMENT.L));
    		
    		topRF_S.setSensor(this.robotPos_X+1, this.robotPos_Y+1, this.robotDir);
    		topMR_S.setSensor(this.robotPos_X, this.robotPos_Y+1, dirToRotate(MOVEMENT.R));
    		//MidLL_S.setSensor(this.robotPos_X-1, this.robotPos_Y, dirToRotate(MOVEMENT.L));
    		topMF_S.setSensor(this.robotPos_X, this.robotPos_Y+1, this.robotDir);
    		
    		MidLR_L.setSensor(this.robotPos_X-1, this.robotPos_Y, dirToRotate(MOVEMENT.R));
    		break;
    	case E:
    		topLF_S.setSensor(this.robotPos_X+1, this.robotPos_Y+1, this.robotDir);
    		//topMF_L.setSensor(this.robotPos_X+1, this.robotPos_Y, this.robotDir);
    		topMF_L.setSensor(this.robotPos_X+1, this.robotPos_Y+1, dirToRotate(MOVEMENT.L));
    		
    		topRF_S.setSensor(this.robotPos_X+1, this.robotPos_Y-1, this.robotDir);
    		topMR_S.setSensor(this.robotPos_X+1, this.robotPos_Y, dirToRotate(MOVEMENT.R));
    		//MidLL_S.setSensor(this.robotPos_X, this.robotPos_Y+1, dirToRotate(MOVEMENT.L));
    		topMF_S.setSensor(this.robotPos_X+1, this.robotPos_Y, this.robotDir);
    		
    		MidLR_L.setSensor(this.robotPos_X, this.robotPos_Y+1, dirToRotate(MOVEMENT.R));
    		break;
    	case W:
    		topLF_S.setSensor(this.robotPos_X-1, this.robotPos_Y+1, this.robotDir);
    		//topMF_L.setSensor(this.robotPos_X-1, this.robotPos_Y, this.robotDir);
    		topMF_L.setSensor(this.robotPos_X-1, this.robotPos_Y, dirToRotate(MOVEMENT.L));
    		
    		topRF_S.setSensor(this.robotPos_X-1, this.robotPos_Y-1, this.robotDir);
    		topMR_S.setSensor(this.robotPos_X-1, this.robotPos_Y, dirToRotate(MOVEMENT.R));
    		//MidLL_S.setSensor(this.robotPos_X, this.robotPos_Y-1, dirToRotate(MOVEMENT.L));
    		topMF_S.setSensor(this.robotPos_X-1, this.robotPos_Y, this.robotDir);
    		
    		MidLR_L.setSensor(this.robotPos_X, this.robotPos_Y-1, dirToRotate(MOVEMENT.R));
    		break;
    	case S:
    		topLF_S.setSensor(this.robotPos_X+1, this.robotPos_Y-1, this.robotDir);
    		//topMF_L.setSensor(this.robotPos_X, this.robotPos_Y-1, this.robotDir);
    		topMF_L.setSensor(this.robotPos_X, this.robotPos_Y-1, dirToRotate(MOVEMENT.L));
    		
    		topRF_S.setSensor(this.robotPos_X-1, this.robotPos_Y-1, this.robotDir);
    		topMR_S.setSensor(this.robotPos_X, this.robotPos_Y-1, dirToRotate(MOVEMENT.R));
    		//MidLL_S.setSensor(this.robotPos_X+1, this.robotPos_Y, dirToRotate(MOVEMENT.L));
    		topMF_S.setSensor(this.robotPos_X, this.robotPos_Y-1, this.robotDir);
    		
    		MidLR_L.setSensor(this.robotPos_X+1, this.robotPos_Y, dirToRotate(MOVEMENT.R));
    		break;
    	}*/
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
   		 	//add in comm manager part or ignore 
    		
        }
    	
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
        	//comm.sendMsg(this.getRobotPosY() + "," + this.getRobotPosX() + "," + DIRECTION.print(this.getRobotDir()), CommunicationMgr.BOT_POS);
        }

    }
    
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
    		/*
    		distance[0] = topLF_S.distanceToObstacle(expMap, realMap);
    		//System.out.println(distance[0]);
    		distance[1] = topMF_L.distanceToObstacle(expMap, realMap);
    		//System.out.println(distance[1]);
    		distance[2] = topRF_S.distanceToObstacle(expMap, realMap);
    		//System.out.println(distance[2]);
    		distance[3] = topMR_S.distanceToObstacle(expMap, realMap);
    		//System.out.println(distance[3]);
    		//distance[4] = MidLL_S.distanceToObstacle(expMap, realMap);
    		distance[4] = topMF_S.distanceToObstacle(expMap, realMap);
    		
    		//System.out.println(distance[4]);
    		distance[5] = MidLR_L.distanceToObstacle(expMap, realMap);
    		//System.out.println(distance[5]);
    		*/
    		distance[0] = a.distanceToObstacle(expMap, realMap);
    		distance[1] = b.distanceToObstacle(expMap, realMap);
    		distance[2] = c.distanceToObstacle(expMap, realMap);
    		distance[3] = d.distanceToObstacle(expMap, realMap);
    		distance[4] = e.distanceToObstacle(expMap, realMap);
    		distance[5] = f.distanceToObstacle(expMap, realMap);
    		
    	}else{
    		//comm mgr part
    		//need to send request? 
    		
    		CommunicationMgr comm = CommunicationMgr.getCommMgr();
            String msg = comm.recvMsg();	//sensor data
            
            System.out.println("sensor data received");
            
    		
            /*
            String[] msgArr = msg.split(";");
            
            if(msgArr[0].equals(CommunicationMgr.SENSOR_DATA)){
            	distance[0] = Integer.parseInt(msgArr[1].split("_")[1]);	//a
            	distance[1] = Integer.parseInt(msgArr[2].split("_")[1]);	//b
            	distance[2] = Integer.parseInt(msgArr[3].split("_")[1]);	//c
            	distance[3] = Integer.parseInt(msgArr[4].split("_")[1]);	//d
            	distance[4] = Integer.parseInt(msgArr[5].split("_")[1]);	//e
            	distance[5] = Integer.parseInt(msgArr[6].split("_")[1]);	//f
            	
            }
            */
            
            String msg1[] = msg.split(";");
           // System.out.println("msg split");
            //printarr(msg1);
            
            
            try{
            /*
            distance[0] = Integer.parseInt(msg1[0]);	//a
        	distance[1] = Integer.parseInt( msg1[1]);	//b
        	distance[2] = Integer.parseInt(msg1[2]);	//c
        	distance[3] = Integer.parseInt(msg1[3]);	//d
        	distance[4] = Integer.parseInt(msg1[4]);	//e
        	System.out.println("dist split until e");
        	distance[5] = Integer.parseInt(msg1[5]);	//f
            
        	System.out.println("distance split" );
        	*/
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
            
            
            //printarr(distance);
          //set obstacles based on sensor values
            /*
        	topLF_S.findAndSetObstacleOnMap(expMap, distance[2]+10);
        	//System.out.println("Sensor 1 working");
        	topMF_L.findAndSetObstacleOnMap(expMap, distance[4]+10);	//e
        	//System.out.println("Sensor 2 working");
        	topRF_S.findAndSetObstacleOnMap(expMap, distance[0]+10);
        	//System.out.println("Sensor 3 working");
        	topMR_S.findAndSetObstacleOnMap(expMap, distance[3]+20);
        	//System.out.println("Sensor 4 working");
        	//MidLL_S.findAndSetObstacleOnMap(expMap, distance[5]);
        	//System.out.println("Sensor 5 working");
        	topMF_S.findAndSetObstacleOnMap(expMap, distance[5]+10);
        	
        	MidLR_L.findAndSetObstacleOnMap(expMap, distance[1]+30);
        	//System.out.println("Sensor 6 working");
        	*/
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