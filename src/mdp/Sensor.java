package mdp;

public class Sensor {
	private int minRange;
	private int maxRange;
	private int x;	//x coordinate of sensor
	private int y;	//y coordinate of sensor
	private String id;
	public enum DIRECTION{N,S,E,W};	
	
	private DIRECTION dir = DIRECTION.N;	// can change later
	
	
	public Sensor(int minRange, int maxRange, int x, int y, String id, DIRECTION dir){
		this.minRange = minRange;
		this.maxRange = maxRange;
		this.x = x;
		this.y = y;
		this.id = id;
		this.dir = dir;
	}
	
	public void setSensor(int x, int y, DIRECTION dir){
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
	
}
