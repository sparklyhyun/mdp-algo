package mdp;
import Coordinates;
import Map;
import Constants;
import robot;
import robot.DIRECTION;
import robot.MOVEMENT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class FastestPath {
    private ArrayList<Coordinates> nextVisit;        // array of Coordinates to be visited next
    private ArrayList<Coordinates> visited;        // array of visited Coordinates
    private HashMap<Coordinates, Coordinates> parents;    // HashMap of Child --> Parent
    private Coordinates current;                   // current Coordinates
    private Coordinates[] neighbors;               // array of neighbors of current Coordinates
    private DIRECTION curDir;               // current direction of robot
    private double[][] gCosts;              // array of real cost from START to [x-coordinate][y-coordinate] i.e. g(n)
    private Robot robot;
    private Map map;
    private final Map realMap;
    private int loopCount;
    private boolean explorationMode;        //indicate whether it is in exploration mode

    public FastestPathAlgo(Map map, Robot robot) {
        this.realMap = null;
        initObject(map, robot);
    }

    public FastestPathAlgo(Map map, Robot robot, Map realMap) {
        this.realMap = realMap;
        this.explorationMode = true;
        initObject(map, robot);
    }
  
    private void initObject(Map map, Robot robot) {
        this.robot = robot;
        this.map = map;
        this.nextVisit = new ArrayList<>();
        this.visited = new ArrayList<>();
        this.parents = new HashMap<>();
        this.neighbors = new Coordinates[4];
        this.current = getCoordinates(robot.getRobotPosX(), robot.getRobotPosY()); 
        this.curDir = robot.getRobotDir();
        this.gCosts = new double[Constants.MAX_X][Constants.MAX_Y];

        // Initialise gCosts array
        for (int i = 0; i < Constants.MAX_X; i++) {
            for (int j = 0; j < Constants.MAX_Y; j++) {
                Coordinates coordinates = getCoordinates(i, j);
                if (!canBeVisited(coordinates)) {
                    gCosts[i][j] = Constants.INFINITE_COST; // TO BE ADDED INTO THE CONSTANTS
                } else {
                    gCosts[i][j] = -1;
                }
            }
        }
        nextVisit.add(current);

        // Initialise starting point
        gCosts[robot.getRobotPosX()][robot.getRobotPosY()] = 0; 
        this.loopCount = 0;
    }  
    /**
     * Returns true if the cell can be visited.
     */
    private boolean canBeVisited(Coordinates c) {
        boolean checkIsExplored = c.getIsExplored();
        boolean checkIsObstacle = c.getIsObstacle()
        boolean checkIsVirtualWall = c.getIsVirtualWall();
        boolean canBeVisitedCheck = checkIsExplored && !checkIsObstacle && !checkIsVirtualWall
        return canBeVisitedCheck;
    }

    /**
     * Returns the Coordinates inside nextVisit with the minimum g(n) + h(n).
     */
    private Coordinates minimumCostCoordinates(int goalX, int getY) {
        int size = nextVisit.size();
        double minCost = Constants.INFINITE_COST;
        Coordinates result = null;

        for (int i = size - 1; i >= 0; i--) {
            double gCost = gCosts[(nextVisit.get(i).getX())][(nextVisit.get(i).getY())];
            double cost = gCost + costH(nextVisit.get(i), goalX, getY);
            if (cost < minCost) {
                minCost = cost;
                result = nextVisit.get(i);
            }
        }

        return result;
    }
    
    /**
     * Returns the heuristic cost i.e. h(n) from a given Coordinates to a given [goalX, goalY] in the maze.
     */
    private double costH(Coordinates c, int goalX, int goalY) {
        // Heuristic: The no. of moves will be equal to the difference in the x coordinate and y coordinate values.
        double movementCost = (Math.abs(goalX - c.getY()) + Math.abs(goalX - c.getX())) * Constants.MOVE_COST;

        if (movementCost == 0) return 0;

        // Heuristic: If c is not in the same X coordinate and Y coordinate, one turn will be needed.
        double turnCost = 0;
        if (goalY - c.getY() != 0 && goalX - c.getX() != 0) {
            turnCost = Constants.TURN_COST;
        }

        return movementCost + turnCost;
    }

    /**
     * Returns the target direction of the robot from [robotR, robotC] to target Coordinates.
     */
    private DIRECTION getTargetDir(int robotR, int robotC, DIRECTION robotDir, Coordinates target) {
        if (robotC - target.getY() > 0) {
            return DIRECTION.WEST;
        } else if (target.getY() - robotC > 0) {
            return DIRECTION.EAST;
        } else {
            if (robotR - target.getX() > 0) {
                return DIRECTION.SOUTH;
            } else if (target.getX() - robotR > 0) {
                return DIRECTION.NORTH;
            } else {
                return robotDir;
            }
        }
    }
    
    /**
     * Get the actual turning cost from one DIRECTION to another.
     */
    private double getTurnCost(DIRECTION a, DIRECTION b) {
        int numOfTurn = Math.abs(a.ordinal() - b.ordinal());
        if (numOfTurn > 2) {
            numOfTurn = numOfTurn % 2;
        }
        return (numOfTurn * Constants.TURN_COST);
    }

    /**
     * Calculate the actual cost of moving from Coordinates a to Coordinates b (assuming both are neighbors).
     */
    private double costG(Coordinates a, Coordinates b, DIRECTION aDir) {
        double moveCost = Constants.MOVE_COST; // one movement to neighbor

        double turnCost;
        DIRECTION targetDir = getTargetDir(a.getX(), a.getY(), aDir, b);
        turnCost = getTurnCost(aDir, targetDir);

        return moveCost + turnCost;
    }    
}
