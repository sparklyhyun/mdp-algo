package mdp;

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

public class Map extends JPanel{
	private final Coordinates[][] coordinates = new Coordinates[Constants.MAX_X][Constants.MAX_Y];
	
	
	public Map() throws IOException{
		
		for(int i = 0; i<Constants.MAX_X; i++){
			for(int j = 0; j<Constants.MAX_Y; j++){
				coordinates[i][j] = new Coordinates(i,j);
				
			}
		}
		setBoundary();
		readMapDesc(); 	//set obstacles from the map descriptor
		
		
	}
	
	public void setObstacles(int x, int y){
		//set obstacle
		coordinates[x][y].setObstacle();
		
		//check if within range
		boolean withinX = false;
		boolean withinY = false;
		
		if(x>=1 && x<=Constants.MAX_X-1){
			withinX = true;
		}
		if(y>=1 && y<=Constants.MAX_Y-1){
			withinY = true;
		}
		
		
		//set virtual wall around obstacle
		
		//middle row
		if(withinX){
			if(!(coordinates[x-1][y].getIsVirtualWall() && coordinates[x-1][y].getIsObstacle())){ //left
				coordinates[x-1][y].setIsVirtualWall();
				}
			if(!(coordinates[x+1][y].getIsVirtualWall()&& coordinates[x+1][y].getIsObstacle())){ //right
				coordinates[x+1][y].setIsVirtualWall();
			}			
		}
		
		//top row
		if(withinX && withinY){
			if(!(coordinates[x-1][y+1].getIsVirtualWall()&& coordinates[x-1][y+1].getIsObstacle())){ //left
				coordinates[x-1][y+1].setIsVirtualWall();
			}
			if(!(coordinates[x][y+1].getIsVirtualWall()&& coordinates[x][y+1].getIsObstacle())){ //middle
				coordinates[x][y+1].setIsVirtualWall();
			}
			if(!(coordinates[x+1][y+1].getIsVirtualWall()&& coordinates[x+1][y+1].getIsObstacle())){ //right
				coordinates[x+1][y+1].setIsVirtualWall();
			}
			
		//bottom row
			if(!(coordinates[x-1][y-1].getIsVirtualWall()&& coordinates[x-1][y-1].getIsObstacle())){	//left
				coordinates[x-1][y-1].setIsVirtualWall();
			}
			if(!(coordinates[x][y-1].getIsVirtualWall()&& coordinates[x][y-1].getIsObstacle())){	//middle
				coordinates[x][y-1].setIsVirtualWall();
			}
			if(!(coordinates[x+1][y-1].getIsVirtualWall()&& coordinates[x-1][y-1].getIsObstacle())){	//bottom
				coordinates[x+1][y-1].setIsVirtualWall();
			}
		}
	}
	
	public void setBoundary(){
		//sets boundary virtual wall
		for(int i=0; i < Constants.MAX_X; i++){
			for(int j=0; j<Constants.MAX_Y; j++){
				if(i==0 || j==0 || i==Constants.MAX_X-1|| j==Constants.MAX_Y-1){
					coordinates[i][j].setIsVirtualWall();
				}
				
			}
		}
		}
	
	
	
	
	public void genMapDescBefore(){	//map descriptor with all 0
		
	}
	
	public void genMapDescAfter() throws IOException{	//map descriptor after exploration
		StringBuilder explored = new StringBuilder();
		StringBuilder obstacle = new StringBuilder();
		
		//apend 11 in front 
		explored.append("11");
		
		for(int i = 0; i<=Constants.MAX_X-1 ; i++){
			for(int j =0; j<=Constants.MAX_Y-1; j++){
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
		
		//append 11 at the back
		explored.append("11");
		
		String exploredMap = explored.toString();
		genDescFile(exploredMap, true);
		genHexFile(exploredMap);
		
		String obstacleMap = obstacle.toString();
		genDescFile(obstacleMap, false);
		genHexFile(obstacleMap);
	}
	
	public void genDescFile(String s, boolean exp) throws IOException{
		FileOutputStream fop = null;
		File file;
		
		if(exp == true){	//explored file
			file = new File("exploredMap.txt");
			fop = new FileOutputStream(file);
			if(!file.exists()){
				file.createNewFile();
			}
			
		}
		
		if(exp = false){	//obstacle file
			file = new File("obstacleMap.txt");
			fop = new FileOutputStream(file);
			if(!file.exists()){
				file.createNewFile();
			}
			
		}
	}
	
	public void genHexFile(String s){
		StringBuilder convert = new StringBuilder();
		StringBuilder converted = new StringBuilder();
		int i=0;
		
		//padding bits at the back (if needed) 
		if(s.length()%4 != 0){
			int remainder = s.length()%4;
			for(int k = 0; k<= remainder-1; k++){
				
			}
			
		}
		//convert string to hex
		while(i<s.length()){
			for(int j = 0; j <= 4; j++){
				
			}
			convert.append(s.charAt(i));
			converted.append(convertToHex(convert));
			i++;
		}
		
		try{
			//file creation
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
		//int i;
		
		int x = 0;	//x coordinate of map
		int y = 0;	//y coordinate of map

		
		try{
			/*
			f = new FileInputStream("testMap.txt");
			isr = new InputStreamReader(f);
			
			//read till end of the file
			while((i = isr.read())!= -1){
				if(i == 1){
					setObstacles(x,y);
				}
				if(x<=Constants.MAX_X-1){
					x++;
				}
				else{
					x = 0;
				}
				if(y<=Constants.MAX_Y-1 && x == Constants.MAX_X-1){
					y++;
				}
				else if(y==Constants.MAX_Y-1 && x == Constants.MAX_X-1){
					break;
				}
				*/
			File file = new File("testMap.txt");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			
			while((line = br.readLine())!= null){
				if(line == "1"){
					setObstacles(x,y);
				}
				if(x<=Constants.MAX_X-1){
					x++;
				}
				else{
					x = 0;
				}
				if(y<=Constants.MAX_Y-1 && x == Constants.MAX_X-1){
					y++;
				}
				else if(y==Constants.MAX_Y-1 && x == Constants.MAX_X-1){
					break;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}

		
		
	}
	

	

}
