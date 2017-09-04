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
}
