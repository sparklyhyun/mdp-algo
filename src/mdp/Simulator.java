package mdp;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;


public class Simulator {
	private static JFrame _mapFrame = null;          // JFrame to display Map

	private static JPanel _mapTiles = null;           // JPanel for map Tiles
	private static JPanel _mapButtons = null;        // JPanel for map buttons

	private static Robot robot;

	private static Map realMap;              // real map
	private static Map exploredMap;          // exploration map

	private static int timeLimit = 3600;            // time limit
	private static int coverageLimit = 300;         // coverage limit
	
	//private static final CommMgr comm = CommMgr.getCommMgr();

	private static final boolean realExecution = false; //for now, not real map


	public static void main(String[] args) throws IOException {
		//if (realExecution) comm.openConnection();   //connection function to be added!!!

		robot = new Robot(Constants.START_X, Constants.START_Y, realExecution); 

		if (!realExecution) {
		    realMap = new Map(robot); 
		}

		exploredMap = new Map(robot);
		viewFullMap();
	}	
	
	
	private static void viewFullMap() {
		_mapFrame = new JFrame();
		_mapFrame.setTitle("Group 9 MDP Simulator");
		_mapFrame.setSize(new Dimension(690, 700));
		_mapFrame.setResizable(true);
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
				    //loadMapFromDisk(realMap, loadText.getText());
				    //exploredMap.readMapDesc();
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
		/*
        class FastestPath extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                robot.setRobotPos(Constants.START_X, Constants.START_Y);
                exploredMap.repaint();

                if (realExecution) {
                    while (true) {
                        System.out.println("Waiting for FP_START...");
                       // String msg = comm.recvMsg();
                       // if (msg.equals(CommMgr.FP_START)) break;
                   // }
                }

                //FastestPath fastestPath = new FastestPath(exploredMap, robot);

                //fastestPath.runFastestPath(Constants.GOAL_X, Constants.GOAL_Y);

                return 222;
            }
        }
        */	
		
        // Fastest Path Button
        /*
        JButton FastestPath_btn = new JButton("Fastest Path");
        formatButton(FastestPath_btn);
        FastestPath_btn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) _mapTiles.getLayout());
                cl.show(_mapTiles, "EXPLORATION");
                new FastestPath().execute();
            }
        });
        _buttons.add(FastestPath_btn);	
		*/
		
        // Exploration Class
            
        class Exploration extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                int x, y;

                x = Constants.START_X;
                y = Constants.START_Y;

                robot.setRobotPos(x, y);
                exploredMap.repaint();

                Exploration exploration;
                exploration = new Exploration(exploredMap, realMap, robot, coverageLimit, timeLimit);
                	
                /*
                if (realRun) {
                    //CommMgr.getCommMgr().sendMsg(null, CommMgr.ROBOT_START);
                }
				*/
                
                exploration.startExploration();
               //generateMapDescriptor(exploredMap);

                if (realExecution) {
                    //new FastestPath().execute();
                }

                return 111;
            }
        }
		
        // Exploration Button
        JButton Exploration_btn = new JButton("Exploration");
        formatButton(Exploration_btn);
        Exploration_btn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) _mapTiles.getLayout());
                cl.show(_mapTiles, "EXPLORATION");
                new Exploration().execute();
            }
        });
        _mapButtons.add(Exploration_btn);
		

}
        }

