package RMI;

import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Calendar;

public interface RMIInterface extends Remote{

    void RegisterPerson(String username, String password, String instituicao, int telefone, String morada, int CC, Calendar valCC,String type) throws RemoteException, UsernameAlreadyExistsException;
    void CreateElection(Calendar dataI, Calendar dataF, String titulo, String descricao, String instituicao) throws RemoteException, ElectionAlreadyExistsException;
    String addTableToElection(String table, String electionName) throws RemoteException;
    String addListaToElection(String lista, String electionName) throws RemoteException;
    String removeTableFromElection(String table, String election) throws RemoteException;
    void adicionarMesaDeVoto(String departamento) throws RemoteException;
    String evalCredentials(String username, String password) throws RemoteException, Exception;
    void ShowActiveElections() throws RemoteException;
    long backupServer() throws RemoteException, UnknownHostException;
}