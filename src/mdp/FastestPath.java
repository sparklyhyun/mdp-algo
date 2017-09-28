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
    private double[][] gCosts;              // array of real cost from START to [x-coordinate][y-coordinate] i.e. g(n)
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
        this.current = map.getCoordinate(robot.getRobotPosX(), robot.getRobotPosY()); 
        this.curDir = robot.getRobotDir();
        this.gCosts = new double[Constants.MAX_X][Constants.MAX_Y];

        // Initialize gCosts array
        for (int i = 0; i < Constants.MAX_X; i++) {
            for (int j = 0; j < Constants.MAX_Y; j++) {
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
        gCosts[robot.getRobotPosX()][robot.getRobotPosY()] = 0; 
        this.loopCount = 0;
    }  
    /**
     * Returns true if the cell can be visited.
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
    private Coordinates minimumCostCoordinates(int goalX, int goalY) {
        int size = nextVisit.size();
        double minCost = robot.INFINITE_COST;
        Coordinates result = null;

        for (int i = size - 1; i >= 0; i--) {
            double gCost = gCosts[(nextVisit.get(i).getX())][(nextVisit.get(i).getY())];
            double cost = gCost + costH(nextVisit.get(i), goalX, goalY);
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
        double movementCost = (Math.abs(goalY - c.getY()) + Math.abs(goalX - c.getX())) * robot.MOVE_COST;

        if (movementCost == 0) return 0;

        // Heuristic: If c is not in the same X coordinate and Y coordinate, one turn will be needed.
        double turnCost = 0;
        if (goalY - c.getY() != 0 && goalX - c.getX() != 0) {
            turnCost = robot.TURN_COST;
        }

        return movementCost + turnCost;
    }

    /**
     * Returns the target direction of the robot from [robotR, robotC] to target Coordinates.
     */
    private DIRECTION getTargetDir(int robotR, int robotC, DIRECTION robotDir, Coordinates target) {
    	//need to change 
        if (robotC - target.getY() > 0) {
            return DIRECTION.W;
        } else if (target.getY() - robotC > 0) {
            return DIRECTION.E;
        } else {
            if (robotR - target.getX() > 0) {
                return DIRECTION.S;
            } else if (target.getX() - robotR > 0) {
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
        DIRECTION targetDir = getTargetDir(a.getX(), a.getY(), aDir, b);
        turnCost = getTurnCost(aDir, targetDir);

        return moveCost + turnCost;
    }    
}

    /**
     * Find the fastest path from the robot's current position to [goalX, goalY].
     */
    public String runFastestPath(int goalX, int goalY) {
        System.out.println("Calculating fastest path from (" + current.getX() + ", " + current.getY() + ") to goal (" + goalX + ", " + goalY + ")...");

        Stack<Cell> path;
        do {
            loopCount++;

            // Get cell with minimum cost from toVisit and assign it to current.
            current = minimumCostCell(goalX, goalY);

            // Point the robot in the direction of current from the previous cell.
            if (parents.containsKey(current)) {
                curDir = getTargetDir(parents.get(current).getX(), parents.get(current).getY(), curDir, current);
            }

            visited.add(current);       // add current to visited
            toVisit.remove(current);    // remove current from toVisit

            if (visited.contains(map.getCell(goalX, goalY))) {
                System.out.println("Goal visited. Path found!");
                path = getPath(goalX, goalY);
                printFastestPath(path);
                return executePath(path, goalX, goalY);
            }

            // Setup neighbors of current cell. [Top, Bottom, Left, Right].
            if (map.checkValidCoordinates(current.getX() + 1, current.getY())) {
                neighbors[0] = map.getCell(current.getX() + 1, current.getY());
                if (!canBeVisited(neighbors[0])) {
                    neighbors[0] = null;
                }
            }
            if (map.checkValidCoordinates(current.getX() - 1, current.getY())) {
                neighbors[1] = map.getCell(current.getX() - 1, current.getY());
                if (!canBeVisited(neighbors[1])) {
                    neighbors[1] = null;
                }
            }
            if (map.checkValidCoordinates(current.getX(), current.getY() - 1)) {
                neighbors[2] = map.getCell(current.getX(), current.getY() - 1);
                if (!canBeVisited(neighbors[2])) {
                    neighbors[2] = null;
                }
            }
            if (map.checkValidCoordinates(current.getX(), current.getY() + 1)) {
                neighbors[3] = map.getCell(current.getX(), current.getY() + 1);
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

                    if (!(toVisit.contains(neighbors[i]))) {
                        parents.put(neighbors[i], current);
                        gCosts[neighbors[i].getX()][neighbors[i].getY()] = gCosts[current.getX()][current.getY()] + costG(current, neighbors[i], curDir);
                        toVisit.add(neighbors[i]);
                    } else {
                        double currentGScore = gCosts[neighbors[i].getX()][neighbors[i].getY()];
                        double newGScore = gCosts[current.getX()][current.getY()] + costG(current, neighbors[i], curDir);
                        if (newGScore < currentGScore) {
                            gCosts[neighbors[i].getX()][neighbors[i].getY()] = newGScore;
                            parents.put(neighbors[i], current);
                        }
                    }
                }
            }
        } while (!toVisit.isEmpty());

        System.out.println("Path not found!");
        return null;
    }

    /**
     * Generates path in reverse using the parents HashMap.
     */
    private Stack<Cell> getPath(int goalX, int goalY) {
        Stack<Cell> actualPath = new Stack<>();
        Cell temp = map.getCell(goalX, goalY);

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
    private String executePath(Stack<Cell> path, int goalX, int goalY) {
        StringBuilder outputString = new StringBuilder();

        Cell temp = path.pop();
        DIRECTION targetDir;

        ArrayList<MOVEMENT> movements = new ArrayList<>();

        Robot tempBot = new Robot(1, 1, false);
        tempBot.setSpeed(0);
        while ((tempBot.getRobotPosX() != goalX) || (tempBot.getRobotPosY() != goalY)) {
            if (tempBot.getRobotPosX() == temp.getX() && tempBot.getRobotPosY() == temp.getY()) {
                temp = path.pop();
            }

            targetDir = getTargetDir(tempBot.getRobotPosX(), tempBot.getRobotPosY(), tempBot.getRobotCurDir(), temp);

            MOVEMENT m;
            if (tempBot.getRobotCurDir() != targetDir) {
                m = getTargetMove(tempBot.getRobotCurDir(), targetDir);
            } else {
                m = MOVEMENT.FORWARD;
            }

            System.out.println("Movement " + MOVEMENT.print(m) + " from (" + tempBot.getRobotPosX() + ", " + tempBot.getRobotPosY() + ") to (" + temp.getX() + ", " + temp.getY() + ")");

            tempBot.move(m);
            movements.add(m);
            outputString.append(MOVEMENT.print(m));
        }

        if (!bot.getRealBot() || explorationMode) {
            for (MOVEMENT x : movements) {
                if (x == MOVEMENT.FORWARD) {
                    if (!canMoveForward()) {
                        System.out.println("Early termination of fastest path execution.");
                        return "T";
                    }
                }

                bot.move(x);
                this.map.repaint();

                // During exploration, use sensor data to update map.
                if (explorationMode) {
                    bot.setSensors();
                    bot.sense(this.map, this.realMap);
                    this.map.repaint();
                }
            }
        } else {
            int fCount = 0;
            for (MOVEMENT x : movements) {
                if (x == MOVEMENT.FORWARD) {
                    fCount++;
                    if (fCount == 10) {
                        bot.moveForwardMultiple(fCount);
                        fCount = 0;
                        map.repaint();
                    }
                } else if (x == MOVEMENT.RIGHT || x == MOVEMENT.LEFT) {
                    if (fCount > 0) {
                        bot.moveForwardMultiple(fCount);
                        fCount = 0;
                        map.repaint();
                    }

                    bot.move(x);
                    map.repaint();
                }
            }

            if (fCount > 0) {
                bot.moveForwardMultiple(fCount);
                map.repaint();
            }
        }

        System.out.println("\nMovements: " + outputString.toString());
        return outputString.toString();
    }

    /**
     * Returns true if the robot can move forward one cell with the current heading.
     */
    private boolean canMoveForward() {
        int x = bot.getRobotPosX();
        int y = bot.getRobotPosy();

        switch (bot.getRobotCurDir()) {
            case NORTH:
                if (!map.isObstacleCell(x + 2, y - 1) && !map.isObstacleCell(x + 2, y) && !map.isObstacleCell(x + 2, y + 1)) {
                    return true;
                }
                break;
            case EAST:
                if (!map.isObstacleCell(x + 1, y + 2) && !map.isObstacleCell(x, y + 2) && !map.isObstacleCell(x - 1, y + 2)) {
                    return true;
                }
                break;
            case SOUTH:
                if (!map.isObstacleCell(x - 2, y - 1) && !map.isObstacleCell(x - 2, y) && !map.isObstacleCell(x - 2, y + 1)) {
                    return true;
                }
                break;
            case WEST:
                if (!map.isObstacleCell(x + 1, y - 2) && !map.isObstacleCell(x, y - 2) && !map.isObstacleCell(x - 1, y - 2)) {
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
    private void printFastestPath(Stack<Cell> path) {
        System.out.println("\nLooped " + loopCount + " times.");
        System.out.println("The number of steps is: " + (path.size() - 1) + "\n");

        Stack<Cell> pathForPrint = (Stack<Cell>) path.clone();
        Cell temp;
        System.out.println("Path:");
        while (!pathForPrint.isEmpty()) {
            temp = pathForPrint.pop();
            if (!pathForPrint.isEmpty()) System.out.print("(" + temp.getX() + ", " + temp.getY() + ") --> ");
            else System.out.print("(" + temp.getX() + ", " + temp.getY() + ")");
        }

        System.out.println("\n");
    }

    /**
     * Prints all the current g(n) values for the cells.
     */
    public void printGCosts() {
        for (int i = 0; i < MapConstants.MAX_X; i++) {
            for (int j = 0; j < MapConstants.MAX_Y; j++) {
                System.out.print(gCosts[MapConstants.MAX_X - 1 - i][j]);
                System.out.print(";");
            }
            System.out.println("\n");
        }
    }
}
