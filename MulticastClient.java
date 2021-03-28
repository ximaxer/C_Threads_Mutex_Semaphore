import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Scanner;
import RMI.CONST;
/**
 * The MulticastClient class joins a multicast group and loops receiving
 * messages from that group. The client also runs a MulticastUser thread that
 * loops reading a string from the keyboard and multicasting it to the group.
 * <p>
 * The example IPv4 address chosen may require you to use a VM option to
 * prefer IPv4 (if your operating system uses IPv6 sockets by default).
 * <p>
 * Usage: java -Djava.net.preferIPv4Stack=true MulticastClient
 *
 * @author Raul Barbosa
 * @version 1.0
 */

// LISTENER
public class MulticastClient extends Thread {
    public static boolean LogResult=false;
    public static void main(String[] args) {
        MulticastClient client = new MulticastClient();
        client.start();
        MulticastUser user = new MulticastUser();
        user.start();
    }

    public void run() {
        String myID="";
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(CONST.MULTICAST.PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(CONST.MULTICAST.MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                byte[] buffer = new byte[3*1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                if(!myID.equals("")){       //terminal ja recebeu o ip
                    String message = new String(packet.getData(), 0, packet.getLength());
                    if(myID.equals(packet.getAddress().getHostAddress())){
                        System.out.println(packet.getAddress().getHostAddress() +"|"+message);
                    }
                    


                }else{                      //terminal ainda nao recebeu o ip
                    System.out.println("Terminal IP:    "+packet.getAddress().getHostAddress());
                    myID = packet.getAddress().getHostAddress();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}

// SENDER
class MulticastUser extends Thread {
    boolean hasIP=false;
    boolean LoggedIn = false;
    public MulticastUser() {
        super("User " + (long) (1));
    }

    public void run() {
        LoggedIn = MulticastClient.LogResult;
        MulticastSocket socket = null;
        Scanner keyboardScanner = null;
        String message = "";
        keyboardScanner = new Scanner(System.in);
        System.out.print(this.getName() + " ready, ");
        try {
            while (true) {
                socket = new MulticastSocket();  // create socket without binding it (only for sending)
                if(hasIP){   
                    if(!LoggedIn){
                        message = RequestLogin();
                        LoggedIn=true;
                    }else{
                        message = keyboardScanner.nextLine();
                    }
                    byte[] buffer = message.getBytes();

                    InetAddress group = InetAddress.getByName(CONST.MULTICAST.MULTICAST_ADDRESS);
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, CONST.MULTICAST.PORT);
                    socket.send(packet);
                }else{
                        message = "give my my ip";
                        byte[] buffer = message.getBytes();
                        InetAddress group = InetAddress.getByName(CONST.MULTICAST.MULTICAST_ADDRESS);
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, CONST.MULTICAST.PORT);
                        socket.send(packet);
                        hasIP=true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            keyboardScanner.close();
            socket.close();
        }
    }

    public String RequestLogin(){
        String username="", password="";
        Scanner input = null;
        input = new Scanner(System.in);
        System.out.printf("\nUsername:");
        username = input.nextLine();
        System.out.printf("\nPassword:");
        password = input.nextLine();
        
        input.close();
        return createLoginMessage(username, password);
    }
    public String createLoginMessage(String username, String password){
        return "type|login;username|"+username+";password|"+password;
    }    










        //          type|login;username|abc;password|123
}
