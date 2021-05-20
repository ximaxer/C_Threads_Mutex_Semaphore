package webServer.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TesteIF extends Remote {
    void simples() throws RemoteException;
}
