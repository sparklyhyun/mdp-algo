package mdp;

import Map;
import Constants;
import Robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Simulator {
	private static JFrame _mapFrame = null;          // JFrame to display Map

	private static JPanel _mapView = null;           // JPanel for map views
	private static JPanel _mapbuttons = null;        // JPanel for buttons

	private static Robot robot;

	private static Map realMap = null;              // real map
	private static Map exploredMap = null;          // exploration map

	private static int timeLimit = 3600;            // time limit
	private static int coverageLimit = 300;         // coverage limit


	private static final boolean realExecution = true;


	public static void main(String[] args) {
	if (realExecution) comm.openConnection();   //connection funtion to be added!!!

	robot = new Robot(Constants.START_X, Constants.START_Y, realExecution);

	if (!realExecution) {
	    realMap = new Map(robot);
	    realMap.setAllUnexplored();
	}

	exploredMap = new Map(robot);
	exploredMap.setAllUnexplored();

	viewFullMap();
	}	
	
	private static void viewFullMap() {
		_mapFrame = new JFrame();
		_mapFrame.setTitle("Group 9 MDP Simulator");
		_mapFrame.setSize(new Dimension(690, 700));
		_mapFrame.setResizable(false);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		_mapFrame.setLocation(dimension.width / 2 - _mapFrame.getSize().width / 2, dimension.height / 2 - _mapFrame.getSize().height / 2);
		
	

}
