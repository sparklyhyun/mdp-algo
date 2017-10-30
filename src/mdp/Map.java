package mdp;

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

public class Map extends JPanel{
	public final Coordinates[][] coordinates = new Coordinates[Constants.MAX_Y][Constants.MAX_X];
	private Robot robot = null;

	public Map(Robot robot) throws IOException{
		this.robot = robot;
		for(int i = 0; i<Constants.MAX_Y; i++){
			for(int j = 0; j<Constants.MAX_X; j++){
				coordinates[i][j] = new Coordinates(i,j);
				//for testing purpose
				//coordinates[i][j].setExplored();
				
			}
		}
		
		
		for(int i=1; i<=2 ; i++){
			for(int j=1; j<=2; j++){
				coordinates[j][i].setExplored();
			}
		}
		
		
		setBoundary();
		if(!robot.getRealRobot()){
			//for testing simulator only 
			readMapDesc();
		}
		
	}
	
	public void setExploredAll(){
		for(int i = 0; i<Constants.MAX_Y; i++){
			for(int j = 0; j<Constants.MAX_X; j++){
				//coordinates[i][j] = new Coordinates(i,j);
				//for testing purpose
				coordinates[i][j].setExplored();
				
			}
		}
	}
	
	/*
	public void removeObstacle(int x, int y){
		coordinates[y][x]
	}*/
	

	public void clearObs(){
		for(int i = 0; i<Constants.MAX_Y; i++){
			for(int j = 0; j<Constants.MAX_X; j++){
				coordinates[i][j] = new Coordinates(i,j);				
			}
		}
	}
	
	public void setVirtualWall(int x, int y){
		boolean withinX;
		boolean withinY;
		
		if(x>=0 && x<Constants.MAX_X){
			withinX = true;
		}else{
			return;
		}
		
		if(y>=0 && y<Constants.MAX_Y){
			withinY = true;
		}else{
			return;
		}
		
		
		//set virtual wall around obstacle
		
		//middle row
		if(withinX){
			if(checkWithinRange(x-1,y) && !(coordinates[y][x-1].getIsVirtualWall() && coordinates[y][x-1].getIsObstacle())){ //left
				coordinates[y][x-1].setIsVirtualWall();
				}
			if(checkWithinRange(x+1,y) && !(coordinates[y][x+1].getIsVirtualWall()&& coordinates[y][x+1].getIsObstacle())){ //right
				coordinates[y][x+1].setIsVirtualWall();
			}			
		}
		
		//top row
		if(withinX && withinY){
			if(checkWithinRange(x-1,y+1) && !(coordinates[y+1][x-1].getIsVirtualWall()&& coordinates[y+1][x-1].getIsObstacle())){ //left
				coordinates[y+1][x-1].setIsVirtualWall();
			}
			if(checkWithinRange(x,y+1) && !(coordinates[y+1][x].getIsVirtualWall()&& coordinates[y+1][x].getIsObstacle())){ //middle
				coordinates[y+1][x].setIsVirtualWall();
			}
			if(checkWithinRange(x+1,y+1) && !(coordinates[y+1][x+1].getIsVirtualWall()&& coordinates[y+1][x+1].getIsObstacle())){ //right
				coordinates[y+1][x+1].setIsVirtualWall();
			}
			
		//bottom row
			if(checkWithinRange(x-1,y-1) && !(coordinates[y-1][x-1].getIsVirtualWall()&& coordinates[y-1][x-1].getIsObstacle())){	//left
				coordinates[y-1][x-1].setIsVirtualWall();
			}
			if(checkWithinRange(x,y-1) && !(coordinates[y-1][x].getIsVirtualWall()&& coordinates[y-1][x].getIsObstacle())){	//middle
				coordinates[y-1][x].setIsVirtualWall();
			}
			if(checkWithinRange(x+1,y-1) && !(coordinates[y-1][x+1].getIsVirtualWall()&& coordinates[y-1][x+1].getIsObstacle())){	//bottom
				coordinates[y-1][x+1].setIsVirtualWall();
			}
		}
		
	}
	
	public void setObstacles(int x, int y){
		//set obstacle
		//System.out.print("Obstacle at y " + y + "\n");
		//System.out.print("Obstacle at x " + x + "\n");
		coordinates[y][x].setObstacle();
		
		//check if within range
		
	}
	
	public void setBoundary(){
		//sets boundary virtual wall
		for(int i=0; i < Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_X; j++){
				if(i==0 || j==0 || i==Constants.MAX_Y-1|| j==Constants.MAX_X-1){
					coordinates[i][j].setIsVirtualWall();
				}
				
			}
		}
		}
	
	
	public boolean isObstacle(int x, int y){
		//System.out.println("weird");
		return (coordinates[y][x].getIsObstacle());

	}
	
	public boolean isExplored(int x, int y){
		return (coordinates[y][x].getIsExplored());
	}
	
	public boolean isVirtualWall(int x, int y){
		return (coordinates[y][x].getIsVirtualWall());
	}
	
	
	public boolean checkWithinRange(int x, int y){
		//System.out.println("Checking if sensor is within boundary");
		//System.out.println("Sensor X position : " + x);
		//System.out.println("Sensor Y position : " + y);
		return (x>=0 && x<Constants.MAX_X && y>= 0 && y<Constants.MAX_Y);
	}
	
	public boolean checkValidCoordinates(int y, int x) {
		return (x>=0 && x<Constants.MAX_X && y>= 0 && y<Constants.MAX_Y);
		
	}
	
	public Coordinates getCoordinateTwo(int y, int x) {
		return coordinates[y][x];
	}
	public Coordinates getCoordinate(int x, int y){
		//System.out.println("hello");
		//System.out.println(x + " , "+y );
		/*
		if(coordinates[y][x] == null){
			System.out.println("coordinate null");
		}
		*/
		return coordinates[y][x];
		
	}
	
	public void setAllExplored(){
		for(int i=0; i<=Constants.MAX_Y; i++){
			for(int j=0; i<=Constants.MAX_X; i++){
				coordinates[i][j].setExplored();
			}
		}
	}
	
	
	private boolean isStartZone(int x, int y){
		return x>=0 && x<=2 && y>=0 && y<= 2; 
	}
	
	private boolean isGoalZone(int y, int x){
		return x<=Constants.GOAL_X+1 && x>=Constants.GOAL_X-1 && y<=Constants.GOAL_Y+1 && y>=Constants.GOAL_Y-1;
	}
	
	
	/*
	public void genMapDescBefore() throws IOException{	//map descriptor with all 0
			File file = new File("beforeMap.txt");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			if(!file.exists()){
				file.createNewFile();
			}
			for(int i = 0; i<Constants.MAX_X; i++){
				for(int j = 0; j<Constants.MAX_Y-1; i++){
					bw.write("0");
				}
				bw.newLine();
			}
			bw.close();
		}
	*/
	
	public void genMapDescAfter() throws IOException{	//map descriptor after exploration
		StringBuilder explored = new StringBuilder();
		StringBuilder obstacle = new StringBuilder();
		
		//System.out.println("genmapdescAfter entered");
		
		for(int i = 0; i<Constants.MAX_Y ; i++){
			for(int j =0; j<Constants.MAX_X; j++){
				if(coordinates[i][j].getIsExplored()){
					explored.append("1");
					if(coordinates[i][j].getIsObstacle()){
						obstacle.append("1");
					}else{
						obstacle.append("0");
					}
				}else{
					explored.append("0");
				}
			}
		}
		
		//System.out.println("string buffer done");
		//System.out.println(explored.toString());
		//System.out.println(obstacle.toString());
		
		
		String exploredMap = explored.toString();
		genDescFile(exploredMap, true);
		genHexFile(exploredMap, true);
		//System.out.println("explored map complete");
		
		String obstacleMap = obstacle.toString();
		genDescFile(obstacleMap, false);
		genHexFile(obstacleMap, false);
	}
		
	public void genDescFile(String s, boolean exp) throws IOException{
		//System.out.println("gendescfile entered");
	      File file;
	      int count = 0;
		
		if(exp){	//explored file
			file = new File("exploredMap.txt");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			//System.out.println("gendesc file initialization");
			if(!file.exists()){
				file.createNewFile();
				//System.out.println("gendescfile new file created");
			}
			//apend 11 in front 
			bw.write("11");
			//bw.newLine();
			//System.out.println("11 appended in front ");
			
			
			for(int i=0; i<Constants.MAP_SIZE; i++){
				bw.write(s.charAt(i));
			}
			
			//append 11 at the back
			bw.write("11");
			bw.close();
			//System.out.println("11 appended at the back");
		}else if(!exp){	//obstacle file
			file = new File("obstacleMap.txt");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			//System.out.println("obstacle file gen initialization");
			if(!file.exists()){
				file.createNewFile();
			}
			bw.write(s);
			bw.close();
		}
	}
	
	public void genHexFile(String s, boolean exp){
		//System.out.println("genhexfile entered");
		StringBuilder convert = new StringBuilder();
		StringBuilder converted = new StringBuilder();
		int i=0;
		File file;
		//System.out.println("genhexfile initiated");
		
		//padding bits at the back (if needed) 		
		int remainder = s.length()%4;
		//System.out.println(remainder);
		if(remainder != 0){
			for(int k = 0; k< remainder; k++){
				s.concat("0"); 
			}
			//System.out.println("concatenated: " + s);
		}
		//System.out.println(s);
		//convert string to hex
		while(i<s.length()){
			for(int j = 0; j < 4; j++){
				//System.out.println("for loop entered");
				convert.append(s.charAt(i));
				//System.out.println("convert = " + convert );
				i++;
			}
			converted.append(convertToHex(convert));
			//System.out.println("converted:" + converted.toString());
			convert.setLength(0);		//empty string builder 
			i++;
		}
		
		if(exp){
		try{
			
			file = new File("HexMapExplored.txt");
			//System.out.println("hex file created");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			if(!file.exists()){
				file.createNewFile();
			}
			bw.write(converted.toString());
			//System.out.println("hex file written");
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		}else{
		try{
			file = new File("HexMapObstacle.txt");
			//System.out.println("hex file created");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			if(!file.exists()){
				file.createNewFile();
			}
			bw.write(converted.toString());
			//System.out.println("hex file written");
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		}
		
			
	}
	
	public String convertToHex(StringBuilder s){
		//System.out.println("convert to hex entered");
		String returnString = "";
		
		switch(s.toString()){
		case "0000":
			returnString = "0";
			//System.out.println("returnString = " + returnString);
			break;
		case "0001":
			returnString = "1";
			//System.out.println("returnString = " + returnString);
			break;
		case "0010":
			returnString = "2";
			//System.out.println("returnString = " + returnString);
			break;
		case "0011":
			returnString = "3";
			//System.out.println("returnString = " + returnString);
			break;
		case "0100":
			returnString = "4";
			//System.out.println("returnString = " + returnString);
			break;
		case "0101":
			returnString = "5";
			//System.out.println("returnString = " + returnString);
			break;
		case "0110":
			returnString = "6";
			//System.out.println("returnString = " + returnString);
			break;
		case "0111":
			returnString = "7";
			//System.out.println("returnString = " + returnString);
			break;
		case "1000":
			returnString = "8";
			//System.out.println("returnString = " + returnString);
			break;
		case "1001":
			returnString = "9";
			//System.out.println("returnString = " + returnString);
			break;
		case "1010":
			returnString = "A";
			//System.out.println("returnString = " + returnString);
			break;
		case "1011":
			returnString = "B";
			//System.out.println("returnString = " + returnString);
			break;
		case "1100":
			returnString = "C";
			//System.out.println("returnString = " + returnString);
			break;
		case "1101":
			returnString = "D";
			//System.out.println("returnString = " + returnString);
			break;
		case "1110":
			returnString = "E";
			//System.out.println("returnString = " + returnString);
			break;
		case "1111":
			returnString = "F";
			//System.out.println("returnString = " + returnString);
			break;
		}
		return returnString;
	}
	
	public void readMapDesc() throws IOException{	//read text file & put in coordinates array		
		int x = 0;	//x coordinate of map
		int y = 0;	//y coordinate of map
		int i = 0;
		String ss = "1";
		char c = ss.charAt(0);	//cast string to char
		
		try{			
			File file = new File("SampleArena3.txt");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			StringBuffer sb = new StringBuffer();
			String s;
			
			while((s = br.readLine())!= null){
				sb.append(s);
				sb.append("\n"); //end of line character
			}
			br.close();
			fr.close();
			
			//test
			System.out.print(sb);
			
			System.out.print("\n");
			i = 0;
			System.out.print("Map size : " + Constants.MAP_SIZE+"\n");
			System.out.print("Constants.MAX_Y : "+ Constants.MAX_Y + "\n");
			System.out.print("Constants.MAX_X : " + Constants.MAX_X + "\n");
			
			//System.out.print("Test\n");
			
			
			for (y = 0; y<Constants.MAX_Y; y++){
				for (x = 0; x < Constants.MAX_X; x++){
					if (sb.charAt(i) == c){
						setObstacles(x,y);
						//System.out.print("1");
					}
					else{
						//System.out.print("0");
					}
					i++;
				}
				//System.out.print("\n");
				i++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	 public static void loadMapFromDisk(Map map, String filename) {
	        try {
	            InputStream inputStream = new FileInputStream( filename + ".txt");
	            BufferedReader buf = new BufferedReader(new InputStreamReader(inputStream));

	            String line = buf.readLine();
	            StringBuilder sb = new StringBuilder();
	            while (line != null) {
	                sb.append(line);
	                line = buf.readLine();
	            }

	            String bin = sb.toString();
	            int binPtr = 0;
	            for (int row = Constants.MAX_Y - 1; row >= 0; row--) {
	                for (int col = 0; col < Constants.MAX_X; col++) {
	                    if (bin.charAt(binPtr) == '1') map.setObstacles(col, row);
	                    binPtr++;
	                }
	            }

	            map.setAllExplored();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
	
	//map desc for communication mgr
	 public static String[] generateMapDescriptor(Map map) {
	        String[] ret = new String[2];

	        StringBuilder Part1 = new StringBuilder();
	        StringBuilder Part1_bin = new StringBuilder();
	        Part1_bin.append("11"); //remove padding
	        for (int i = 0; i < Constants.MAX_Y; i++) {
	            for (int j = 0; j < Constants.MAX_X; j++) {
	                if (map.getCoordinate(j, i).getIsExplored())
	                    Part1_bin.append("1");
	                else
	                    Part1_bin.append("0");

	                if (Part1_bin.length() == 4) {
	                    Part1.append(binToHex(Part1_bin.toString()));
	                    Part1_bin.setLength(0);
	                }
	            }
	        }
	        Part1_bin.append("11");
	        Part1.append(binToHex(Part1_bin.toString()));
	        System.out.println("P1: " + Part1.toString());
	        ret[0] = Part1.toString();

	        StringBuilder Part2 = new StringBuilder();
	        StringBuilder Part2_bin = new StringBuilder();
	        /*
	        for (int i = 0; i < Constants.MAX_Y; i++) {
	            for (int j = 0; j < Constants.MAX_X; j++) {
	                if (map.getCoordinate(j, i).getIsExplored()) {
	                    if (map.getCoordinate(j, i).getIsObstacle())
	                        Part2_bin.append("1");
	                    else
	                        Part2_bin.append("0");

	                    if (Part2_bin.length() == 4) {
	                        Part2.append(binToHex(Part2_bin.toString()));
	                        Part2_bin.setLength(0);
	                    }
	                }else{//set 0
	                	Part2_bin.append("0");
	                }
	            }
	        }
	        */
	        for (int i = 0; i < Constants.MAX_Y; i++) {
	            for (int j = 0; j < Constants.MAX_X; j++) {
	                if (map.getCoordinate(j, i).getIsExplored()) {
	                	if(map.getCoordinate(j, i).getIsObstacle()) 
	                		Part2_bin.append("1");
	                	else
	                		Part2_bin.append("0");
	                		
	                }
	                else
	                    Part2_bin.append("0");

	                if (Part2_bin.length() == 4) {
	                    Part2.append(binToHex(Part2_bin.toString()));
	                    Part2_bin.setLength(0);
	                }
	            }
	        }
	        if (Part2_bin.length() > 0) Part2.append(binToHex(Part2_bin.toString()));
	        System.out.println("P2: " + Part2.toString());
	        ret[1] = Part2.toString();

	        return ret;
	    }
	 
	 private static String binToHex(String bin) {
	        int dec = Integer.parseInt(bin, 2);

	        return Integer.toHexString(dec);
	    }
	
	//GUI 
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		GuiCell[][] guiCells = new GuiCell[Constants.MAX_Y][Constants.MAX_X]; //name??
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_X; j++){
				guiCells[i][j] = new GuiCell(j*Constants.CELL_SIZE, i*Constants.CELL_SIZE, Constants.CELL_SIZE);
			}
		}
		
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_X; j++){
				Color cellColor;
				if(isStartZone(i,j)){
					cellColor = Constants.COLOR_START;
				}else if(isGoalZone(i,j)){
					cellColor = Constants.COLOR_GOAL;
				}else{
					if(!coordinates[i][j].getIsExplored()){
						cellColor = Constants.COLOR_UNEXP;
					}else if(coordinates[i][j].getIsObstacle()){
						cellColor = Constants.COLOR_OBS;
					}else{
						cellColor = Constants.COLOR_FREE;
					}
				}
				g.setColor(cellColor);
				g.fillRect(guiCells[i][j].x + Constants.MAPX_OFFSET, guiCells[i][j].y, guiCells[i][j].cellSize, guiCells[i][j].cellSize);
			}
		}
	
		g.setColor(Constants.COLOR_ROBOT);
		int rx = robot.getRobotPosX();
		int ry = robot.getRobotPosY();
		g.fillOval((rx-1)*Constants.CELL_SIZE + Constants.MAPX_OFFSET + Constants.ROBOTX_OFFSET , Constants.MAPY - (ry * Constants.CELL_SIZE + Constants.ROBOTY_OFFSET), Constants.ROBOT_W, Constants.ROBOT_H);
		
		//to see robot direction
		g.setColor(Constants.ROBOT_DIR);
		Constants.DIRECTION d = robot.getRobotDir();
		switch(d){
		case N:g.fillOval(rx*Constants.CELL_SIZE + 10 + Constants.MAPX_OFFSET, Constants.MAPY - ry*Constants.CELL_SIZE - 15, Constants.ROBOT_W_DIR, Constants.ROBOT_H_DIR);
			break;
		case E:g.fillOval(rx*Constants.CELL_SIZE + 35 + Constants.MAPX_OFFSET, Constants.MAPY - ry*Constants.CELL_SIZE + 10, Constants.ROBOT_W_DIR, Constants.ROBOT_H_DIR);
			break;
		case S:g.fillOval(rx*Constants.CELL_SIZE + 10 + Constants.MAPX_OFFSET, Constants.MAPY - ry*Constants.CELL_SIZE + 35, Constants.ROBOT_W_DIR, Constants.ROBOT_H_DIR);
			break;
		case W:g.fillOval(rx*Constants.CELL_SIZE + 15 + Constants.MAPX_OFFSET, Constants.MAPY - ry*Constants.CELL_SIZE + 10, Constants.ROBOT_W_DIR, Constants.ROBOT_H_DIR);
			break;
		}
	}
	
	private class GuiCell{
		public final int x;
		public final int y;
		public final int cellSize;
		
		public GuiCell(int borderX, int borderY, int borderSize){
			this.x = borderX + Constants.OUTLINE;
			this.y = Constants.MAPY - (borderY - Constants.OUTLINE);
			this.cellSize = borderSize - (Constants.OUTLINE * 2);
			
		}
	}
}