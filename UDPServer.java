import java.net.*;
import java.io.*;
public class UDPServer{
	public static void main(String args[]){ 
		DatagramSocket aSocket = null;
		String s;
		try{
			aSocket = new DatagramSocket(6789);
			System.out.println("Socket Datagram � escuta no porto 6789");
			while(true){
				byte[] buffer = new byte[1000]; 			
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				s=new String(request.getData(), 0, request.getLength());	
				System.out.println("Server Recebeu: " + s);	

				DatagramPacket reply = new DatagramPacket(request.getData(), 
						request.getLength(), request.getAddress(), request.getPort());
				aSocket.send(reply);
			}
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e) {System.out.println("IO: " + e.getMessage());
		}finally {if(aSocket != null) aSocket.close();}
	}

	String formatString(String message) {
		HashMap<String, String> messageMap = parseMessage(message);
		if (messageMap == null)
			return null;
		try{  //verificação se o use está registado
			this.actionResults = BDVerification(messageMap);
		} catch (IgnorePacket e){
			return null;
		} catch (Exception e){
			e.printStackTrace();
		}
		try { return createResponse(messageMap); }
		catch (InvalidRequestType e){ return null; }
	}

	private HashMap<String,String> formatMessage(String message){
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



	private String createResponse(HashMap<String, String> messagesMap) throws InvalidRequestType{
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
}