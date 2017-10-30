package mdp;

import javax.swing.*;

import mdp.Constants.MOVEMENT;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class Simulator {
	private static JFrame _mapFrame = null;          // JFrame to display Map

	private static JPanel _mapTiles = null;           // JPanel for map Tiles
	private static JPanel _mapButtons = null;        // JPanel for map buttons

	private static Robot robot;

	private static Map realMap;              // real map
	private static Map exploredMap;          // exploration map

	private static int timeLimit = 3600;            // time limit
	private static int coverageLimit = 300;         // coverage limit
	private static int explorationMode;
	private static int robotDelay = 100;
	
	private static int waypointX = 13;	//for now
	private static int waypointY = 10; 	//for now
	
	
	private static boolean started = false;
	
	private static final CommunicationMgr comm = CommunicationMgr.getCommMgr();

	private static final boolean realExecution = false; //for now, not real map


	public static void main(String[] args) throws IOException {
		if (realExecution) comm.openConnection();   //connection function to be added!!!
		
		robot = new Robot(Constants.START_X, Constants.START_Y, realExecution);  

		if (!realExecution) {
		    realMap = new Map(robot); 
		}
		if(!started){
			exploredMap = new Map(robot);
			started = true;
		}
		exploredMap = new Map(robot);
		viewFullMap();
                              
	}	
	
	
	private static void viewFullMap() {
		_mapFrame = new JFrame();
		_mapFrame.setTitle("Group 9 MDP Simulator");
		_mapFrame.setSize(new Dimension(690, 700));
		_mapFrame.setResizable(false);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		_mapFrame.setLocation(dimension.width / 2 - _mapFrame.getSize().width / 2, dimension.height / 2 - _mapFrame.getSize().height / 2);
        
		// Create the CardLayout for storing the different maps
		_mapTiles = new JPanel(new CardLayout());

		// Create the JPanel for the buttons
		_mapButtons = new JPanel();

		// Add _mapTiles & _mapButtons to the main frame's content pane
		Container contentPane = _mapFrame.getContentPane();
		contentPane.add(_mapTiles, BorderLayout.CENTER);
		contentPane.add(_mapButtons, BorderLayout.PAGE_END);

		// Initialize the main map layout
		initMainLayout();

		// Initialize the map buttons
		initButtonsLayout();

		// View full display of the application
		_mapFrame.setVisible(true);
		_mapFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }
	
	private static void initMainLayout() {
		if (!realExecution) {
		    _mapTiles.add(realMap, "REAL_MAP");
		}
		_mapTiles.add(exploredMap, "EXPLORATION");

		CardLayout c = ((CardLayout) _mapTiles.getLayout());
		if (!realExecution) {
		    c.show(_mapTiles, "REAL_MAP");
		} else {
		    c.show(_mapTiles, "EXPLORATION");
		}
	}
	
	private static void initButtonsLayout() {
		_mapButtons.setLayout(new GridLayout());
		addLoadMapButton();
	}
	
	private static void formatButton(JButton btn) {
		btn.setFont(new Font("Calibri", Font.BOLD, 12));
		btn.setFocusPainted(false);
	}
	
	private static void addLoadMapButton() {
		if (!realExecution){
		    // Load Map Button
		    JButton LoadMap_btn = new JButton("Load Map");
		    formatButton(LoadMap_btn);
		    LoadMap_btn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
			    JDialog loadMapDialog = new JDialog(_mapFrame, "Load Map", true);
			    loadMapDialog.setSize(200, 30);
			    loadMapDialog.setLayout(new FlowLayout());

			    final JTextField loadText = new JTextField(13);
			    JButton loadMapButton = new JButton("Load");

			    loadMapButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
				    loadMapDialog.setVisible(false);
				    Map.loadMapFromDisk(realMap, loadText.getText());
				    CardLayout cl = ((CardLayout) _mapTiles.getLayout());
				    cl.show(_mapTiles, "REAL_MAP");
				    realMap.repaint();
				}
			    });

			    loadMapDialog.add(new JLabel("File Name: "));
			    loadMapDialog.add(loadText);
			    loadMapDialog.add(loadMapButton);
			    loadMapDialog.setVisible(true);
			}
		    });
		    _mapButtons.add(LoadMap_btn);
	}
        // FastestPath Class
		
		  class FastestPathAlgo extends SwingWorker<Integer, String> {
	            protected Integer doInBackground() throws Exception {
	                //robot.setRobotPos(Constants.START_X, Constants.START_Y);
	            	System.out.println("fastest path executed");
	                exploredMap.repaint();
                        //CommunicationMgr.getCommMgr().sendMsg("S\n", null);
	                String msg1[] = new String[2];
	                System.out.println("Waiting for FP_START...");
	                if (realExecution) {
	                    while (true) {
	                        String msg = CommunicationMgr.getCommMgr().recvMsg();
	                        msg1 = msg.split(";");
	                        if (msg1[0].equals(CommunicationMgr.FP_START)) break;	//can change accordingly 
	                    }
	                }
	                
	                //get waypoint here
	                if(realExecution){
	                	String msgRcv[] = msg1[1].split(",");  
	 	               waypointX = Integer.parseInt(msgRcv[0]);
	 	               waypointY = Integer.parseInt(msgRcv[1]);
	 	               System.out.println("waypoint x,y: " + waypointX + ", " + waypointY);
	 	               
	                }
	               

	                FastestPath fastestPath;
	                fastestPath = new FastestPath(exploredMap, robot, realMap);               
	                fastestPath.runFastestPath(waypointY,waypointX);
	                System.out.println("robot x simulator: " + robot.getRobotPosX());
	                
	                FastestPath wayptFP = new FastestPath(exploredMap, robot, realMap);
	                System.out.println("robot y simulator: " + robot.getRobotPosY() );
	                wayptFP.runFastestPath(Constants.GOAL_Y,Constants.GOAL_X); 	
	                
	                
                        // HC
	                /*
                        String finalPath = "";
                        int frontCount = 0;
                        String[] codes = new String[] {"0", "Z", "X", "V"};
                        for(MOVEMENT x: Constants.combinedFP) {
                            if(x == MOVEMENT.L) {
                                if(frontCount >= 10)
                                        finalPath += codes[frontCount-10];
                                else if(frontCount != 0)
                                        finalPath += frontCount + "";

                                frontCount = 0;
                                finalPath += "A";
                            }
                            else if(x == MOVEMENT.R) {
                                if(frontCount >= 10) 
                                        finalPath += codes[frontCount-10];
                                else if(frontCount != 0)
                                        finalPath += frontCount + "";

                                frontCount = 0;
                                finalPath += "D";
                            }
                            else if(x == MOVEMENT.F)
                                    frontCount += 1;
                        }

                        // Handle left overs
                        if(frontCount >= 10) 
                                finalPath += codes[frontCount-10];
                        else if(frontCount != 0)
                                finalPath += frontCount + "";

                        frontCount = 0;
	               
	                CommunicationMgr.getCommMgr().testPrint2(finalPath);
					*/
	                return 222;
	            }
	        }
		// Fastest Path Button
	        JButton btn_FastestPath = new JButton("Fastest Path");
	        formatButton(btn_FastestPath);
	        btn_FastestPath.addMouseListener(new MouseAdapter() {
	            public void mousePressed(MouseEvent e) {
	                CardLayout cl = ((CardLayout) _mapTiles.getLayout());
	                cl.show(_mapTiles, "EXPLORATION");
	                new FastestPathAlgo().execute();
	            }
	        });
	        _mapButtons.add(btn_FastestPath);
	
            
        class Explore extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                int x, y;

                x = robot.robotPos_X;
                y = robot.robotPos_Y;
                
                //robot.setRobotPos(x, y);
                exploredMap.repaint();

                Exploration exploration = new Exploration(exploredMap, realMap, robot, coverageLimit, timeLimit, 0, robotDelay );
                
                if (realExecution) {
                	// send waypoint before               	
                	 while(true) {
                         System.out.println("Waiting for Android Explore");
                         String msg = comm.recvMsg();
                         if(msg.equals("E")) break;   	//change rcv msg if needed 
                     }
                	// String descriptor = String.join(";", Map.generateMapDescriptor(exploredMap));
                    // comm.sendMap(robot.getRobotPosX() + "," + robot.getRobotPosY(), descriptor, robot.getRobotDir(),null);
                
                }
                 
                exploration.startExploration();
                
                
                	
                
                if (realExecution) {
                    Map.generateMapDescriptor(exploredMap);
                    
                    System.out.println("here lol");
                    new FastestPathAlgo().execute();
                }
                 System.out.println("exploration exit");

                return 111; //<-- need to change accordingly 
            }
        }
		
        // Exploration Button
        JButton Exploration_btn = new JButton("Exploration");
        formatButton(Exploration_btn);
        
        Exploration_btn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) _mapTiles.getLayout());
                cl.show(_mapTiles, "EXPLORATION");
                
                new Explore().execute();
                
            }
        });
        _mapButtons.add(Exploration_btn);
		
        
        //coverage limited exploration class
        class ExploreCoverageLimited extends SwingWorker<Integer, String>{
        	protected Integer doInBackground() throws Exception{
        		robot.setRobotPos(Constants.START_X, Constants.START_Y);
        		exploredMap.repaint();
        		
        		Exploration explorationCL = new Exploration(exploredMap, realMap, robot, coverageLimit, timeLimit, 1, robotDelay);
        		explorationCL.startExploration();
        		
        		return 333; //<- need to change accordingly
        	}
        }
        
        //coverage limited exploration button
        JButton explorationCL_button = new JButton("Coverage Limited");
        formatButton(explorationCL_button);
        explorationCL_button.addMouseListener(new MouseAdapter(){
        	public void mousePressed(MouseEvent e) {
                JDialog explorationCL_dialog = new JDialog(_mapFrame, "Coverage-Limited Exploration", true);
                explorationCL_dialog.setSize(400, 60);
                explorationCL_dialog.setLayout(new FlowLayout());
                final JTextField coverage_text = new JTextField(5);
                JButton run_button = new JButton("Run");
        	
                run_button.addMouseListener(new MouseAdapter(){
                	public void mousePressed(MouseEvent e){
                		explorationCL_dialog.setVisible(false);
                		coverageLimit = (int) ((Integer.parseInt(coverage_text.getText())) * Constants.MAP_SIZE / 100.0); 
                		new ExploreCoverageLimited().execute();
                		CardLayout cl = ((CardLayout) _mapTiles.getLayout());
                		cl.show(_mapTiles , "EXPLORATION");
                	}
                	
                });
                explorationCL_dialog.add(new JLabel("Coverage Limit (in %): "));
                explorationCL_dialog.add(coverage_text);
                explorationCL_dialog.add(run_button);
                explorationCL_dialog.setVisible(true);
        	}
        	
        });
        _mapButtons.add(explorationCL_button);
        
        
        //time limited exploration class
        class ExplorationTimeLimited extends SwingWorker<Integer, String>{
        	protected Integer doInBackground() throws Exception{
        		robot.setRobotPos(Constants.START_X, Constants.START_Y);
        		exploredMap.repaint();
        		
        		Exploration explorationTL = new Exploration(exploredMap, realMap, robot, coverageLimit, timeLimit, 2, robotDelay);
        		explorationTL.startExploration();
        		
        		return 444; //<- need to change accordingly
        	}

        }
        
        //time limited exploration button
        JButton explorationTL_button = new JButton("Time Limited");
        formatButton(explorationTL_button);
        explorationTL_button.addMouseListener(new MouseAdapter(){
        	public void mousePressed(MouseEvent e) {
                JDialog explorationTL_dialog = new JDialog(_mapFrame, "Coverage-Limited Exploration", true);
                explorationTL_dialog.setSize(400, 60);
                explorationTL_dialog.setLayout(new FlowLayout());
                final JTextField time_text = new JTextField(5); //time in MM:SS format
                JButton run_button = new JButton("Run");
        	
                run_button.addMouseListener(new MouseAdapter(){
                	public void mousePressed(MouseEvent e){
                		explorationTL_dialog.setVisible(false);
                		String time = time_text.getText();
                		String[] timeSplit = time.split(":");
                		timeLimit = (Integer.parseInt(timeSplit[0]) * 60) + Integer.parseInt(timeSplit[1]);
                		CardLayout cl = ((CardLayout) _mapTiles.getLayout());
                		cl.show(_mapTiles , "EXPLORATION");
                		new ExplorationTimeLimited().execute();
                	}
                });
                explorationTL_dialog.add(new JLabel("Time Limit (in mm:ss):"));
                explorationTL_dialog.add(time_text);
                explorationTL_dialog.add(run_button);
                explorationTL_dialog.setVisible(true);
               }
        	
        
        });
        
        _mapButtons.add(explorationTL_button);
        
        
        //to set speed
        class SetSpeed extends SwingWorker<Integer, String>{
        	protected Integer doInBackground() throws Exception{
        		robot.setRobotPos(Constants.START_X, Constants.START_Y);
        		exploredMap.repaint();
        		
        		return 555; //<- need to change accordingly
        	}

        }
        JButton setSpeed_button = new JButton("Set Speed");
        formatButton(setSpeed_button);
        setSpeed_button.addMouseListener(new MouseAdapter(){
        	public void mousePressed(MouseEvent e) {
                JDialog setSpeed_dialog = new JDialog(_mapFrame, "Set Robot Speed", true);
                setSpeed_dialog.setSize(400, 60);
                setSpeed_dialog.setLayout(new FlowLayout());
                final JTextField setSpeed_text = new JTextField(5);
                JButton set_button = new JButton("Set");
        	
                set_button.addMouseListener(new MouseAdapter(){
                	public void mousePressed(MouseEvent e){
                		setSpeed_dialog.setVisible(false);
                		robotDelay =  1000 / (int)(Integer.parseInt(setSpeed_text.getText())); 
                		System.out.println(robotDelay);
                		new SetSpeed().execute();
                		CardLayout cl = ((CardLayout) _mapTiles.getLayout());
                		cl.show(_mapTiles , "SET SPEED");
                	}
                	
                });
                setSpeed_dialog.add(new JLabel("Robot Speed (in steps/second): "));
                setSpeed_dialog.add(setSpeed_text);
                setSpeed_dialog.add(set_button);
                setSpeed_dialog.setVisible(true);
        	}
        	
        });
        _mapButtons.add(setSpeed_button);
        
        //get waypoint
        /*
        class Waypoint extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {

                 if (realExecution) {
                	 
                		 System.out.println("Waiting for Waypoint");
                         String msg = CommunicationMgr.getCommMgr().recvMsg();
                         System.out.println(msg);
                      
                         String[] msgArr = msg.split(",");	 //waypoint arriving x,y
                         waypointX = Integer.parseInt(msgArr[0]);
                         waypointY = Integer.parseInt(msgArr[1]);
                         System.out.println("waypointX: " + waypointX);
                         System.out.println("waypointY: " + waypointY); 
                        	
                         
                         		
            	
                	
                }


                return 666; //<-- need to change accordingly 
            }
        }
		
        // Waypoint Button
        JButton waypoint_btn = new JButton("Get Waypoint");
        formatButton(waypoint_btn);
        
        Exploration_btn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) _mapTiles.getLayout());
                cl.show(_mapTiles, "Get Waypoint");
                
                new Waypoint().execute();
                
            }
        });
        _mapButtons.add(waypoint_btn);
        */
        //FOR TESTING ONLY******************************************************************************
        /*
        //BUTTON IN FRONT 
        class test1 extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {

            	CommunicationMgr.getCommMgr().sendMsg(MOVEMENT.F.toString(), CommunicationMgr.BOT_INSTR);
                return 000; //<-- need to change accordingly 
            }
        }
		
        JButton test1_btn = new JButton("F");
        formatButton(waypoint_btn);
        
        Exploration_btn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) _mapTiles.getLayout());
                cl.show(_mapTiles, "F");
                
                new test1().execute();
                
            }
        });
        _mapButtons.add(test1_btn);
        
      //BUTTON RIGHT 
        class test2 extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {

            	//CommunicationMgr.getCommMgr().sendMsg(MOVEMENT.R.toString(), CommunicationMgr.BOT_INSTR);
                return 000; //<-- need to change accordingly 
            }
        }
		
        JButton test2_btn = new JButton("R");
        formatButton(waypoint_btn);
        
        Exploration_btn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) _mapTiles.getLayout());
                cl.show(_mapTiles, "R");
                
                new test2().execute();
                
            }
        });
        _mapButtons.add(test2_btn);
        
      //BUTTON LEFT 
        class test3 extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {

            	//CommunicationMgr.getCommMgr().sendMsg(MOVEMENT.L.toString(), CommunicationMgr.BOT_INSTR);
                return 000; //<-- need to change accordingly 
            }
        }
		
        JButton test3_btn = new JButton("L");
        formatButton(waypoint_btn);
        
        Exploration_btn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) _mapTiles.getLayout());
                cl.show(_mapTiles, "L");
                
                new test3().execute();
                
            }
        });
        _mapButtons.add(test3_btn);
        */
        
	}
}