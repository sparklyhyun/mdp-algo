package mdp;

import java.awt.*;

public class Constants {
	public static int rightTurn = 0;
	
	//map related constants
	public static final int MAX_X = 15;
	public static final int MAX_Y = 20;
	public static final int MAP_SIZE = 300;
	
	public static final int START_X = 1; 
	public static final int START_Y = 1;
	public static final int GOAL_X = 13;
	public static final int GOAL_Y = 18;
	
	
	//robot related constants
	public static final int ROBOT_WIDTH = 3;
	public static final int ROBOT_SIZE = 9;
	public static final int ROBOT_SPEED = 5; //need to change 
	
	public static final int RANGE_LONG_MIN = 0;
	public static final int RANGE_LONG_MAX = 15;
	public static final int RANGE_SHORT_MIN = 0;
	public static final int RANGE_SHORT_MAX = 8;
	
	public enum DIRECTION{N,E,S,W;	//clockwise direction
		
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
    public enum MOVEMENT {
        F, B, R, L, CALIBRATE, ERROR;

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
                case CALIBRATE:
                    return 'C';
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
