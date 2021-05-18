package RMI;

import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;

public interface RMIInterface extends Remote{

    void RegisterPerson(String username, String password, String instituicao, int telefone, String morada, int CC, Calendar valCC,String type) throws RemoteException, UsernameAlreadyExistsException;
    String CreateElection(Calendar dataI, Calendar dataF, String titulo, String descricao, String instituicao) throws RemoteException, ElectionAlreadyExistsException;
    String addTableToElection(String table, String electionName) throws RemoteException;
    String addListaToElection(String lista, String electionName) throws RemoteException;
    String removeTableFromElection(String table, String election) throws RemoteException;
    void adicionarMesaDeVoto(String departamento, String ip, String port) throws RemoteException;
    String evalCredentials(String username, String password) throws Exception;
    String ShowActiveElections() throws RemoteException;
    long backupServer() throws RemoteException, UnknownHostException;
    String formatListsMap(String electionName) throws RemoteException;
    String addVote(String username, String electionName, String listaPretendida) throws InvalidUsername, RemoteException;
    boolean checkElectionExists(String electionName) throws RemoteException;
    String showWhereVoted(String electionName) throws RemoteException;
    String ShowUnstartedElections() throws RemoteException;
    ArrayList<String> ShowUnstartedElectionsList() throws RemoteException;
    boolean verifyUnstartedElection(String name) throws RemoteException;
    String ShowFinishedElections() throws RemoteException;
    String ShowElections() throws RemoteException;
    String editElection1(String change, String property,String electionName) throws RemoteException;
    String editElection2(Calendar change, String property,String electionName) throws RemoteException;
    String atribuiAdressoMesa(String departamento) throws RemoteException;
    String atribuiPortaMesa(String departamento) throws RemoteException;
    String showMesas() throws RemoteException;
    String showResultsPastElection(String electionName)throws RemoteException;
    ArrayList<String> getAllUsers() throws RemoteException;
    ArrayList<String> listaDeMesas() throws RemoteException;
    ArrayList<String> showEleicoesEMesas() throws RemoteException;
    ArrayList<String> showEleicoesEListas() throws RemoteException;
    ArrayList<String> showAllVotes() throws RemoteException;
    ArrayList<String> showElectionProperties(String electionName) throws RemoteException;
    ArrayList<String> showActiveElectionList() throws RemoteException;
    String updateElection(String eleicao, String descricao, String instituicao, Calendar dataI, Calendar dataF) throws RemoteException;
    ArrayList<String> showResultsPastElectionsList() throws RemoteException;
    boolean verifyIfVotedOnElection(String electionName,int CC) throws RemoteException;
    ArrayList<String> electionListsList(String electionName) throws RemoteException;
}