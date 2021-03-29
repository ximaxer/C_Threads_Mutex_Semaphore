import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import RMI.CONST;
import java.util.Timer;
import java.util.TimerTask;


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
        new Acaba(60,1);        //nao diz que sai
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
                        System.out.println(packet.getAddress().getHostAddress() +"| "+message);
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
    private boolean hasIP=false;
    private boolean LoggedIn = false;
    public MulticastUser() {
        super("User " + (long) (1));
    }

    public void run() {
        new Acaba(60,0);        //diz que sai
        LoggedIn = MulticastClient.LogResult;
        MulticastSocket socket = null;
        Scanner keyboardScanner = null;
        String message = "";
        keyboardScanner = new Scanner(System.in);
        System.out.print(this.getName() + " ready, ");
        try {
            while (true) {
                socket = new MulticastSocket();  // create socket without binding it (only for sending)
                message = "";
                if(this.hasIP){   
                    if(!this.LoggedIn){
                        try{
                            Thread.sleep(250);
                        }catch (InterruptedException  e) {
                            e.printStackTrace();
                        }
                        message = RequestLogin();
                        this.LoggedIn=true;
                    }else{
                        try{
                            message = keyboardScanner.nextLine();
                        }catch (NoSuchElementException e){}
                    }
                    byte[] buffer = message.getBytes();
                    if(message!=""){
                        InetAddress group = InetAddress.getByName(CONST.MULTICAST.MULTICAST_ADDRESS);
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, CONST.MULTICAST.PORT);
                        socket.send(packet);
                    }
                }else{
                        message = "give my my ip";
                        byte[] buffer = message.getBytes();
                        InetAddress group = InetAddress.getByName(CONST.MULTICAST.MULTICAST_ADDRESS);
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, CONST.MULTICAST.PORT);
                        socket.send(packet);
                        this.hasIP=true;
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
        System.out.printf("Password:");
        password = input.nextLine();
        
        input.close();
        return createLoginMessage(username, password);
    }
    public String createLoginMessage(String username, String password){
        return "type|login;username|"+username+";password|"+password;
    }    

        //          type|login;username|abc;password|123
}

class Acaba {

    Timer timer;

    public Acaba(int seconds,int trigger) {
        timer = new Timer();
        timer.schedule(new RemindTask(trigger), seconds * 1000);
    }

    class RemindTask extends TimerTask {
        int tipo;
        public RemindTask(int trigger){
            this.tipo = trigger;
        }
        public void run() {
            if(tipo == 0)System.out.printf("\nProgram terminating!");
            System.exit(0);
            timer.cancel(); //Terminate the timer thread
        }
    }
}