package mdp;

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

public class Map extends JPanel{
	private final Coordinates[][] coordinates = new Coordinates[Constants.MAX_Y][Constants.MAX_X];
	private Robot robot = null;

	public Map(Robot robot) throws IOException{
		this.robot = robot;
		for(int i = 0; i<Constants.MAX_Y; i++){
			for(int j = 0; j<Constants.MAX_X; j++){
				coordinates[i][j] = new Coordinates(i,j);
				
			}
		}
		setBoundary();
		readMapDesc(); 	//set obstacles from the map descriptor
		
		
		
	}
	
	public void setObstacles(int x, int y){
		//set obstacle
		coordinates[y][x].setObstacle();
		
		//check if within range
		boolean withinX = false;
		boolean withinY = false;
		
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
	
	public void setBoundary(){
		//sets boundary virtual wall
		for(int i=0; i < Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_X; j++){
				if(i==0 || j==0 || i==Constants.MAX_X-1|| j==Constants.MAX_Y-1){
					coordinates[i][j].setIsVirtualWall();
				}
				
			}
		}
		}
	
	
	public boolean isObstacle(int x, int y){
		return (coordinates[y][x].getIsObstacle());

	}
	
	public boolean isExplored(int x, int y){
		return (coordinates[y][x].getIsExplored());
	}
	
	public boolean isVirtualWall(int x, int y){
		return (coordinates[y][x].getIsVirtualWall());
	}
	
	
	public boolean checkWithinRange(int x, int y){
		return (x>=0 && x<Constants.MAX_X && y>= 0 && y<Constants.MAX_Y);
	}
	
	public Coordinates getCoordinate(int x, int y){
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
	
	
	public void genMapDescAfter() throws IOException{	//map descriptor after exploration
		StringBuilder explored = new StringBuilder();
		StringBuilder obstacle = new StringBuilder();
		

		
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
		

		
		String exploredMap = explored.toString();
		genDescFile(exploredMap, true);
		genHexFile(exploredMap);
		
		String obstacleMap = obstacle.toString();
		genDescFile(obstacleMap, false);
		genHexFile(obstacleMap);
	}
		
	public void genDescFile(String s, boolean exp) throws IOException{
	      File file;
	      int count = 0;
		
		if(exp == true){	//explored file
			file = new File("exploredMap.txt");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			if(!file.exists()){
				file.createNewFile();
			}
			//apend 11 in front 
			bw.write("11");
			bw.newLine();
			
			for(int i = 0; i<Constants.MAX_Y; i++){
				for(int j = 0; j<Constants.MAX_X; i++){
					bw.write(s.charAt(count));
					count++;
				}
				bw.newLine();
			}
			//append 11 at the back
			bw.write("11");
			bw.close();
			
		}
		
		if(exp = false){	//obstacle file
			file = new File("obstacleMap.txt");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			if(!file.exists()){
				file.createNewFile();
			}
			bw.write(s);
			bw.close();
		}
	}
	
	public void genHexFile(String s){
		StringBuilder convert = new StringBuilder();
		StringBuilder converted = new StringBuilder();
		int i=0;
		File file;
		
		//padding bits at the back (if needed) 
		if(s.length()%4 != 0){
			int remainder = s.length()%4;
			for(int k = 0; k<= remainder-1; k++){
				s.concat("0"); 
			}
			
		}
		//convert string to hex
		while(i<s.length()){
			for(int j = 0; j <= 4; j++){
				convert.append(s.charAt(i));
			}
			converted.append(convertToHex(convert));
			convert.setLength(0);		//empty string builder 
			i++;
		}
		
		try{
			
			file = new File("HexMap.txt");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			if(!file.exists()){
				file.createNewFile();
			}
			bw.write(converted.toString());
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}
	
	public String convertToHex(StringBuilder s){
		String returnString = "";
		
		if(s.equals("0000")){
			returnString = "0";
		}
		if(s.equals("0001")){
			returnString = "1";
		}
		if(s.equals("0010")){
			returnString = "2";
		}
		if(s.equals("0011")){
			returnString = "3";
		}
		if(s.equals("0100")){
			returnString = "4";
		}
		if(s.equals("0101")){
			returnString = "5";
		}
		if(s.equals("0110")){
			returnString = "6";
		}
		if(s.equals("0111")){
			returnString = "7";
		}
		if(s.equals("1000")){
			returnString = "8";
		}
		if(s.equals("1001")){
			returnString = "9";
		}
		if(s.equals("1010")){
			returnString = "A";
		}
		if(s.equals("1011")){
			returnString = "B";
		}
		if(s.equals("1100")){
			returnString = "C";
		}
		if(s.equals("1101")){
			returnString = "D";
		}
		if(s.equals("1110")){
			returnString = "E";
		}
		if(s.equals("1111")){
			returnString = "F";
		}

		return returnString;
	}
	
	public void readMapDesc() throws IOException{	//read text file & put in coordinates array	
		//FileInputStream f = null; 
		//InputStreamReader isr = null;
		
		int i = 0;
		int x = 0;	//x coordinate of map
		int y = 0;	//y coordinate of map

		String ss = "1";
		char c = ss.charAt(0);	//cast string to char
		
		try{			
			File file = new File("testMap.txt");
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
			//System.out.print(sb);
			
			while(i<Constants.MAP_SIZE){
				if(sb.charAt(i) == c){
					setObstacles(x,y);
				}
				if(x<Constants.MAX_X){
					x++;
				}
				else{
					x = 0;
				}
				if(y<Constants.MAX_Y && x == Constants.MAX_X){
					y++;
				}
				else if(y==Constants.MAX_Y-1 && x == Constants.MAX_X-1){
					break;
				}
				i++;
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//GUI 
	public void paintComponent(Graphics g){
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
