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
				//setObstacles();
				setBoundary();
			}
		}
		
		
		
		
	}
	
	public void setObstacles(int x, int y, boolean obstacle){
		//set obstacle
		coordinates[x][y].setObstacle();
		
		//set virtual wall around obstacle
		
		//middle row
		if(x>=1){
			if(!coordinates[x-1][y].getIsVirtualWall()){ //left
				coordinates[x-1][y].setIsVirtualWall();
				}
			if(!coordinates[x+1][y].getIsVirtualWall()){ //right
				coordinates[x+1][y].setIsVirtualWall();
			}			
		}
		
		//top row
		if(y>=1){
			if(!coordinates[x-1][y+1].getIsVirtualWall()){ //left
				coordinates[x-1][y+1].setIsVirtualWall();
			}
			if(!coordinates[x][y+1].getIsVirtualWall()){ //middle
				coordinates[x][y+1].setIsVirtualWall();
			}
			if(!coordinates[x+1][y+1].getIsVirtualWall()){ //right
				coordinates[x+1][y+1].setIsVirtualWall();
			}
			
		}
		
		if()
		
		
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
	
	
	
	
	public void genMapDescBefore(){
		
	}
	

}
