package mdp;

import java.util.*;

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
		
	}
	
	public void generateMapDescriptor(){
		final Formatter x;
		
		try{
			x = new Formatter("testMap.txt");
			//System.out.println("file created");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	}
	
}
