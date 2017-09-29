package mdp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import mdp.Constants.*;

public class FastestPath {
    private ArrayList<Coordinates> nextVisit;        // array of Coordinates to be visited next
    private ArrayList<Coordinates> visited;        // array of visited Coordinates
    private HashMap<Coordinates, Coordinates> parents;    // HashMap of Child --> Parent
    private Coordinates current;                   // current Coordinates
    private Coordinates[] neighbors;               // array of neighbors of current Coordinates
    private DIRECTION curDir;               // current direction of robot
    private double[][] gCosts;              // array of real cost from START to [y-coordinate][x-coordinate] i.e. g(n)
    private Robot robot;
    private Map map;
    private Map realMap;
    private int loopCount;
    private boolean explorationMode;        //indicate whether it is in exploration mode

    public FastestPath(Map map, Robot robot) {
        this.realMap = null;
        initObject(map, robot);
    }

    public FastestPath(Map map, Robot robot, Map realMap) {
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
        this.current = map.getCoordinate(robot.getRobotPosY(), robot.getRobotPosX()); 
        this.curDir = robot.getRobotDir();
        this.gCosts = new double[Constants.MAX_Y][Constants.MAX_X];

        // Initialize gCosts array
        for (int i = 0; i < Constants.MAX_Y; i++) {
            for (int j = 0; j < Constants.MAX_X; j++) {
                Coordinates coordinates = map.getCoordinate(i, j);
                if (!canBeVisited(coordinates)) {
                    gCosts[i][j] = robot.INFINITE_COST; // TO BE ADDED INTO THE CONSTANTS
                } else {
                    gCosts[i][j] = -1;
                }
            }
        }
        nextVisit.add(current);

        // Initialize starting point
        gCosts[robot.getRobotPosY()][robot.getRobotPosX()] = 0; 
        this.loopCount = 0;
    }  
    /**
     * Returns true if the coordinates can be visited.
     */
    private boolean canBeVisited(Coordinates c) {
        boolean checkIsExplored = c.getIsExplored();
        boolean checkIsObstacle = c.getIsObstacle();
        boolean checkIsVirtualWall = c.getIsVirtualWall();
        boolean canBeVisitedCheck = checkIsExplored && !checkIsObstacle && !checkIsVirtualWall;
        return canBeVisitedCheck;
    }

    /**
     * Returns the Coordinates inside nextVisit with the minimum g(n) + h(n).
     */
    private Coordinates minimumCostCoordinates(int goalY, int goalX) {
        int size = nextVisit.size();
        double minCost = robot.INFINITE_COST;
        Coordinates result = null;

        for (int i = size - 1; i >= 0; i--) {
            double gCost = gCosts[(nextVisit.get(i).getY())][(nextVisit.get(i).getX())];
            double cost = gCost + costH(nextVisit.get(i), goalY, goalX);
            if (cost < minCost) {
                minCost = cost;
                result = nextVisit.get(i);
            }
        }

        return result;
    }
    
    /**
     * Returns the heuristic cost i.e. h(n) from a given Coordinates to a given [goalY, goalX] in the maze.
     */
    private double costH(Coordinates c, int goalY, int goalX) {
        // Heuristic: The no. of moves will be equal to the difference in the y coordinate and x coordinate values.
        double movementCost = (Math.abs(goalX - c.getX()) + Math.abs(goalY - c.getY())) * robot.MOVE_COST;

        if (movementCost == 0) return 0;

        // Heuristic: If c is not in the same Y coordinate and X coordinate, one turn will be needed.
        double turnCost = 0;
        if (goalX - c.getX() != 0 && goalY - c.getY() != 0) {
            turnCost = robot.TURN_COST;
        }

        return movementCost + turnCost;
    }

    /**
     * Returns the target direction of the robot from [robotR, robotC] to target Coordinates.
     */
    private DIRECTION getTargetDir(int robotR, int robotC, DIRECTION robotDir, Coordinates target) {
    	//need to change 
        if (robotC - target.getX() > 0) {
            return DIRECTION.W;
        } else if (target.getX() - robotC > 0) {
            return DIRECTION.E;
        } else {
            if (robotR - target.getY() > 0) {
                return DIRECTION.S;
            } else if (target.getY() - robotR > 0) {
                return DIRECTION.N;
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
        return (numOfTurn * robot.TURN_COST);
    }

    /**
     * Calculate the actual cost of moving from Coordinates a to Coordinates b (assuming both are neighbors).
     */
    private double costG(Coordinates a, Coordinates b, DIRECTION aDir) {
        double moveCost = robot.MOVE_COST; // one movement to neighbor

        double turnCost;
        DIRECTION targetDir = getTargetDir(a.getY(), a.getX(), aDir, b);
        turnCost = getTurnCost(aDir, targetDir);

        return moveCost + turnCost;
    }    
}

    /**
     * Find the fastest path from the robot's current position to [goalY, goalX].
     */
    public String runFastestPath(int goalY, int goalX) {
        System.out.println("Calculating fastest path from (" + current.getY() + ", " + current.getX() + ") to goal (" + goalY + ", " + goalX + ")...");

        Stack<Coordinates> path;
        do {
            loopCount++;

            // Get coordinates with minimum cost from nextVisit and assign it to current.
            current = minimumCostCoordinates(goalY, goalX);

            // Point the robot in the direction of current from the previous coordinates.
            if (parents.containsKey(current)) {
                curDir = getTargetDir(parents.get(current).getY(), parents.get(current).getX(), curDir, current);
            }

            visited.add(current);       // add current to visited
            nextVisit.remove(current);    // remove current from nextVisit

            if (visited.contains(map.getCoordinate(goalY, goalX))) {
                System.out.println("Goal visited. Path found!");
                path = getPath(goalY, goalX);
                printFastestPath(path);
                return executePath(path, goalY, goalX);
            }

            // Setup neighbors of current coordinate. [Top, Bottom, Left, Right].
            if (map.checkValidCoordinates(current.getY() + 1, current.getX())) {
                neighbors[0] = map.getCoordinate(current.getY() + 1, current.getX());
                if (!canBeVisited(neighbors[0])) {
                    neighbors[0] = null;
                }
            }
            if (map.checkValidCoordinates(current.getY() - 1, current.getX())) {
                neighbors[1] = map.getCoordinate(current.getY() - 1, current.getX());
                if (!canBeVisited(neighbors[1])) {
                    neighbors[1] = null;
                }
            }
            if (map.checkValidCoordinates(current.getY(), current.getX() - 1)) {
                neighbors[2] = map.getCoordinate(current.getY(), current.getX() - 1);
                if (!canBeVisited(neighbors[2])) {
                    neighbors[2] = null;
                }
            }
            if (map.checkValidCoordinates(current.getY(), current.getX() + 1)) {
                neighbors[3] = map.getCoordinate(current.getY(), current.getX() + 1);
                if (!canBeVisited(neighbors[3])) {
                    neighbors[3] = null;
                }
            }

            // Iterate through neighbors and update the g(n) values of each.
            for (int i = 0; i < 4; i++) {
                if (neighbors[i] != null) {
                    if (visited.contains(neighbors[i])) {
                        continue;
                    }

                    if (!(nextVisit.contains(neighbors[i]))) {
                        parents.put(neighbors[i], current);
                        gCosts[neighbors[i].getY()][neighbors[i].getX()] = gCosts[current.getY()][current.getX()] + costG(current, neighbors[i], curDir);
                        nextVisit.add(neighbors[i]);
                    } else {
                        double currentGScore = gCosts[neighbors[i].getY()][neighbors[i].getX()];
                        double newGScore = gCosts[current.getY()][current.getX()] + costG(current, neighbors[i], curDir);
                        if (newGScore < currentGScore) {
                            gCosts[neighbors[i].getY()][neighbors[i].getX()] = newGScore;
                            parents.put(neighbors[i], current);
                        }
                    }
                }
            }
        } while (!nextVisit.isEmpty());

        System.out.println("Path not found!");
        return null;
    }

    /**
     * Generates path in reverse using the parents HashMap.
     */
    private Stack<Coordinates> getPath(int goalY, int goalX) {
        Stack<Coordinates> actualPath = new Stack<>();
        Coordinates temp = map.getCoordinate(goalY, goalX);

        while (true) {
            actualPath.push(temp);
            temp = parents.get(temp);
            if (temp == null) {
                break;
            }
        }

        return actualPath;
    }

    /**
     * Executes the fastest path and returns a StringBuilder object with the path steps.
     */
    private String executePath(Stack<Coordinates> path, int goalY, int goalX) {
        StringBuilder outputString = new StringBuilder();

        Coordinates temp = path.pop();
        DIRECTION targetDir;

        ArrayList<MOVEMENT> movements = new ArrayList<>();

        Robot tempRobot = new Robot(1, 1, false);
        tempRobot.setSpeed(0);
        while ((tempRobot.getRobotPosY() != goalY) || (tempRobot.getRobotPosX() != goalX)) {
            if (tempRobot.getRobotPosY() == temp.getY() && tempRobot.getRobotPosX() == temp.getX()) {
                temp = path.pop();
            }

            targetDir = getTargetDir(tempRobot.getRobotPosY(), tempRobot.getRobotPosX(), tempRobot.getRobotCurDir(), temp);

            MOVEMENT m;
            if (tempRobot.getRobotCurDir() != targetDir) {
                m = getTargetMove(tempRobot.getRobotCurDir(), targetDir);
            } else {
                m = MOVEMENT.FORWARD;
            }

            System.out.println("Movement " + MOVEMENT.print(m) + " from (" + tempRobot.getRobotPosY() + ", " + tempRobot.getRobotPosX() + ") to (" + temp.getY() + ", " + temp.getX() + ")");

            tempRobot.move(m);
            movements.add(m);
            outputString.append(MOVEMENT.print(m));
        }

        if (!robot.getRealRobot() || explorationMode) {
            for (MOVEMENT x : movements) {
                if (x == MOVEMENT.FORWARD) {
                    if (!canMoveForward()) {
                        System.out.println("Early termination of fastest path execution.");
                        return "T";
                    }
                }

                robot.move(x);
                this.map.repaint();

                // During exploration, use sensor data to update map.
                if (explorationMode) {
                    robot.setSensors();
                    robot.sense(this.map, this.realMap);
                    this.map.repaint();
                }
            }
        } else {
            int fCount = 0;
            for (MOVEMENT x : movements) {
                if (x == MOVEMENT.FORWARD) {
                    fCount++;
                    if (fCount == 10) {
                        robot.moveForwardMultiple(fCount);
                        fCount = 0;
                        map.repaint();
                    }
                } else if (x == MOVEMENT.RIGHT || x == MOVEMENT.LEFT) {
                    if (fCount > 0) {
                        robot.moveForwardMultiple(fCount);
                        fCount = 0;
                        map.repaint();
                    }

                    robot.move(x);
                    map.repaint();
                }
            }

            if (fCount > 0) {
                robot.moveForwardMultiple(fCount);
                map.repaint();
            }
        }

        System.out.println("\nMovements: " + outputString.toString());
        return outputString.toString();
    }

    /**
     * Returns true if the robot can move forward one coordinate with the current heading.
     */
    private boolean canMoveForward() {
        int x = robot.getRobotPosX();
        int y = robot.getRobotPosY();

        switch (robot.getRobotCurDir()) {
            case NORTH:
                if (!map.isObstacle(y + 2, x - 1) && !map.isObstacle(y + 2, x) && !map.isObstacle(y + 2, x + 1)) {
                    return true;
                }
                break;
            case EAST:
                if (!map.isObstacle(y + 1, x + 2) && !map.isObstacle(y, x + 2) && !map.isObstacle(y - 1, x + 2)) {
                    return true;
                }
                break;
            case SOUTH:
                if (!map.isObstacle(y - 2, x - 1) && !map.isObstacle(y - 2, x) && !map.isObstacle(y - 2, x + 1)) {
                    return true;
                }
                break;
            case WEST:
                if (!map.isObstacle(y + 1, x - 2) && !map.isObstacle(y, x - 2) && !map.isObstacle(y - 1, x - 2)) {
                    return true;
                }
                break;
        }

        return false;
    }

    /**
     * Returns the movement to execute to get from one direction to another.
     */
    private MOVEMENT getTargetMove(DIRECTION a, DIRECTION b) {
        switch (a) {
            case NORTH:
                switch (b) {
                    case NORTH:
                        return MOVEMENT.ERROR;
                    case SOUTH:
                        return MOVEMENT.LEFT;
                    case WEST:
                        return MOVEMENT.LEFT;
                    case EAST:
                        return MOVEMENT.RIGHT;
                }
                break;
            case SOUTH:
                switch (b) {
                    case NORTH:
                        return MOVEMENT.LEFT;
                    case SOUTH:
                        return MOVEMENT.ERROR;
                    case WEST:
                        return MOVEMENT.RIGHT;
                    case EAST:
                        return MOVEMENT.LEFT;
                }
                break;
            case WEST:
                switch (b) {
                    case NORTH:
                        return MOVEMENT.RIGHT;
                    case SOUTH:
                        return MOVEMENT.LEFT;
                    case WEST:
                        return MOVEMENT.ERROR;
                    case EAST:
                        return MOVEMENT.LEFT;
                }
                break;
            case EAST:
                switch (b) {
                    case NORTH:
                        return MOVEMENT.LEFT;
                    case SOUTH:
                        return MOVEMENT.RIGHT;
                    case WEST:
                        return MOVEMENT.LEFT;
                    case EAST:
                        return MOVEMENT.ERROR;
                }
        }
        return MOVEMENT.ERROR;
    }

    /**
     * Prints the fastest path from the Stack object.
     */
    private void printFastestPath(Stack<Coordinates> path) {
        System.out.println("\nLooped " + loopCount + " times.");
        System.out.println("The number of steps is: " + (path.size() - 1) + "\n");

        Stack<Coordinates> pathForPrint = (Stack<Coordinates>) path.clone();
        Coordinates temp;
        System.out.println("Path:");
        while (!pathForPrint.isEmpty()) {
            temp = pathForPrint.pop();
            if (!pathForPrint.isEmpty()) System.out.print("(" + temp.getY() + ", " + temp.getX() + ") --> ");
            else System.out.print("(" + temp.getY() + ", " + temp.getX() + ")");
        }

        System.out.println("\n");
    }

    /**
     * Prints all the current g(n) values for the coordinates.
     */
    public void printGCosts() {
        for (int i = 0; i < Constants.MAX_Y; i++) {
            for (int j = 0; j < Constants.MAX_X; j++) {
                System.out.print(gCosts[Constants.MAX_Y - 1 - i][j]);
                System.out.print(";");
            }
            System.out.println("\n");
        }
    }
}
