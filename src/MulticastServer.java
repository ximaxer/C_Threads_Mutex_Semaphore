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

    public static int BUFFER_SIZE = 256;
    private String serverID;
    public static RMIInterface rmi;
    public static boolean NeedNewTerm=false;
    public static HashMap<String, String> listaTerminais= new HashMap<String, String>();
    public static boolean waitingForTerminal=true;
    public static String newTerm="";
    public static String dep="";
    public static String address="";
    public static String porta="";

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException{
        Scanner keyboardScanner = null;
        keyboardScanner = new Scanner(System.in);
        //rmi = (RMIInterface) Naming.lookup("server");
        rmi = (RMIInterface) LocateRegistry.getRegistry("localhost",7001).lookup("server");
        do{
            System.out.print("Departamento da mesa:");
            dep = keyboardScanner.nextLine();
            try {
                address = rmi.atribuiAdressoMesa(dep);
                porta = rmi.atribuiPortaMesa(dep);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }while(address.equals("") || address.equals("Esta mesa nao existe!\n"));
        System.out.println("SERVER Initializing...");
        System.out.println("Multicast Server running...");
        MulticastServer2 inputter = new MulticastServer2();
        inputter.start();
        gestaoTerminais gestor = new gestaoTerminais();
        gestor.start();
        MulticastServer server = new MulticastServer();
        server.start();
        atribuicaoDeTerminais atribuidor = new atribuicaoDeTerminais();
        atribuidor.start();
    }

    public MulticastServer(){
        super("Server "+ (long) (Math.random()*10000));
        this.serverID = this.getName().split("\\s+")[1];
    }

    public void run(){
        boolean listen=true;
        MulticastSocket socket = null;
        try{
            InetAddress group = InetAddress.getByName(address  /*CONST.MULTICAST.MULTICAST_ADDRESS*/);
            while (true){
                    newTerm="Nao ha terminais disponiveis.";
                    Thread.sleep(50);
                    socket = new MulticastSocket(Integer.parseInt(porta));
                    socket.joinGroup(group);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    System.out.println("waiting.");
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    message=message.substring(1);
                    System.out.println(packet.getAddress()+"|"+message);
                    if(message.equals("give my my ip")){
                        listaTerminais.put(packet.getAddress().toString().substring(1), "blocked");
                    }
                if(!waitingForTerminal && !message.equals("")){ //DEBUG
                    String response = this.handle(message);
                    if (response != null) {
                        System.out.println("SENDING RESPONSE: "+ response + "to "+packet.getAddress()); //DEBUG
                        buffer = response.getBytes();
                        packet = new DatagramPacket(buffer, buffer.length, group, Integer.parseInt(porta));
                        socket = new MulticastSocket();
                        socket.send(packet);
                    }
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
    /**
    * Handles the String recieved from a Voting Terminal
    */
	String handle(String message) throws RemoteException, Exception {
        System.out.println(message);
		HashMap<String, String> messageMap = parseMessage(message);
		if (messageMap == null)	return null;
		try { return createResponse(messageMap); }
		catch (InvalidRequestType e){ return null; }
	} 
    /**
    * Creates a Hashmap based on the String recieved
    */
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
    /**
    * Creates a response to send to the Voting Terminal
    */
	private String createResponse(HashMap<String, String> messageMap) throws InvalidRequestType, RemoteException, Exception{
        //System.out.println("#####"+ messageMap.get("username")+"######"+messageMap.get("password"));
        //String result ="Success!";
		switch (messageMap.get("type")){    
			case "login":
            String result= rmi.evalCredentials(messageMap.get("username"),messageMap.get("password"));
                if(result.equals("Success!"))
				return "type|status;logged|on;msg|Welcome to eVoting"+ShowListas(rmi.ShowActiveElections());
                else
                return "type|status;logged|out;msg|"+result+"\n";
            case "ElectionChoice":
                if(rmi.checkElectionExists(messageMap.get("election"))){
                    return "type|"+messageMap.get("election")+";"+rmi.formatListsMap(messageMap.get("election"))+"\n";
                }else{
                    return "Eleicao inexistente.\n";
                }
            case "Vote": 
                return rmi.webVoteCallBack(messageMap.get("username"),messageMap.get("election"),messageMap.get("voto"))+"\n";
			case "block":
                lockTerminal(messageMap.get("terminal"));
				return "type|block;terminal|" + messageMap.get("terminal")+"\n";
			default:
				throw new InvalidRequestType("Unexpected value: " + messageMap.get("type"));
		}
	}
    /**
    * Locks the specified terminal
    */
    public void lockTerminal(String terminal){
        for (String key : listaTerminais.keySet()) {
            if(key.toString().equals(terminal)){
                listaTerminais.put(key,"blocked");
                MulticastServer.NeedNewTerm=false;
            }
        }
    }
    /**
    * Formats the output from the RMIServer to print the election's candidate lists
    */
    public String ShowListas(String lista){
        String finalList="";
        if (lista.equals("Nao existem eleicoes ativas.") || lista.equals("Nao existem listas nesta eleicao.") ){
            return lista+"\n";
        }
        for (String fragmento_lista : lista.split(";")) {
            finalList=finalList+"\n"+fragmento_lista;
        }
        return finalList+"\n";
    }
}

class MulticastServer2 extends Thread{
    public static String CCs="";
    public MulticastServer2() {
        super("Server " + (long) (1));
    }
    public void run() {
        Scanner input = null;
        input = new Scanner(System.in);
        while(true){
            if(MulticastServer.listaTerminais!=null){
                System.out.printf("Identifique-se: ");
                String nrCC = input.nextLine();
                if(!nrCC.equals("") && verifyCC(nrCC,CCs)){
                    CCs+=nrCC+";";
                    if (CCs.length()!=0){
                        if(CCs.valueOf(CCs.length()-1).equals(";"))CCs=CCs.substring(0, CCs.length()-1);;
                    }
                    MulticastServer.NeedNewTerm=true;
                }
                nrCC="";
            }
        }
    }
    
    /**
    * Verifies if the CC owner has already voted
    */
    public boolean verifyCC(String CC, String CCs){
        for (String nrCC : CCs.split(";")) {
            if(nrCC.equals(CC))return false;
        }
        return true;
    }
}

class gestaoTerminais extends Thread{
    public static String CCs="";
    public gestaoTerminais() {
        super("Server " + (long) (1));
    }
    public void run() {
        MulticastSocket socket = null;
        try{
            System.out.println(MulticastServer.address);
            InetAddress group = InetAddress.getByName(MulticastServer.address);//CONST.MULTICAST.MULTICAST_ADDRESS
            while (true){
                if(isEveryTermBlocked(MulticastServer.listaTerminais))MulticastServer.waitingForTerminal=true;
                socket = new MulticastSocket(Integer.parseInt(MulticastServer.porta));
                socket.joinGroup(group);
                byte[] buffer = new byte[MulticastServer.BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                String message = new String(packet.getData(), 0, packet.getLength());
                if(MulticastServer.NeedNewTerm){
                    MulticastServer.newTerm = unlockTerminals(MulticastServer.listaTerminais);
                    if(!MulticastServer.newTerm.equals("Nao ha terminais disponiveis.")){
                        String response = "unblocked";
                        System.out.println("Terminal with IP:"+MulticastServer.newTerm+" "+response);
                        response=MulticastServer.newTerm+"|"+response;
                        buffer = response.getBytes();
                        packet = new DatagramPacket(buffer, buffer.length, group, Integer.parseInt(MulticastServer.porta));
                        socket = new MulticastSocket();
                        socket.send(packet);
                        MulticastServer.NeedNewTerm=false;
                        MulticastServer.waitingForTerminal=false;
                    }
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
    /**
    * Verifies if every terminal is blocked or not
    */
    private boolean isEveryTermBlocked(HashMap<String, String> terminais){
        for (String key : terminais.keySet()) {
            if(terminais.get(key).equals("unblocked")){
                return false;
            }
        }  
        return true;
    }
    /**
    * Unlocks a terminal if possible
    */
    private String unlockTerminals(HashMap<String, String> terminais){
        for (String key : terminais.keySet()) {
            if(terminais.get(key).equals("blocked")){
                terminais.put(key,"unblocked");
                MulticastServer.NeedNewTerm=false;
                return key;         //ip do terminal desbloqueado
            }
        }
        return "Nao ha terminais disponiveis.";
    }

}
class atribuicaoDeTerminais extends Thread{
    public static String CCs="";
    public atribuicaoDeTerminais() {
        super("Server " + (long) (1));
    }
    public void run() {
        MulticastSocket socket = null;
        try{
            InetAddress group = InetAddress.getByName(CONST.MULTICAST.MULTICAST_ADDRESS);
            while (true){
                String message="";
                socket = new MulticastSocket(CONST.MULTICAST.PORT);
                socket.joinGroup(group);
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                message = new String(packet.getData(), 0, packet.getLength());
                if(message.equals(MulticastServer.dep)){
                    String response=MulticastServer.address+";"+MulticastServer.porta;
                    System.out.println(response);
                    buffer = response.getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, group, CONST.MULTICAST.PORT);
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
}