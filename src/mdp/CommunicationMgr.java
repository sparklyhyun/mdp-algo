package mdp;

import java.io.*;
import java.net.*;

import mdp.Constants.DIRECTION;
import mdp.Constants.MOVEMENT;

public class CommunicationMgr {

    public static final String EX_START = "EX_START";       // Android --> PC
    public static final String FP_START = "FP_START";       // Android --> PC
    public static final String MAP_STRINGS = "MAP";         // PC --> Android
    public static final String BOT_POS = "BOT_POS";         // PC --> Android
    public static final String BOT_START = "BOT_START";     // PC --> Arduino
    public static final String BOT_MOVEMENT = "BOT_MOV";      // PC --> Arduino
    public static final String BOT_DIRECTION = "BOT_DIR";
    public static final String BOT_INSTR = "INSTR";
    public static final String SENSOR_DATA = "SDATA";       // Arduino --> PC

    private static CommunicationMgr commMgr = null;
    private static Socket connection = null;

    private BufferedWriter writer;
    private BufferedReader reader;

    private CommunicationMgr() {
    }

    public static CommunicationMgr getCommMgr() {
        if (commMgr == null) {
            commMgr = new CommunicationMgr();
        }
        return commMgr;
    }

    public void openConnection() {
        System.out.println("Opening connection...");

        try {
            String host = "192.168.9.254";
            int port = 1338;
            connection = new Socket(host, port);

            writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(connection.getOutputStream())));
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            System.out.println("openConnection() --> " + "Connection established successfully!");

            return;
        } catch (UnknownHostException e) {
            System.out.println("openConnection() --> UnknownHostException");
        } catch (IOException e) {
            System.out.println("openConnection() --> IOException");
        } catch (Exception e) {
            System.out.println("openConnection() --> Exception");
            System.out.println(e.toString());
        }

        System.out.println("Failed to establish connection!");
    }

    public void closeConnection() {
        System.out.println("Closing connection...");

        try {
            reader.close();

            if (connection != null) {
            	connection.close();
            	connection = null;
            }
            System.out.println("Connection closed!");
        } catch (IOException e) {
            System.out.println("closeConnection() --> IOException");
        } catch (NullPointerException e) {
            System.out.println("closeConnection() --> NullPointerException");
        } catch (Exception e) {
            System.out.println("closeConnection() --> Exception");
            System.out.println(e.toString());
        }
    }

    public void sendMap(String position, String map, DIRECTION a, String movement) {
        try {
            writer.write(movement + ";POS_" + position +"," + a + ";" + map );
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
    }
    
    //for fastest path
    public void fastestSendMap(String position, String map, DIRECTION a, String movement, int count){
    	try {
            writer.write(movement + ";POS_" + position +"," + a + ";" + map );
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void sendMsg(String msg, String msgType) {
        System.out.println("Sending a message...");
        
        try {
            String outputMsg = "";
            if (msg == null) {
                //outputMsg = msgType + "\n";
            } else if (msgType.equals(MAP_STRINGS) || msgType.equals(BOT_POS)) {
                //outputMsg = msgType + " " + msg + "\n";
            } else {
                //outputMsg = msgType + "\n" + msg + "\n";
            	outputMsg = msg + "\n";
            }

            System.out.println("Sending out message:\n" + outputMsg);
            writer.write(outputMsg);
            writer.flush();
        } catch (IOException e) {
            System.out.println("sendMsg() --> IOException");
        } catch (Exception e) {
            System.out.println("sendMsg() --> Exception");
            System.out.println(e.toString());
        }
    }

    public String recvMsg() {
        System.out.println("Receiving a message...");
        
        try {
            StringBuilder sb = new StringBuilder();
            String input = reader.readLine();

            if (input != null && input.length() > 0) {
                sb.append(input);
                System.out.println(sb.toString());
                return sb.toString();
            }
        } catch (IOException e) {
            System.out.println("recvMsg() --> IOException");
        } catch (Exception e) {
            System.out.println("recvMsg() --> Exception");
            System.out.println(e.toString());
        }

        return null;
    }

    public boolean isConnected() {
        return connection.isConnected();
    }
}