import RMI.CONST;
import RMI.InvalidRequestType;
import RMI.RMIInterface;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.io.IOException;
import java.net.*;
import java.util.*;

import javax.lang.model.util.ElementScanner6;

public class MulticastServer extends Thread{

    private static int BUFFER_SIZE = 256;
    private String serverID;
    //private static RMIInterface rmi;

    public static void main(String[] args) throws RemoteException, NotBoundException{
        
        //rmi = (RMIInterface) LocateRegistry.getRegistry(7420).lookup("server");
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
                System.out.println(packet.getAddress()+"|"+message); //DEBUG

                String response = this.handle(message);
                if (response != null) {
                    System.out.println("SENDING RESPONSE: "+ response + "to "+packet.getAddress()); //DEBUG
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

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	String handle(String message) throws RemoteException, Exception {
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



	private String createResponse(HashMap<String, String> messageMap) throws InvalidRequestType, RemoteException, Exception{
        //String result= rmi.evalCredentials(messageMap.get("username"),messageMap.get("password"));
        String result ="Success!";
		switch (messageMap.get("type")){    
			case "login":
                if(result.equals("Success!"))
				return "type|status;logged|on;msg|Welcome to eVoting";
                else
                return "type|status;logged|out;msg|"+result;
			case "exit":
				return "username|" + messageMap.get("id")+";type|status;" + "exited";
			default:
				throw new InvalidRequestType("Unexpected value: " + messageMap.get("type"));
		}
	}
}
