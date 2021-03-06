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
				//for testing purpose
				coordinates[i][j].setExplored();
				
			}
		}
	}
	

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
		coordinates[y][x].setObstacle();
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
	
	public boolean checkValidCoordinates(int y, int x) {
		return (x>=0 && x<Constants.MAX_X && y>= 0 && y<Constants.MAX_Y);
		
	}
	
	public Coordinates getCoordinateTwo(int y, int x) {
		return coordinates[y][x];
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
	
	
	public void readMapDesc() throws IOException{	//read text file & put in coordinates array		
		int x = 0;	//x coordinate of map
		int y = 0;	//y coordinate of map
		int i = 0;
		String ss = "1";
		char c = ss.charAt(0);	//cast string to char
		
		try{			
			File file = new File("SampleArena4.txt");
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
			
			for (y = 0; y<Constants.MAX_Y; y++){
				for (x = 0; x < Constants.MAX_X; x++){
					if (sb.charAt(i) == c){
						setObstacles(x,y);
					}
					else{
					}
					i++;
				}
				i++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	//map desc for communication mgr
	 public static String[] generateMapDescriptor(Map map) {
	        String[] fin = new String[2];

	        StringBuilder Part1 = new StringBuilder();
	        StringBuilder Part1_bin = new StringBuilder();
	        Part1_bin.append("11"); 
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
	        fin[0] = Part1.toString();

	        StringBuilder Part2 = new StringBuilder();
	        StringBuilder Part2_bin = new StringBuilder();
	
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
	        fin[1] = Part2.toString();

	        return fin;
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