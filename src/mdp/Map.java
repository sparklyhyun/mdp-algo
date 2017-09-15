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
	
	public void setObstacles(){
		//for now test
		//fullMap.get(38).setObstacleTrue();
		//fullMap.get(7).setObstacleTrue();
		
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
