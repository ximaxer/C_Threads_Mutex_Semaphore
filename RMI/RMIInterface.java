package RMI;

import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface RMIInterface extends Remote{

    void RegisterPerson(String username, String password, String instituicao, int telefone, String morada, int CC, Date valCC,String type) throws RemoteException, UsernameAlreadyExistsException;
    void CreateElection(Date dataI, Date dataF, String titulo, String descricao, String instituicao, Data data) throws ElectionAlreadyExistsException;
    void addTableToElection(Table table, Election election) throws RemoteException;
    void removeTableFromElection(Table table, Election election) throws RemoteException;
    String evalCredentials(String username, String password) throws RemoteException, Exception;
    long backupServer() throws RemoteException, UnknownHostException;
}