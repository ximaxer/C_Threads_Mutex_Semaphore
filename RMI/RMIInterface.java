package RMI;

import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Calendar;

public interface RMIInterface extends Remote{

    void RegisterPerson(String username, String password, String instituicao, int telefone, String morada, int CC, Calendar valCC,String type) throws RemoteException, UsernameAlreadyExistsException;
    String CreateElection(Calendar dataI, Calendar dataF, String titulo, String descricao, String instituicao) throws RemoteException, ElectionAlreadyExistsException;
    String addTableToElection(String table, String electionName) throws RemoteException;
    String addListaToElection(String lista, String electionName) throws RemoteException;
    String removeTableFromElection(String table, String election) throws RemoteException;
    void adicionarMesaDeVoto(String departamento, String ip, String port) throws RemoteException;
    String evalCredentials(String username, String password) throws RemoteException, Exception;
    String ShowActiveElections() throws RemoteException;
    long backupServer() throws RemoteException, UnknownHostException;
    String formatListsMap(String electionName) throws RemoteException;
    String addVote(String username, String electionName, String listaPretendida) throws InvalidUsername, RemoteException;
    boolean checkElectionExists(String electionName) throws RemoteException;
    String showWhereVoted(String electionName) throws RemoteException;
    String ShowUnstartedElections() throws RemoteException;
    String ShowFinishedElections() throws RemoteException;
    String ShowElections() throws RemoteException;
    String editElection1(String change, String property,String electionName) throws RemoteException;
    String editElection2(Calendar change, String property,String electionName) throws RemoteException;
    String atribuiAdressoMesa(String departamento) throws RemoteException;
    String atribuiPortaMesa(String departamento) throws RemoteException;
    String showMesas() throws RemoteException;
    String showResultsPastElection(String electionName)throws RemoteException;
}