package mdp;

import java.util.*;
import javax.swing.*;
import java.awt.*;

public class Map extends JPanel{
	//private final ArrayList<Coordinates> fullMap = new ArrayList<Coordinates>();
	private final Coordinates[][] coordinates = new Coordinates[Constants.MAX_X][Constants.MAX_Y];
	
	
	public Map(){
		//initialize map
		/*
		for(int i=0; i< Constants.MAX_X; i++){
			for(int j=0; j< Constants.MAX_Y; j++ ){
			mdp.Coordinates coordinates = new Coordinates(i,j);
			fullMap.add(coordinates);
			}
		}
		*/
		
		for(int i = 0; i<Constants.MAX_X; i++){
			for(int j = 0; j<Constants.MAX_Y; j++){
				coordinates[i][j] = new Coordinates(i,j);
				setBoundary();
			}
		}
		
		
		
		
	}
	
	public void setObstacles(int x, int y, boolean obstacle){
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
	
	public void readMapDesc(){
		
	}
	

}
