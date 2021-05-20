package webServer.model;

import RMI.ElectionAlreadyExistsException;
import RMI.InvalidUsername;
import RMI.RMIInterface;
import RMI.UsernameAlreadyExistsException;
import ws.WebSocketAnnotation;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Calendar;

public interface BeanInterface extends Remote {
    String castVoteRMI(String username, String electionName, String lista) throws InvalidUsername, RemoteException;
    void test() throws RemoteException;
    void notificationFromRMI(String notif) throws RemoteException;
}
