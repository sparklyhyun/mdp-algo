package mdp;

public class Coordinates {
	private final int x;
	private final int y;
	private boolean isObstacle = false;
	private boolean isObs2 = false; 
	private boolean isExplored = false;
	private boolean isVirtualWall = false;
	private boolean isWayPoint = false;
	private boolean frontS = false;
	
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
		isObs2 = true;
	}
	
	public void removeObstacle(){
		isObstacle = false;
	}

	public void removeVirtualWall(){
		isVirtualWall = false; 
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
	
	public void setFrontS(){
		frontS = true;
	}
	
	public boolean getIsObstacle(){
		//System.out.println("so weird: " + isObstacle);
		return isObstacle;
	}
	
	
	/*
	public boolean getIsObstacle(){
		return isObs2;
	}*/
	
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
	
	public boolean getFrontS(){
		return frontS;
	}
}
