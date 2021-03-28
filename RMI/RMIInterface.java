package RMI;

import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Calendar;

public interface RMIInterface extends Remote{

    void RegisterPerson(String username, String password, String instituicao, int telefone, String morada, int CC, Calendar valCC,String type) throws RemoteException, UsernameAlreadyExistsException;
    void CreateElection(Calendar dataI, Calendar dataF, String titulo, String descricao, String instituicao) throws ElectionAlreadyExistsException;
    void addTableToElection(Table table, Election election) throws RemoteException;
    void removeTableFromElection(Table table, Election election) throws RemoteException;
    String evalCredentials(String username, String password) throws RemoteException, Exception;
    long backupServer() throws RemoteException, UnknownHostException;
}