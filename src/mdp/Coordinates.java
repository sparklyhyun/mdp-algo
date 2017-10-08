package mdp;

public class Coordinates {
	private final int x;
	private final int y;
	private boolean isObstacle = false;
	private boolean isExplored = false;
	private boolean isVirtualWall = false;
	private boolean isWayPoint = false;
	
	public Coordinates(int i, int j){
		this.x = j;
		this.y = i;
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
	public void setUnExplored(){
		isExplored = false;
	}

	public void setIsVirtualWall(){
		isVirtualWall = true;
	}
	
	public boolean getIsObstacle(){
		return isObstacle;
	}
	
	public boolean getIsExplored(){
		return isExplored;
	}
	
	public boolean getIsVirtualWall(){
		return isVirtualWall;
	}

	public void setIsWayPoint(){
		isWayPoint = true;
	}
		
	public boolean getIsWayPoint(){
		return isWayPoint;
	}
	
}
