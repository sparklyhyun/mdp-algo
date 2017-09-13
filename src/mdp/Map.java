package mdp;

import java.util.*;
import javax.swing.*;
import java.awt.*;

public class Map {
	private final ArrayList<Coordinates> fullMap = new ArrayList<Coordinates>();
	
	public Map(){
		//initialize map
		for(int i=0; i< Constants.MAX_X; i++){
			for(int j=0; j< Constants.MAX_Y; j++ ){
			mdp.Coordinates coordinates = new Coordinates(i,j);
			fullMap.add(coordinates);
			}
		}
		setObstacles();
		setVirtualWall();
	}
	
	public void setObstacles(){
		
	}
	
	public void setVirtualWall(){
		//outer wall
		for(int i = 0; i<fullMap.size(); i++){
			if( fullMap.get(i).getX()<Constants.MAX_X && fullMap.get(i).getY() == 0){
				fullMap.get(i).setIsVirtualWall();
			}
			
		
			
		
		}
	}
	
	public void genMapDescBefore(){
		
	}
	
	
	
}
