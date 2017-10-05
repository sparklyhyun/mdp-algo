package mdp;

import mdp.Constants.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class FastestPath2 {
    private ArrayList<Coordinates> toVisit;        // array of Coordinatess to be visited
    private ArrayList<Coordinates> visited;        // array of visited Coordinatess
    private HashMap<Coordinates, Coordinates> parents;    // HashMap of Child --> Parent
    private Coordinates current;                   // current Coordinates
    private Coordinates[] neighbors;               // array of neighbors of current Coordinates
    private DIRECTION curDir;               // current direction of robot
    private double[][] gCosts;              // array of real cost from START to [row][col] i.e. g(n)
    private Robot bot;
    private Map exploredMap;
    private final Map realMap;
    private int loopCount;
    private boolean explorationMode;

    public FastestPath2(Map exploredMap, Robot bot) {
        this.realMap = null;
        initObject(exploredMap, bot);
    }

    public FastestPath2(Map exploredMap, Robot bot, Map realMap) {
        this.realMap = realMap;
        this.explorationMode = true;
        initObject(exploredMap, bot);
    }

    /**
     * Initialise the FastestPath object.
     */
    private void initObject(Map map, Robot bot) {
        this.bot = bot;
        this.exploredMap = map;
        this.toVisit = new ArrayList<>();
        this.visited = new ArrayList<>();
        this.parents = new HashMap<>();
        this.neighbors = new Coordinates[4];
        this.current = map.getCoordinateTwo(bot.getRobotPosX(), bot.getRobotPosY());
        this.curDir = bot.getRobotDir();
        this.gCosts = new double[Constants.MAX_Y][Constants.MAX_X];

        // Initialise gCosts array
        for (int i = 0; i < Constants.MAX_Y; i++) {
            for (int j = 0; j < Constants.MAX_X; j++) {
                Coordinates Coordinates = map.getCoordinateTwo(i, j);
                if (!canBeVisited(Coordinates)) {
                    gCosts[i][j] = Robot.INFINITE_COST;
                } else {
                    gCosts[i][j] = 0;
                }
            }
        }
        toVisit.add(current);

        // Initialise starting point
        gCosts[bot.getRobotPosY()][bot.getRobotPosX()] = 0;
        this.loopCount = 0;
    }

    /**
     * Returns true if the Coordinates can be visited.
     */
    private boolean canBeVisited(Coordinates c) {
        return c.getIsExplored() && !c.getIsObstacle() && !c.getIsVirtualWall();
    }

    /**
     * Returns the Coordinates inside toVisit with the minimum g(n) + h(n).
     */
    private Coordinates minimumCostCoordinates(int goalRow, int getX) {
        int size = toVisit.size();
        double minCost = Robot.INFINITE_COST;
        Coordinates result = null;

        for (int i = size - 1; i >= 0; i--) {
            double gCost = gCosts[(toVisit.get(i).getY())][(toVisit.get(i).getX())];
            double cost = gCost + costH(toVisit.get(i), goalRow, getX);
            if (cost < minCost) {
                minCost = cost;
                result = toVisit.get(i);
            }
        }

        return result;
    }

    /**
     * Returns the heuristic cost i.e. h(n) from a given Coordinates to a given [goalRow, goalCol] in the maze.
     */
    private double costH(Coordinates b, int goalRow, int goalCol) {
        // Heuristic: The no. of moves will be equal to the difference in the row and column values.
        double movementCost = (Math.abs(goalCol - b.getX()) + Math.abs(goalRow - b.getY())) * Robot.MOVE_COST;

        if (movementCost == 0) return 0;

        // Heuristic: If b is not in the same row or column, one turn will be needed.
        double turnCost = 0;
        if (goalCol - b.getX() != 0 || goalRow - b.getY() != 0) {
            turnCost = Robot.TURN_COST;
        }

        return movementCost + turnCost;
    }

    /**
     * Returns the target direction of the bot from [botR, botC] to target Coordinates.
     */
    private DIRECTION getTargetDir(int botR, int botC, DIRECTION botDir, Coordinates target) {
        if (botC - target.getX() > 0) {
            return DIRECTION.W;
        } else if (target.getX() - botC > 0) {
            return DIRECTION.E;
        } else {
            if (botR - target.getY() > 0) {
                return DIRECTION.S;
            } else if (target.getY() - botR > 0) {
                return DIRECTION.N;
            } else {
                return botDir;
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
        return (numOfTurn * Robot.TURN_COST);
    }

    /**
     * Calculate the actual cost of moving from Coordinates a to Coordinates b (assuming both are neighbors).
     */
    private double costG(Coordinates a, Coordinates b, DIRECTION aDir) {
        double moveCost = Robot.MOVE_COST; // one movement to neighbor

        double turnCost;
        DIRECTION targetDir = getTargetDir(a.getY(), a.getX(), aDir, b);
        turnCost = getTurnCost(aDir, targetDir);

        return moveCost + turnCost;
    }

    /**
     * Find the fastest path from the robot's current position to [goalRow, goalCol].
     */
    public String runFastestPath(int goalRow, int goalCol) {
        System.out.println("Calculating fastest path from (" + current.getY() + ", " + current.getX() + ") to goal (" + goalRow + ", " + goalCol + ")...");

        Stack<Coordinates> path;
        do {
            loopCount++;
            System.out.println("inside do while loop " + loopCount );
            // Get Coordinates with minimum cost from toVisit and assign it to current.
            current = minimumCostCoordinates(goalRow, goalCol);

            // Point the robot in the direction of current from the previous Coordinates.
            if (parents.containsKey(current)) {
                curDir = getTargetDir(parents.get(current).getY(), parents.get(current).getX(), curDir, current);
            }
            System.out.println("if statement exit 1");
            
            
            visited.add(current);       // add current to visited
            toVisit.remove(current);    // remove current from toVisit

            if (visited.contains(exploredMap.getCoordinateTwo(goalRow, goalCol))) {
                System.out.println("Goal visited. Path found!");
                path = getPath(goalRow, goalCol);
                printFastestPath(path);
                return executePath(path, goalRow, goalCol);
            }
            System.out.println("if statement exit 2");
            
            // Setup neighbors of current Coordinates. [Top, Bottom, Left, Right].
            if (exploredMap.checkValidCoordinates(current.getY() + 1, current.getX())) {
            	//System.out.println(x);
                neighbors[0] = exploredMap.getCoordinateTwo(current.getY() + 1, current.getX());
                if (!canBeVisited(neighbors[0])) {
                    neighbors[0] = null;
                }
            }
            System.out.println("if statement exit 3");
            
            if (exploredMap.checkValidCoordinates(current.getY() - 1, current.getX())) {
                neighbors[1] = exploredMap.getCoordinateTwo(current.getY() - 1, current.getX());
                if (!canBeVisited(neighbors[1])) {
                    neighbors[1] = null;
                }
            }
            System.out.println("if statement exit 4");
            
            if (exploredMap.checkValidCoordinates(current.getY(), current.getX() - 1)) {
                neighbors[2] = exploredMap.getCoordinateTwo(current.getY(), current.getX() - 1);
                if (!canBeVisited(neighbors[2])) {
                    neighbors[2] = null;
                }
            }
            if (exploredMap.checkValidCoordinates(current.getY(), current.getX() + 1)) {
                neighbors[3] = exploredMap.getCoordinateTwo(current.getY(), current.getX() + 1);
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
                        gCosts[neighbors[i].getY()][neighbors[i].getX()] = gCosts[current.getY()][current.getX()] + costG(current, neighbors[i], curDir);
                        toVisit.add(neighbors[i]);
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
        } while (!toVisit.isEmpty());

        System.out.println("Path not found!");
        return null;
    }

    /**
     * Generates path in reverse using the parents HashMap.
     */
    private Stack<Coordinates> getPath(int goalRow, int goalCol) {
        Stack<Coordinates> actualPath = new Stack<>();
        Coordinates temp = exploredMap.getCoordinateTwo(goalRow, goalCol);

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
    private String executePath(Stack<Coordinates> path, int goalRow, int goalCol) {
        StringBuilder outputString = new StringBuilder();

        Coordinates temp = path.pop();
        DIRECTION targetDir;

        ArrayList<MOVEMENT> movements = new ArrayList<>();

        Robot tempBot = new Robot(1, 1, false);
        tempBot.setSpeed(0);
        while ((tempBot.getRobotPosY() != goalRow) || (tempBot.getRobotPosX() != goalCol)) {
            if (tempBot.getRobotPosY() == temp.getY() && tempBot.getRobotPosX() == temp.getX()) {
                temp = path.pop();
            }

            targetDir = getTargetDir(tempBot.getRobotPosY(), tempBot.getRobotPosX(), tempBot.getRobotDir(), temp);

            MOVEMENT m;
            if (tempBot.getRobotDir() != targetDir) {
                m = getTargetMove(tempBot.getRobotDir(), targetDir);
            } else {
                m = MOVEMENT.F;
            }

            System.out.println("Movement " + MOVEMENT.print(m) + " from (" + tempBot.getRobotPosY() + ", " + tempBot.getRobotPosX() + ") to (" + temp.getY() + ", " + temp.getX() + ")");

            tempBot.move(m, 1, false);
            movements.add(m);
            outputString.append(MOVEMENT.print(m));
        }

        if (!bot.getRealRobot() || explorationMode) {
            for (MOVEMENT x : movements) {
                if (x == MOVEMENT.F) {
                    if (!canMoveForward()) {
                        System.out.println("Early termination of fastest path execution.");
                        return "T";
                    }
                }

                bot.move(x, 1, false);
                this.exploredMap.repaint();

                // During exploration, use sensor data to update exploredMap.
                if (explorationMode) {
                    bot.setSentors();
                    bot.senseDist(this.exploredMap, this.realMap);
                    this.exploredMap.repaint();
                }
            }
        } else {
            int fCount = 0;
            for (MOVEMENT x : movements) {
                if (x == MOVEMENT.F) {
                    fCount++;
                    if (fCount == 10) {
                        bot.move(x, fCount, false);
                        fCount = 0;
                        exploredMap.repaint();
                    }
                } else if (x == MOVEMENT.R || x == MOVEMENT.L) {
                    if (fCount > 0) {
                        bot.move(x, fCount, false);
                        fCount = 0;
                        exploredMap.repaint();
                    }

                    bot.move(x, fCount, false);
                    exploredMap.repaint();
                }
            }

            if (fCount > 0) {
                //bot.move(x, fCount, false);
                exploredMap.repaint();
            }
        }

        System.out.println("\nMovements: " + outputString.toString());
        return outputString.toString();
    }

    /**
     * Returns true if the robot can move forward one Coordinates with the current heading.
     */
    private boolean canMoveForward() {
        int row = bot.getRobotPosY();
        int col = bot.getRobotPosX();

        switch (bot.getRobotDir()) {
            case N:
                if (!exploredMap.isObstacle(row + 2, col - 1) && !exploredMap.isObstacle(row + 2, col) && !exploredMap.isObstacle(row + 2, col + 1)) {
                    return true;
                }
                break;
            case E:
                if (!exploredMap.isObstacle(row + 1, col + 2) && !exploredMap.isObstacle(row, col + 2) && !exploredMap.isObstacle(row - 1, col + 2)) {
                    return true;
                }
                break;
            case S:
                if (!exploredMap.isObstacle(row - 2, col - 1) && !exploredMap.isObstacle(row - 2, col) && !exploredMap.isObstacle(row - 2, col + 1)) {
                    return true;
                }
                break;
            case W:
                if (!exploredMap.isObstacle(row + 1, col - 2) && !exploredMap.isObstacle(row, col - 2) && !exploredMap.isObstacle(row - 1, col - 2)) {
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
            case N:
                switch (b) {
                    case N:
                        return MOVEMENT.ERROR;
                    case S:
                        return MOVEMENT.L;
                    case W:
                        return MOVEMENT.L;
                    case E:
                        return MOVEMENT.R;
                }
                break;
            case S:
                switch (b) {
                    case N:
                        return MOVEMENT.L;
                    case S:
                        return MOVEMENT.ERROR;
                    case W:
                        return MOVEMENT.R;
                    case E:
                        return MOVEMENT.L;
                }
                break;
            case W:
                switch (b) {
                    case N:
                        return MOVEMENT.R;
                    case S:
                        return MOVEMENT.L;
                    case W:
                        return MOVEMENT.ERROR;
                    case E:
                        return MOVEMENT.L;
                }
                break;
            case E:
                switch (b) {
                    case N:
                        return MOVEMENT.L;
                    case S:
                        return MOVEMENT.R;
                    case W:
                        return MOVEMENT.L;
                    case E:
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
     * Prints all the current g(n) values for the Coordinatess.
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
