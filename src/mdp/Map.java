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
		}else{
			return;
		}
		
		if(y>=1 && y<=Constants.MAX_Y-1){
			withinY = true;
		}else{
			return;
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
	
	
	public boolean isObstacle(int x, int y){
		return (coordinates[x][y].getIsObstacle());

	}
	
	public boolean isExplored(int x, int y){
		return (coordinates[x][y].getIsExplored());
	}
	
	public boolean isVirtualWall(int x, int y){
		return (coordinates[x][y].getIsVirtualWall());
	}
	
	
	public boolean checkWithinRange(int x, int y){
		return (x>=0 && x<Constants.MAX_X && y>= 0 && y<Constants.MAX_Y);
	}
	
	public Coordinates getCoordinate(int x, int y){
		return coordinates[x][y];
	}
	
	public void setAllExplored(){
		for(int i=0; i<=Constants.MAX_X; i++){
			for(int j=0; i<=Constants.MAX_Y; i++){
				coordinates[i][j].setExplored();
			}
		}
	}
	
	
	
	
	public void genMapDescBefore() throws IOException{	//map descriptor with all 0
			File file = new File("beforeMap.txt");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			if(!file.exists()){
				file.createNewFile();
			}

			for(int i = 0; i<=Constants.MAX_Y-1; i++){
				for(int j = 0; j<=Constants.MAX_X-1; i++){
					bw.write("0");
				}
				bw.newLine();
			}
			bw.close();
		}
	
	
	public void genMapDescAfter() throws IOException{	//map descriptor after exploration
		StringBuilder explored = new StringBuilder();
		StringBuilder obstacle = new StringBuilder();
		

		
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
			
			for(int i = 0; i<=Constants.MAX_Y-1; i++){
				for(int j = 0; j<=Constants.MAX_X-1; i++){
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
			String s;
			
			while((s = br.readLine())!= null){
				if(s.charAt(i) == c){
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
				i++;
			}
			br.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
