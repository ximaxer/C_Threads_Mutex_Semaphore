import logic.Multicast.IgnorePacket;
import logic.Multicast.UnidentifiedRequestType;
import misc.Admin;
import misc.excep.InvalidRequestType;
import misc.excep.InvalidUsername;
import misc.excep.UsernameAlreadyInUseException;
import misc.Login;
import misc.User;

import java.net.*;
import java.io.*;
import java.util.*;
public class UDPServer {
	public static void main(String args[]) {
		DatagramSocket aSocket = null;
		String s;
		try {
			aSocket = new DatagramSocket(6789);
			System.out.println("Socket Datagram � escuta no porto 6789");
			while (true) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				s = new String(request.getData(), 0, request.getLength());
				System.out.println("Server Recebeu: " + s);

				String[] segment = s.split("\\|");
				String reply = HandleUser(segment[1]);
				DatagramPacket reply = new DatagramPacket(request.getData(),
						request.getLength(), request.getAddress(), request.getPort());
				aSocket.send(reply);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null) aSocket.close();
		}
	}
	String handle(String message) {
		HashMap<String, String> messageMap = parseMessage(message);
		if (messageMap == null)
			return null;
		try{
			this.actionResults = actionRequested(messageMap);
		} catch (IgnorePacket e){
			return null;
		} catch (Exception e){
			e.printStackTrace();
		}
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
		this.currentPacketID = null;
		switch (messageMap.get("type")){
			case "login":
				return "username|"+messageMap.get("id")+";password|"+messageMap.get("password")+";type|status;logged|" + this.actionResults.get("logged")
						+";msg| Welcome to eVoting"+(this.actionResults.get("error") == null ? "":(";error|"+this.actionResults.get("error")));
			case "exit":
				return "username|" + messageMap.get("id")+";type|status;" + "exited|" + (this.actionResults.get("exited"));
			default:
				throw new InvalidRequestType("Unexpected value: " + messageMap.get("type"));
		}
	}
	private HashMap<String, String> actionRequested(HashMap<String, String> messageMap) throws IgnorePacket, UnidentifiedRequestType, InvalidUsername {
		// TODO CATCH EXCEPTIONS AND SEND THEM IN TAG "ERROR": EXAMPLE WRONG PASSWORD -> "...;logged-false;error-Wrong password for the user"
		HashMap<String, String> infoMap = new HashMap<>();
		User user;
		switch (messageMap.get("type")) {
			case "login":
				if (!checkPacket(messageMap))
					throw new IgnorePacket("Packet not for this server!");
				try {
					Login l = new Login(messageMap.get("username"), messageMap.get("password"), this.sessionData);
					infoMap.put("logged", "true");
					if (l.getUser() instanceof Admin)
						infoMap.put("usertype", "admin");
					else if (l.getUser().isMembro())
						infoMap.put("usertype", "membro");
					else
						infoMap.put("usertype", "user");
					l.getUser().setLoggedIn(true);
					return infoMap;
				} catch (Exception e) {
					infoMap.put("logged", "false");
					infoMap.put("error", e.getMessage());
					infoMap.put("usertype", "null");
					System.err.println("[DEBUG]");//DEBUG
					e.printStackTrace(System.err); //DEBUG
					return infoMap;
				}
			case "register":
				if (!checkPacket(messageMap))
					throw new IgnorePacket("Packet not for this server!");
				try {
					new Register(messageMap.get("username"), messageMap.get("password"), this.sessionData);
					infoMap.put("registered", "true");
					return infoMap;
				} catch (UsernameAlreadyInUseException e) {
					infoMap.put("registered", "false");
					System.err.println("[DEBUG]");//DEBUG
					e.printStackTrace(System.err); //DEBUG
					return infoMap;
				}
			default:
				throw new UnidentifiedRequestType("Type of request not recognized: "+messageMap.get("type")+ "!");
		}
	}
}