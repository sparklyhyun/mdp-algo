package mdp;

public class Coordinates {
	private final int x;
	private final int y;
	private boolean isObstacle = false;
	private boolean isExplored = false;
	
	public Coordinates(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public void setObstacleTrue(){
		isObstacle = true;
	}
	
	public void setExploredTrue(){
		isExplored = true;
	}
	
	public boolean getIsObstacle(){
		return isObstacle;
	}
	
	public boolean getIsExplored(){
		return isExplored;
	}
		
	
}
