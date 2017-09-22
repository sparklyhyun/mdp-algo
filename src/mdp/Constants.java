package mdp;

public class Constants {
	//map related constants
	public static final int MAX_X = 15;
	public static final int MAX_Y = 20;
	public static final int MAP_SIZE = 300;
	
	public static int START_X = 1; 
	public static int START_Y = 1;
	public static final int GOAL_X = 14;
	public static final int GOAL_Y = 19;
	
	
	//robot related constants
	public static final int ROBOT_WIDTH = 3;
	public static final int ROBOT_SIZE = 9;
	public static final int ROBOT_SPEED = 5; //need to change 
	
	public static final int RANGE_LONG_MIN = 2;
	public static final int RANGE_LONG_MAX = 15;
	public static final int RANGE_SHORT_MIN = 1;
	public static final int RANGE_SHORT_MAX = 8;
	
	public enum DIRECTION{N,E,S,W;	//clockwise direction
		
		public static DIRECTION next(DIRECTION currentDir){	//get next direction
			return values()[(currentDir.ordinal()+1) % values().length];
		}
		
		public static DIRECTION prev(DIRECTION currentDir){	//get previous direction
			return values()[((currentDir.ordinal()-1)+values().length) % values().length ];
		}
	};	
	public enum MOVEMENT{F,B,R,L,CALIBRATE, ERROR};

}
