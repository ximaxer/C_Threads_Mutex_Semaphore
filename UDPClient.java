import java.net.*;
import java.io.*;
public class UDPClient{
	public static void main(String args[]){ 
		// argumentos da linha de comando: hostname 
		if(args.length == 0){
			System.out.println("java UDPClient hostname");
			System.exit(0);
		}
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();    

		String texto = "";
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		

				System.out.print("1- Lista A\n2- Lista B\n3- Branco\n4- Exit\n");
				// READ STRING FROM KEYBOARD
	     	  try{
					texto = reader.readLine();

				}
			  catch(Exception e){}
			  
				
				byte [] m = texto.getBytes();
				
				InetAddress aHost = InetAddress.getByName(args[0]);
				int serverPort = 6789;		                                                
				DatagramPacket request = new DatagramPacket(m,m.length,aHost,serverPort);
				aSocket.send(request);			                        
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);	
				aSocket.receive(reply);
				System.out.println("Obrigado por votar\n");

		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());
		}finally {if(aSocket != null) aSocket.close();}
	} 
}