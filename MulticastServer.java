import RMI.CONST;
import RMI.InvalidRequestType;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class MulticastServer extends Thread{

    private static int BUFFER_SIZE = 256;
    private String serverID;

    public static void main(String[] args){
        System.out.println("SERVER Initializing...");
        MulticastServer server = new MulticastServer();
        server.start();
    }

    public MulticastServer(){
        super("Server "+ (long) (Math.random()*10000));
        this.serverID = this.getName().split("\\s+")[1];
    }

    public void run(){
        MulticastSocket socket = null;
        System.out.println("Multicast Server running...");
        try{
            InetAddress group = InetAddress.getByName(CONST.MULTICAST.MULTICAST_ADDRESS);
            while (true){
                socket = new MulticastSocket(CONST.MULTICAST.PORT);
                socket.joinGroup(group);
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                System.out.println("waiting.");
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(packet.getAddress()+" : "+message); //DEBUG

                String response = this.handle(message);
                if (response != null) {
                    System.out.println("SENDING RESPONSE: "+response + "to "+packet.getAddress()); //DEBUG
                    buffer = response.getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, group, CONST.MULTICAST.PORT);
                    socket = new MulticastSocket();
                    socket.send(packet);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
            assert socket != null;
            socket.close();

        }
    }

	String handle(String message) {
		HashMap<String, String> messageMap = parseMessage(message);
		if (messageMap == null)	return null;
		try { return createResponse(messageMap); }
		catch (InvalidRequestType e){ return null; }
	}

	private HashMap<String,String> parseMessage(String message){
		try {
			HashMap<String, String> messageMap = new HashMap<>();
			for (String segment : message.split(";")) {
				String[] keyValue = segment.split("\\|");
				messageMap.put(keyValue[0], keyValue[1]);
			}
			return messageMap;
		}catch (ArrayIndexOutOfBoundsException e){
			return null;
		}
	}



	private String createResponse(HashMap<String, String> messageMap) throws InvalidRequestType{
		switch (messageMap.get("type")){
			case "login":
				return "username|"+messageMap.get("username")+";password|"+messageMap.get("password")+";type|status;logged|" +";msg| Welcome to eVoting";
			case "exit":
				return "username|" + messageMap.get("id")+";type|status;" + "exited";
			default:
				throw new InvalidRequestType("Unexpected value: " + messageMap.get("type"));
		}
	}
}