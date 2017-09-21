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
}
