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
    private static double[][] gCost;              // array of real cost from START to [y-coordinate][x-coordinate]
    private Robot robot;                    //robot object
    private int loopCount;                  // loop count variable
    private boolean explorationMode;        //to indicate whether it is in exploration mode    

    public FastestPath(Map map, Robot robot, Map realMap) {
    	System.out.println("fastest path entered");
        this.realMap = realMap;
        this.map = map;
        this.explorationMode = robot.getRealRobot();
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
                }
                else if(virtualWallCost(coordinates)){
                	gCost[i][j] = robot.VIRTUAL_COST;
                }
                else {
                    gCost[i][j] = -1;
                    
                    //System.out.println(gCost[i][j]);
                }
                //System.out.println("x: "+ i+ "y: " +j);
            }//System.out.println("Gcostarray almost done");
            //printGCost();
            
        }
        
        System.out.println("Gcostarray done");
        printGCost();
        nextVisit.add(current);
        
        System.out.println("nextvisit added");
        
        // Initialize starting point
        //gCost[robot.getRobotPosY()][robot.getRobotPosX()] = 0; 
        this.loopCount = 0;
    }  
    
    //Returns true if the coordinates can be visited.
    
    private boolean canBeVisited(Coordinates c) {
        boolean checkIsExplored = c.getIsExplored();
        boolean checkIsObstacle = c.getIsObstacle();
        //boolean checkIsVirtualWall = c.getIsVirtualWall();
        boolean canBeVisitedCheck = checkIsExplored && !checkIsObstacle /*&& !checkIsVirtualWall*/;
        //System.out.println("checkisexplored: " + checkIsExplored);
        //System.out.println("checkisobstacle: " + checkIsObstacle);
        //System.out.println("canbevisitedcheck: " + canBeVisitedCheck);
        return canBeVisitedCheck;
    }
    
    private boolean virtualWallCost(Coordinates c){
    	return c.getIsVirtualWall();
    }

    
    //Returns the Coordinates inside nextVisit with the minimum gcost + hcost.
    
    private Coordinates checkAndUpdateMinCost(int goalY, int goalX) {
        int size = nextVisit.size();
        double minCost = robot.INFINITE_COST;
        Coordinates result = null;

        for (int i = size - 1; i >= 0; i--) {
        	//System.out.println("gCost test: " + gCost[(nextVisit.get(i).getY())][(nextVisit.get(i).getX())]);
        	double cost = gCost[(nextVisit.get(i).getY())][(nextVisit.get(i).getX())] + costH(nextVisit.get(i), goalY, goalX);
            //System.out.println("cost test: " + cost);
        	if (cost < minCost) {
                minCost = cost;
                //System.out.println("minCost: " + minCost);
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
        
        //System.out.println("start coord: " + robot.getRobotPosX() + ", " + robot.getRobotPosY());
        
        System.out.println("start coord: " + current.getX() + ", " + current.getY());

        
        Stack<Coordinates> path;
        do {
            loopCount++;

            // Get coordinates with minimum cost from nextVisit and assign it to current.
            current = checkAndUpdateMinCost(goalY, goalX);
           //System.out.println("check update min cost");
            
            System.out.println("current x y : " + current.getX() + ", " + current.getY());
            
            // Point the robot in the direction of current from the previous coordinates.
            if (parents.containsKey(current)) {
                curDir = getTargetDir(parents.get(current).getY(), parents.get(current).getX(), curDir, current);
                System.out.println("curDir = " + curDir);
                //System.out.println("if statement entered1");
            }
            //System.out.println("if statement exited1");
            visited.add(current);       // add current to visited
            //System.out.println("addcurrent done");
            nextVisit.remove(current);    // remove current from nextVisit
            //System.out.println("removecurrent done");
            
            //Coordinates coord = map.getCoordinate(goalX, goalY);
            
            //trapped here 
            //boolean bool = visited.contains(map.getCoordinate(goalX, goalY));
            //System.out.println("if condition: "+ bool);
            
            
            if (visited.contains(map.getCoordinate(goalX, goalY))) {
            	System.out.println("Goal visited. Path found!");
            	System.out.println("goal x, y : "+goalX+ ", "+goalY);
                path = getPath(goalY, goalX);
               // System.out.println("get path done" );
                printFastestPath(path);
               // System.out.println("printfastestpath done");
                return executeFastestPath(path, goalY, goalX);
            }
        	//System.out.println("if statement exit 0");

            // Setup neighbors of current coordinate. [Top, Bottom, Left, Right].
            if (map.checkWithinRange(current.getX(), current.getY()+1)) {	//top
            	//System.out.println("if statement entered2");
                neighbors[0] = map.getCoordinate(current.getX(), current.getY()+1);
                //System.out.println("neighbour :" + neighbors[0].getX() + ", " + neighbors[0].getY());
                if (!canBeVisited(neighbors[0])) {
                    neighbors[0] = null;
                    //System.out.println("neighbour null");
                }
            }
            //System.out.println("if statement exit 1");
            
            if (map.checkWithinRange(current.getX(), current.getY()-1)) {	//bottom
            	//System.out.println("if statement entered3");

                neighbors[1] = map.getCoordinate(current.getX(), current.getY()-1);
                //System.out.println("neighbour :" + neighbors[1].getX() + ", " + neighbors[1].getY());
                if (!canBeVisited(neighbors[1])) {
                    neighbors[1] = null;
                }
            }
           // System.out.println("if statement exit 2");
            if (map.checkWithinRange(current.getX()-1, current.getY())) {	//left
            	//System.out.println("if statement entered4");

                neighbors[2] = map.getCoordinate(current.getX()-1, current.getY());
                //System.out.println("neighbour :" + neighbors[2].getX() + ", " + neighbors[2].getY());
                if (!canBeVisited(neighbors[2])) {
                    neighbors[2] = null;
                }
            }
           // System.out.println("if statement exit 3");
            if (map.checkWithinRange(current.getX() +1, current.getY())) {	//right
            	//System.out.println("if statement entered5");

                neighbors[3] = map.getCoordinate(current.getX()+1, current.getY());
                //System.out.println("neighbour :" + neighbors[3].getX() + ", " + neighbors[3].getY());
                if (!canBeVisited(neighbors[3])) {
                    neighbors[3] = null;
                }
            }
        	System.out.println("if statement all done");
        	

            // Iterate through neighbors and update the g(n) values of each.
            
            for (int i = 0; i < 4; i++) {
            	//System.out.println("for loop entered");
                if (neighbors[i] != null) {
                    if (visited.contains(neighbors[i])) {
                    	//System.out.println("contains neighbour[i]");

                        continue;
                    }

                    if (!(nextVisit.contains(neighbors[i]))) {
                        parents.put(neighbors[i], current);      
                        //System.out.println("gCost[neighbors[i].getY(][neighbors[i].getX()]: " + gCost[neighbors[i].getY()][neighbors[i].getX()]);
                        gCost[neighbors[i].getY()][neighbors[i].getX()] = gCost[neighbors[i].getY()][neighbors[i].getX()] + costG(current, neighbors[i], curDir);
                        //System.out.println("Gcost after: " + gCost[neighbors[i].getY()][neighbors[i].getX()]);
                       
                        nextVisit.add(neighbors[i]);
                       // System.out.println("put neighbours");
                    } else {
                        double currentGScore = gCost[neighbors[i].getY()][neighbors[i].getX()];
                        double newGScore = gCost[current.getY()][current.getX()] + costG(current, neighbors[i], curDir);
                        //System.out.println("current gsocre: " + currentGScore);
                        //System.out.println("newGScore: " + newGScore);
                        if (newGScore < currentGScore) {
                        	//System.out.println("new gcosre< current gscore");
                            gCost[neighbors[i].getY()][neighbors[i].getX()] = newGScore;
                           // System.out.println("gcost: " + gCost[neighbors[i].getY()][neighbors[i].getX()] );
                            parents.put(neighbors[i], current);
                           // System.out.println("add neighbours ");
                        }
                    }
                }
            }
            
        } while (!nextVisit.isEmpty());

        System.out.println("Path not found!");
        return null;
    }

    
    //Generates path in reverse using the parents HashMap.
    
    private Stack<Coordinates> getPath(int goalY, int goalX) {
        Stack<Coordinates> actualPath = new Stack<>();
        //here is the problem!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        Coordinates temp = map.getCoordinate(goalX, goalY);
        System.out.println("temp goal x ,y : " +temp.getX() + ", " +temp.getX());
        
        while (true) {
            actualPath.push(temp);
            temp = parents.get(temp);
            if (temp == null) {
                break;
            }
        }
        //System.out.println("get path while loop exited");
        
        //printGCost();
        return actualPath;
    }

    
    //Executes the fastest path and returns a StringBuilder object with the path steps.
    
    private String executeFastestPath(Stack<Coordinates> path, int goalY, int goalX) {
    	
        StringBuilder outputString = new StringBuilder();
        
       // System.out.println("string builder built");
        
        Coordinates temp = path.pop();
        //System.out.println("path inside temp");
       
        
        DIRECTION targetDir;

        ArrayList<MOVEMENT> movements = new ArrayList<>();

        System.out.println("movement array list built");
        
       
        System.out.println("robot position x, y : " + robot.getRobotPosX() + ", " + robot.getRobotPosY());
        
        Robot tempRobot = new Robot(robot.getRobotPosX(), robot.getRobotPosY(), false);
        System.out.println("temp robot position x, y : " + tempRobot.getRobotPosX() + ", " + tempRobot.getRobotPosY());
        
        tempRobot.setDirection(robot.getRobotDir());
        //Robot tempRobot = robot;
        
        //System.out.println("temprobot built");
        
        tempRobot.setSpeed(100);
        
        //System.out.println("temprobot set speed");
        //System.out.println("temproboty: " + tempRobot.getRobotPosY() );
        //System.out.println("goalY: " + goalY);
        //System.out.println("temprobotx: " +tempRobot.getRobotPosX() );
        //System.out.println("goalX: " +goalX);
        //System.out.println("while loop condition:" +(tempRobot.getRobotPosY() != goalY) + (tempRobot.getRobotPosX() != goalX) );
        while ((tempRobot.getRobotPosY() != goalY) || (tempRobot.getRobotPosX() != goalX)) {
        	
        	//System.out.println("while loop entered");
        	//System.out.println("temp x, y : " + temp.getX() + ", " + temp.getY());
            if (tempRobot.getRobotPosY() == temp.getY() && tempRobot.getRobotPosX() == temp.getX()) {
            	//System.out.println("if temp 1");
                temp = path.pop();
            }

            targetDir = getTargetDir(tempRobot.getRobotPosY(), tempRobot.getRobotPosX(), tempRobot.getRobotDir(), temp);
            
           // System.out.println("target direction obtained");
            
            MOVEMENT m;
            
           // System.out.println("movement m created");
            
            if (tempRobot.getRobotDir() != targetDir) {
                m = getTargetMovement(tempRobot.getRobotDir(), targetDir);
                System.out.println("robot dir: " + tempRobot.getRobotDir());
            } else {
                m = MOVEMENT.F;
            }

            System.out.println("Movement " + MOVEMENT.print(m) + " from (" + tempRobot.getRobotPosY() + ", " + tempRobot.getRobotPosX() + ") to (" + temp.getY() + ", " + temp.getX() + ")");

            tempRobot.move(m,1, robot.getRealRobot());
            movements.add(m);
            outputString.append(MOVEMENT.print(m));
            System.out.println(outputString);
        }
        
        
       // System.out.println("while loop exited");
        CommunicationMgr comm = CommunicationMgr.getCommMgr();
        if (!robot.getRealRobot() || explorationMode) {
        	//System.out.println("if statement entered");
        	
            for (MOVEMENT x : movements) {
            	//System.out.println("for loop entered: " + x);
            	
                if (x == MOVEMENT.F) {
                    if (!canRobotMoveForward()) {
                        System.out.println("Early termination of fastest path execution.");
                        return "T";
                    }
                   }
                robot.move(x, 1, robot.getRealRobot());
                this.map.repaint();
                

                // During exploration, use sensor data to update map.
                
                if (explorationMode) {
                    robot.setSentors();
                    robot.senseDist(this.map, this.realMap);
                    this.map.repaint();
                }
              //  System.out.println("for loop exited");
            }
        } else {	//real execution here
            int fCount = 0;
            int sCount = 0;
            for (MOVEMENT x : movements) {
            	//can i send 17 at a time?? 
                if (x == MOVEMENT.F) {
                    fCount++;
                    if (fCount == 10) {
                    	
                        robot.move(x, fCount, robot.getRealRobot());
                        
                      //insert the new send map here
                        //String descriptor = String.join(";", Map.generateMapDescriptor(map));
                    	comm.fastestSendMap(robot.getRobotPosX() + "," + robot.getRobotPosY(), robot.getRobotDir(), robot.sendData(x), fCount);
                        
                    	fCount = 0;
                        
                        map.repaint();
                    }
                    
                } else if (x == MOVEMENT.R || x == MOVEMENT.L) {
                    if (fCount > 0) {
                        robot.move(x, fCount, robot.getRealRobot());
                        
                        //insert new send map here
                        //String descriptor = String.join(";", Map.generateMapDescriptor(map));
                    	comm.fastestSendMap(robot.getRobotPosX() + "," + robot.getRobotPosY(),  robot.getRobotDir(), robot.sendData(x), fCount);
                        
                        fCount = 0;
                        map.repaint();
                    }

                    robot.move(x, 1, robot.getRealRobot());	//need to change here?
                    
                    //insert new send map here
                    //String descriptor = String.join(";", Map.generateMapDescriptor(map));
                	comm.fastestSendMap(robot.getRobotPosX() + "," + robot.getRobotPosY(), robot.getRobotDir(), robot.sendData(x), 1);
                    map.repaint();
                }
            }
            
            //SEE IF NEED ANY CHANGES
            if (fCount > 0) {
                robot.move(MOVEMENT.F, fCount, robot.getRealRobot());
                //insert new send map here
                String descriptor = String.join(";", Map.generateMapDescriptor(map));
            	comm.fastestSendMap(robot.getRobotPosX() + "," + robot.getRobotPosY(), robot.getRobotDir(), robot.sendData(MOVEMENT.F), fCount);
                
                
                map.repaint();
            }
        }
        
        
        /*communication part
         * CommunicationMgr comm = CommunicationMgr.getCommMgr();
    	String descriptor = String.join(";", Map.generateMapDescriptor(map));
    	comm.fastestSendMap(robot.getRobotPosX() + "," + robot.getRobotPosY(), descriptor, robot.getRobotDir(), robot.sendData(m), fCount);
        
         */
        
        System.out.println("\nMovements: " + outputString.toString());
       //System.out.println("robot x: "+ robot.getRobotPosX());
       //System.out.println("robot y: "+ robot.getRobotPosY());
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
    /*
    public void printCost() {
        for (int i = 0; i < Constants.MAX_Y; i++) {
            for (int j = 0; j < Constants.MAX_X; j++) {
                System.out.print(gCost[Constants.MAX_Y - 1 - i][j]);
                System.out.print(";");
            }
            System.out.println("\n");
        }
    }
    */

}