package mdp;

import java.awt.*; 
import java.util.ArrayList;

public class Constants {
	/**/
	
	//contstants for exploration 
	public static int rightTurn = 0;
	public static int rightTurn2 = 0;
	public static int front = 0;
	public static int front2 = 0;
	public static int count2 = 1;
	
	//map related constants
	public static final int MAX_X = 15;
	public static final int MAX_Y = 20;
	public static final int MAP_SIZE = 300;
	
	public static final int START_X = 1; 
	public static final int START_Y = 1;
	public static final int GOAL_X = 13; //13;
	public static final int GOAL_Y = 18;//18;
	
	//for fastest path 
	public static String fp = "";
	public static ArrayList<MOVEMENT> combinedFP;
	
	public static final int INFINITE_COST = 9999;
	public static final int VIRTUAL_COST = 9999;
	public static final int MOVE_COST = 10;                         // cost of FORWARD, BACKWARD movement
	public static final int TURN_COST = 20;                        // cost of RIGHT, LEFT movement

	//robot related constants
	public static final int ROBOT_SPEED = 5; 
	
	public static final int RANGE_LONG_MIN = 1;
	public static final int RANGE_LONG_MAX = 6;
	public static final int RANGE_SHORT_MIN = 1;
	public static final int RANGE_SHORT_MAX = 3;
	
	//public static final int RANGE_B_MAX = 7;
	public static final int RANGE_D_MAX = 3; 

	//offsets applied to sensor data (for sensors a,b,c,e,d,f) to make it more accurate 
	public static final int PAD_A = 10;
	public static final int PAD_B = 10;
	public static final int PAD_C = 10;
	public static final int PAD_D = 10;
	public static final int PAD_E = 20; 
	public static final int PAD_F = 30;
	
	
	public enum DIRECTION{N,E,S,W;	// absolute direction of the robot (in clockwise direction)
		
		public static DIRECTION next(DIRECTION currentDir){	//get next direction
			return values()[(currentDir.ordinal()+1) % values().length];
		}
		
		public static DIRECTION prev(DIRECTION currentDir){	//get previous direction
			return values()[((currentDir.ordinal()-1)+values().length) % values().length ];
		}
		
		public static char print(DIRECTION d) {
            switch (d) {
                case N:
                    return 'N';
                case E:
                    return 'E';
                case S:
                    return 'S';
                case W:
                    return 'W';
                default:
                    return 'X';
            }
		}
		
		
	};	
    public enum MOVEMENT {	//for movement of robot 
        F, B, R, L, CALIBRATE, CALIBRATEL, CALIBRATES , ERROR, U;

        public static char print(MOVEMENT m) {
            switch (m) {
                case F:
                    return 'F';
                case B:
                    return 'B';
                case R:
                    return 'R';
                case L:
                    return 'L';
                case U:
                    return 'U';
                case CALIBRATE:
                    return 'C';
                case CALIBRATEL: 
                	return 'J';
                case CALIBRATES: 
                	return 'S';
                case ERROR:
                default:
                    return 'E';
            }
        }
    }
	
	
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
	
	public static final int ROBOT_H_DIR = 10;
	public static final int ROBOT_W_DIR = 10;
	
	public static final Color ROBOT_DIR = Color.WHITE;
	
	

}
