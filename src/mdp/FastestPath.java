package mdp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.lang.String;

import mdp.Constants.*;

public class FastestPath {
    private HashMap<Coordinates, Coordinates> parents;    // HashMap of Child Coordinates to Parent Coordinates
    private Coordinates current;                   // current position Coordinates
    private ArrayList<Coordinates> visited;        // array of visited Coordinates
    private ArrayList<Coordinates> nextVisit;        // array of Coordinates to be visited next
    private Coordinates[] neighbors;               // array of neighbors of current Coordinates
    private DIRECTION curDir;               // current direction of robot
    private Map map;
    private Map realMap;  
    private double[][] gCost;              // array of real cost from START to [y-coordinate][x-coordinate]
    private Robot robot;                    //robot object
    private int loopCount;                  // loop count variable
    private boolean explorationMode;        //to indicate whether it is in exploration mode
    /*
    public FastestPath(Map map, Robot robot){
        this.realMap = null;
        initObject(map,robot);
    }
    */
    

    public FastestPath(Map map, Robot robot, Map realMap) {
    	System.out.println("fastest path entered");
        this.realMap = realMap;
        this.explorationMode = true;
        System.out.println("fastest path before init");
        initObject(map, robot);
        System.out.println("fastest path after init");
    }
  
    private void initObject(Map map, Robot robot) {
        this.parents = new HashMap<>();
        this.current = map.getCoordinate(robot.getRobotPosX(), robot.getRobotPosY()); 
        this.visited = new ArrayList<>();
        this.nextVisit = new ArrayList<>();
        this.neighbors = new Coordinates[4];
        this.curDir = robot.getRobotDir();
        this.map = map;
        this.gCost = new double[Constants.MAX_Y][Constants.MAX_X];
        this.robot = robot;
        System.out.println("init initialized");
        

        // Initialize gCost array
        for (int i = 0; i < Constants.MAX_Y; i++) {
            for (int j = 0; j < Constants.MAX_X; j++) {
                Coordinates coordinates = map.getCoordinate(j, i);
                if (!canBeVisited(coordinates)) {
                	//System.out.println("init can be visited?: " + canBeVisited(coordinates) );
                    gCost[i][j] = robot.INFINITE_COST;
                    //System.out.println(gCost[i][j]);
                } else {
                    gCost[i][j] = -1;
                    
                    //System.out.println(gCost[i][j]);
                }
                //System.out.println("x: "+ i+ "y: " +j);
            }System.out.println("Gcostarray almost done");
            //printGCost();
            
        }
        
        System.out.println("Gcostarray done");
        printGCost();
        nextVisit.add(current);
        
        System.out.println("nextvisit added");
        
        // Initialize starting point
        gCost[robot.getRobotPosY()][robot.getRobotPosX()] = 0; 
        this.loopCount = 0;
    }  
    
    //Returns true if the coordinates can be visited.
    
    private boolean canBeVisited(Coordinates c) {
        boolean checkIsExplored = c.getIsExplored();
        boolean checkIsObstacle = c.getIsObstacle();
        //boolean checkIsVirtualWall = c.getIsVirtualWall();
        boolean canBeVisitedCheck = checkIsExplored && !checkIsObstacle; /*&& !checkIsVirtualWall*/
        //System.out.println("checkisexplored: " + checkIsExplored);
        //System.out.println("checkisobstacle: " + checkIsObstacle);
        //System.out.println("canbevisitedcheck: " + canBeVisitedCheck);
        return canBeVisitedCheck;
    }

    
    //Returns the Coordinates inside nextVisit with the minimum gcost + hcost.
    
    private Coordinates checkAndUpdateMinCost(int goalY, int goalX) {
        int size = nextVisit.size();
        double minCost = robot.INFINITE_COST;
        Coordinates result = null;

        for (int i = size - 1; i >= 0; i--) {
        	double cost = gCost[(nextVisit.get(i).getY())][(nextVisit.get(i).getX())] + costH(nextVisit.get(i), goalY, goalX);
            if (cost < minCost) {
                minCost = cost;
                result = nextVisit.get(i);
            }
        }

        return result;
    }
    
    
    
    //Returns the heuristic cost i.e. h(n) from a given Coordinates to a given [goalY, goalX] in the maze.
    
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

    
    //Returns the target direction of the robot from [robotR, robotC] to target Coordinates.
    
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
    
    
    //Get the actual turning cost from one DIRECTION to another.
    
    private double getTurnCost(DIRECTION a, DIRECTION b) {
        int numOfTurn = Math.abs(a.ordinal() - b.ordinal());
        if (numOfTurn > 2) {
            numOfTurn = numOfTurn % 2;
        }
        return (numOfTurn * robot.TURN_COST);
    }

    
    //Calculate the actual cost of moving from Coordinates a to Coordinates b (assuming both are neighbors).
    
    private double costG(Coordinates a, Coordinates b, DIRECTION aDir) {
        double moveCost = robot.MOVE_COST; // one movement to neighbor

        double turnCost;
        DIRECTION targetDir = getTargetDir(a.getY(), a.getX(), aDir, b);
        turnCost = getTurnCost(aDir, targetDir);

        return moveCost + turnCost;
    }
    
    //Find the fastest path from the robot's current position to [goalY, goalX].
    
    public String runFastestPath(int goalY, int goalX)  {
        System.out.println("Calculating fastest path from (" + current.getY() + ", " + current.getX() + ") to goal (" + goalY + ", " + goalX + ")...");

        Stack<Coordinates> path;
        do {
            loopCount++;

            // Get coordinates with minimum cost from nextVisit and assign it to current.
            current = checkAndUpdateMinCost(goalY, goalX);
           // System.out.println("check update min cost");
            
            // Point the robot in the direction of current from the previous coordinates.
            if (parents.containsKey(current)) {
                curDir = getTargetDir(parents.get(current).getY(), parents.get(current).getX(), curDir, current);
                System.out.println("curDir = " + curDir);
                //System.out.println("if statement entered1");
            }
            //System.out.println("if statement exited1");
            visited.add(current);       // add current to visited
           // System.out.println("addcurrent done");
            nextVisit.remove(current);    // remove current from nextVisit
            //System.out.println("removecurrent done");
            
            if (visited.contains(map.getCoordinate(goalX, goalY))) {
                System.out.println("Goal visited. Path found!");
                path = getPath(goalY, goalX);
                printFastestPath(path);
                return executeFastestPath(path, goalY, goalX);
            }
        	//System.out.println("if statement exit 0");

            // Setup neighbors of current coordinate. [Top, Bottom, Left, Right].
            if (map.checkWithinRange(current.getX() + 1, current.getY())) {
            	//System.out.println("if statement entered2");
                neighbors[0] = map.getCoordinate(current.getX() + 1, current.getY());
               // System.out.println("neighbour done");
                if (!canBeVisited(neighbors[0])) {
                    neighbors[0] = null;
                    //System.out.println("neighbour null");
                }
            }
           // System.out.println("if statement exit 1");
            
            if (map.checkWithinRange(current.getX(), current.getY()-1)) {
            	//System.out.println("if statement entered3");

                neighbors[1] = map.getCoordinate(current.getX(), current.getY()-1);
                if (!canBeVisited(neighbors[1])) {
                    neighbors[1] = null;
                }
            }
            //System.out.println("if statement exit 2");
            if (map.checkWithinRange(current.getX()-1, current.getY())) {
            	//System.out.println("if statement entered4");

                neighbors[2] = map.getCoordinate(current.getX()-1, current.getY());
                if (!canBeVisited(neighbors[2])) {
                    neighbors[2] = null;
                }
            }
            System.out.println("if statement exit 3");
            if (map.checkWithinRange(current.getX() +1, current.getY())) {
            	//System.out.println("if statement entered5");

                neighbors[3] = map.getCoordinate(current.getX()+1, current.getY());
                if (!canBeVisited(neighbors[3])) {
                    neighbors[3] = null;
                }
            }
        	//System.out.println("if statement all done");


            // Iterate through neighbors and update the g(n) values of each.
            
            for (int i = 0; i < 4; i++) {
            	System.out.println("for loop entered");
                if (neighbors[i] != null) {
                    if (visited.contains(neighbors[i])) {
                    	System.out.println("contains neighbour[i]");

                        continue;
                    }

                    if (!(nextVisit.contains(neighbors[i]))) {
                        parents.put(neighbors[i], current);
                       // System.out.println("put parents");
                        gCost[neighbors[i].getY()][neighbors[i].getX()] = gCost[current.getY()][current.getX()] + costG(current, neighbors[i], curDir);
                        //System.out.println("print Gcost: " + gCost[neighbors[i].getY()][neighbors[i].getX()]);
                        nextVisit.add(neighbors[i]);
                      //  System.out.println("put neighbours");
                    } else {
                        double currentGScore = gCost[neighbors[i].getY()][neighbors[i].getX()];
                        double newGScore = gCost[current.getY()][current.getX()] + costG(current, neighbors[i], curDir);
                     //   System.out.println("current gsocre: " + currentGScore);
                     //   System.out.println("newGScore: " + newGScore);
                        if (newGScore < currentGScore) {
                        	//System.out.println("new gcosre< current gscore");
                            gCost[neighbors[i].getY()][neighbors[i].getX()] = newGScore;
                          //  System.out.println("gcost: " + gCost[neighbors[i].getY()][neighbors[i].getX()] );
                            parents.put(neighbors[i], current);
                           // System.out.println("add neighbours ");
                        }
                    }
                }
            }
            //System.out.println(parents.toString());
            //System.out.println(visited.toString());
            //System.out.println(nextVisit.toString());
        } while (!nextVisit.isEmpty());

        System.out.println("Path not found!");
        return null;
    }

    
    //Generates path in reverse using the parents HashMap.
    
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

    
    //Executes the fastest path and returns a StringBuilder object with the path steps.
    
    private String executeFastestPath(Stack<Coordinates> path, int goalY, int goalX) {
        StringBuilder outputString = new StringBuilder();
        
        System.out.println("string builder built");
        
        Coordinates temp = path.pop();
        System.out.println("path inside temp");

        DIRECTION targetDir;

        ArrayList<MOVEMENT> movements = new ArrayList<>();

        System.out.println("movement array list built");
        
        //Robot tempRobot = new Robot(1, 1, false);
        Robot tempRobot = robot;
        
        System.out.println("temprobot built");
        
        tempRobot.setSpeed(100);
        
        System.out.println("temprobot set speed");
        System.out.println("temproboty: " + tempRobot.getRobotPosY() );
        System.out.println("goalY: " + goalY);
        System.out.println("temprobotx: " +tempRobot.getRobotPosX() );
        System.out.println("goalX: " +goalX);
        System.out.println("while loop condition:" +(tempRobot.getRobotPosY() != goalY) + (tempRobot.getRobotPosX() != goalX) );
        while ((tempRobot.getRobotPosY() != goalY) || (tempRobot.getRobotPosX() != goalX)) {
        	
        	System.out.println("while loop entered");
        	
            if (tempRobot.getRobotPosY() == temp.getY() && tempRobot.getRobotPosX() == temp.getX()) {
                temp = path.pop();
            }

            targetDir = getTargetDir(tempRobot.getRobotPosY(), tempRobot.getRobotPosX(), tempRobot.getRobotDir(), temp);
            
            System.out.println("target direction obtained");
            
            MOVEMENT m;
            
            System.out.println("movement m created");
            
            if (tempRobot.getRobotDir() != targetDir) {
                m = getTargetMovement(tempRobot.getRobotDir(), targetDir);
            } else {
                m = MOVEMENT.F;
            }

            System.out.println("Movement " + MOVEMENT.print(m) + " from (" + tempRobot.getRobotPosY() + ", " + tempRobot.getRobotPosX() + ") to (" + temp.getY() + ", " + temp.getX() + ")");

            tempRobot.move(m,1, false);
            movements.add(m);
            outputString.append(MOVEMENT.print(m));
            System.out.println(outputString);
        }

        System.out.println("while loop exited");
        
        if (!robot.getRealRobot() || explorationMode) {
        	System.out.println("if statement entered");

            for (MOVEMENT x : movements) {
            	System.out.println("for loop entered: " + x);
                if (x == MOVEMENT.F) {
                    if (!canRobotMoveForward()) {
                        System.out.println("Early termination of fastest path execution.");
                        return "T";
                    }

                }
                robot.move(x, 1, false);
                this.map.repaint();
                

                // During exploration, use sensor data to update map.
                /*
                if (explorationMode) {
                    robot.setSentors();
                    robot.senseDist(this.map, this.realMap);
                    this.map.repaint();
                }*/
                System.out.println("for loop exited");
            }
        } else {
            int fCount = 0;
            for (MOVEMENT x : movements) {
                if (x == MOVEMENT.F) {
                    fCount++;
                    if (fCount == 10) {
                        robot.move(x, fCount, false);
                        fCount = 0;
                        map.repaint();
                    }
                } else if (x == MOVEMENT.R || x == MOVEMENT.L) {
                    if (fCount > 0) {
                        robot.move(x, fCount, false);
                        fCount = 0;
                        map.repaint();
                    }

                    robot.move(x, 0, false);
                    map.repaint();
                }
            }

            /*if (fCount > 0) {
                Movement x;
                robot.move(x, fCount, false);
                map.repaint();
            }*/
        }

        System.out.println("\nMovements: " + outputString.toString());
        return outputString.toString();
    }

    
    //Returns true if the robot can move forward one coordinate with the current heading.
    
    private boolean canRobotMoveForward() {
        int x = robot.getRobotPosX();
        int y = robot.getRobotPosY();

        switch (robot.getRobotDir()) {
            case N:
                
            	if(notObstacle(x, y+2) && notObstacle(x+1, y+2)){
            		return true;
            	}
            	/*
            	if (!map.isObstacle(y + 2, x - 1) && !map.isObstacle(y + 2, x) && !map.isObstacle(y + 2, x + 1)) {
                    return true;
                }*/
                break;
            case E:
            	if(notObstacle(x+2, y+1) && notObstacle(x+2, y)){
            		return true;
            	}
            	/*
                if (!map.isObstacle(y + 1, x + 2) && !map.isObstacle(y, x + 2) && !map.isObstacle(y - 1, x + 2)) {
                    return true;
                }*/
                break;
            case S:
            	if(notObstacle(x, y-1) && notObstacle(x+1, y-1)){
            		return true;
            	}
            	/*
                if (!map.isObstacle(y - 2, x - 1) && !map.isObstacle(y - 2, x) && !map.isObstacle(y - 2, x + 1)) {
                    return true;
                }*/
                break;
            case W:
            	if(notObstacle(x-1, y+1) && notObstacle(x-1, y)){
            		return true;
            	}
            	/*
                if (!map.isObstacle(y + 1, x - 2) && !map.isObstacle(y, x - 2) && !map.isObstacle(y - 1, x - 2)) {
                    return true;
                }*/
                break;
        }

        return false;
    }
    
    private boolean notObstacle(int x, int y){
    	if(map.checkWithinRange(x, y)){
    		if(!map.isExplored(x,y)){
    			return false;
    		}
    		if(!map.isObstacle(x,y) &&  map.isExplored(x,y)){
    			return true;
    		}
    	}
		return false;
    }

    
    //Returns the movement to execute to get from one direction to another.
    
    private MOVEMENT getTargetMovement(DIRECTION a, DIRECTION b) {
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

    
    // Prints the fastest path list of Coordinates from the Map Stack Coordinates
    
    private void printFastestPath(Stack<Coordinates> path) {
        System.out.println("\n " + loopCount + " times looped");
        System.out.println("The number of steps needed is: " + (path.size() - 1) + "\n");

        Stack<Coordinates> printPath = (Stack<Coordinates>) path.clone();
        Coordinates tempCoor;
        System.out.println("Path:");
        while (!printPath.isEmpty()) {
            tempCoor = printPath.pop();
            if (!printPath.isEmpty()) 
                System.out.print("(" + tempCoor.getY() + ", " + tempCoor.getX() + ") -> ");
            else 
                System.out.print("(" + tempCoor.getY() + ", " + tempCoor.getX() + ")");
        }

        System.out.println("\n");
    }

    
    // Prints each coordinates' gcost
    
    public void printGCost() {
        for (int i = 0; i < Constants.MAX_Y; i++) {
            for (int j = 0; j < Constants.MAX_X; j++) {
                System.out.print(gCost[Constants.MAX_Y - 1 - i][j]);
                System.out.print(";");
            }
            System.out.println("\n");
        }
    }

}
