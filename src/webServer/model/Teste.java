package webServer.model;

import RMI.RMIInterface;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Teste extends UnicastRemoteObject implements TesteIF, Serializable {
    private static RMIInterface rmi;
    private static final long serialVersionUID = 1L;

    public Teste() throws RemoteException {
        super();
    };

    public void simples() throws RemoteException{
        System.out.println("hello on teste");
    }

    public static void main(String[] args) throws RemoteException {
        Teste t= new Teste();
        System.out.print(t.toString());
        boolean a=false;
        while(!a){
            try{
                rmi = (RMIInterface) LocateRegistry.getRegistry("localhost",7001).lookup("server");
                a=true;
            }catch(Exception e){
                System.out.println("waiting for RMI...");
            }
        }
        try {
            rmi.subscreveTeste((TesteIF) t);
        }catch(Exception e){e.printStackTrace();}
    }




}
