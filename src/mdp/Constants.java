package mdp;

import java.awt.*;

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
	
	
	//GUI related constants
	public static final int CELL_SIZE = 30;
	public static final Color COLOR_START = Color.ORANGE;
	public static final Color COLOR_GOAL = 	Color.GREEN;
	public static final Color COLOR_UNEXP = Color.LIGHT_GRAY;
	public static final Color COLOR_OBS = Color.BLACK;
	public static final Color COLOR_FREE = Color.WHITE;
	public static final Color COLOR_ROBOT = Color.BLUE;
	
	public static final int OUTLINE = 2;
	
	public static final int MAPY = 600;
	public static final int MAPX_OFFSET = 120;
	
	public static final int ROBOTX_OFFSET = 10;
	public static final int ROBOTY_OFFSET = 20;
	
	public static final int ROBOT_H = 70;
	public static final int ROBOT_W = 70;
	
	public static final Color ROBOT_DIR = Color.WHITE;
	
	

}
