package mdp;

import mdp.Coordinates;
import mdp.Constants;
import java.util.ArrayList;

public class Map {
	private final ArrayList<Coordinates> fullMap = new ArrayList<Coordinates>();
	
	public Map(){
		//initialize map
		for(int i=1; i<= Constants.MAX_X; i++){
			for(int j=1; j<=Constants.MAX_Y; j++ ){
			mdp.Coordinates coordinates = new Coordinates(i,j);
			fullMap.add(coordinates);
			}
		}
	}
	
	public void setObstacles(){
		//for now test

		fullMap.get(38).setObstacleTrue();

		fullMap.get(7).setObstacleTrue();
		
	}
	
	public void setVirtualWall(){
		//set boundary virtual wall
		
		for(int i = 0; i < fullMap.size(); i++){
			if((fullMap.get(i).getX() > 0 && fullMap.get(i).getX() < Constants.MAX_X) && (fullMap.get(i).getY() == 1 || fullMap.get(i).getY() == 18 )){
				fullMap.get(i).setVirtualWallTrue();
					}
			if((fullMap.get(i).getY() > 0 && fullMap.get(i).getY() < Constants.MAX_Y) && (fullMap.get(i).getX() == 1 || fullMap.get(i).getX() == 13 ) ){
				fullMap.get(i).setVirtualWallTrue();
					}
		}
}
}
