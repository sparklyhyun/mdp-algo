package mdp;

public class robot {
	int size = Constants.ROBOT_SIZE;	
	int width = Constants.ROBOT_WIDTH;
	int startPos_X = Constants.START_X;
	int startPos_Y = Constants.START_Y;
	int goalPos_X = Constants.GOAL_X;
	int goalPos_Y = Constants.GOAL_Y;
	int robotPos_X;
	int robotPos_Y;
	
	private boolean reachedGoal;	
	private final boolean realRobot;  //use when connected to the robot
	

    public robot(int x, int y, boolean realRobot) {
        robotPos_X = x;
        robotPos_Y = y;
        this.realRobot = realRobot;
    }

    public void setRobotPos(int x, int y) {
        robotPos_X = x;
        robotPos_Y = y;
    }

    public int getRobotPosX() {
        return robotPos_X;
    }

    public int getRobotPosY() {
        return robotPos_Y;
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
    }
