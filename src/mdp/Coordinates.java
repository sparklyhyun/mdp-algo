package mdp;

public class Coordinates {
	private final int x;
	private final int y;
	private boolean isObstacle;
	private boolean isExplored;
	
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
	
	public void setObstacle(){
		isObstacle = true;
	}
	
	public void setExplored(){
		isExplored = true;
	}
		
	
}
