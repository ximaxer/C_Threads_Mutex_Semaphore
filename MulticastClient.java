import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import RMI.CONST;
import java.util.Timer;
import java.util.TimerTask;


// LISTENER
public class MulticastClient extends Thread {
    public static boolean LogResult=false;
    public static boolean hasIP=false;
    public static boolean hasChosenElection = false;
    public static boolean hasVoted = false;
    public static String electionName="";


    public String getElectionName(){
        return this.electionName;
    }

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
		                HashMap<String, String> messageMap = parseMessage(message);
                        updateState(messageMap); 

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

    private void updateState(HashMap<String, String> messageMap){
        String a= this.getElectionName();
        switch (messageMap.get("type")){    
			case "status":
                if(messageMap.get("logged").equals("on")){
                    LogResult=true;
                }else if(messageMap.get("logged").equals("off")){
                    System.out.println("Erro no login tente novamente");
                }
            break;
            case "Eleicao inexistente.\n":
                hasChosenElection=false;
            break;
        }
        if(this.getElectionName().equals(messageMap.get("type"))){
            hasChosenElection=true;
        }
    }
}

// SENDER
class MulticastUser extends Thread {
    String username="", password="",electionName="";
    public MulticastUser() {
        super("User " + (long) (1));
    }

    public void run() {
        new Acaba(60,0);        //diz que sai
        MulticastSocket socket = null;
        Scanner keyboardScanner = null;
        String message = "";
        keyboardScanner = new Scanner(System.in);
        System.out.print(this.getName() + " ready, ");
        try {
            while (true) {
                socket = new MulticastSocket(CONST.MULTICAST.PORT);  // create socket without binding it (only for sending)
                message = "";
                if(MulticastClient.hasIP){   
                    if(!MulticastClient.LogResult){
                        try{
                            Thread.sleep(250);
                        }catch (InterruptedException  e) {
                            e.printStackTrace();
                        }
                        message = RequestLogin();
                    }else if(MulticastClient.LogResult && !MulticastClient.hasChosenElection){
                        this.electionName = keyboardScanner.nextLine();
                        message = ParseElectionChoice();
                        MulticastClient.electionName = this.electionName;
                    }else if(MulticastClient.LogResult && MulticastClient.hasChosenElection && !MulticastClient.hasVoted){
                        String vote = keyboardScanner.nextLine();
                        message = ParseVoteChoice(vote);
                        MulticastClient.hasVoted=true;
                    }
                    if(message!=""){
                        byte[] buffer = message.getBytes();
                        InetAddress group = InetAddress.getByName(CONST.MULTICAST.MULTICAST_ADDRESS);
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, CONST.MULTICAST.PORT);
                        socket.send(packet);
                        message = "";
                    }
                }else{
                        message = "give my my ip";
                        byte[] buffer = message.getBytes();
                        InetAddress group = InetAddress.getByName(CONST.MULTICAST.MULTICAST_ADDRESS);
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, CONST.MULTICAST.PORT);
                        socket.send(packet);
                        message = "";
                        MulticastClient.hasIP=true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    public String RequestLogin(){
        Scanner input = null;
        input = new Scanner(System.in);
        System.out.printf("\nUsername:");
        this.username = input.nextLine();
        System.out.printf("Password:");
        this.password = input.nextLine();
        
        return createLoginMessage();
    }
    public String createLoginMessage(){
        return "type|login;username|"+this.username+";password|"+this.password;
    }
    
    public String ParseElectionChoice(){
        return "type|ElectionChoice;username|"+this.username+";password|"+this.password+";election|"+this.electionName;
    }

    public String ParseVoteChoice(String lista){
        return "type|Vote;username|"+this.username+";password|"+this.password+";election|"+this.electionName+";voto|"+lista;
    }
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