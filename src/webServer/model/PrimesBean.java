package webServer.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

import RMI.ElectionAlreadyExistsException;
import RMI.InvalidUsername;
import RMI.RMIInterface;
import RMI.UsernameAlreadyExistsException;
import org.apache.struts2.components.Bean;
import ws.WebSocketAnnotation;
import java.util.concurrent.CopyOnWriteArrayList;

public class PrimesBean extends UnicastRemoteObject implements BeanInterface, Serializable {
	private static RMIInterface rmi;
	private static final long serialVersionUID = 1L;
	private String uname; // username and password supplied by the user
	private String pass;
	private String election, electionToVote;
	private static WebSocketAnnotation ws;

	public PrimesBean() throws RemoteException {
		super();
		System.out.print("HELLO\n");
		System.out.println(this);
		boolean a=false;
		while(!a) {
			try {
				rmi = (RMIInterface) LocateRegistry.getRegistry("localhost", 7001).lookup("server");
				System.out.print(this.toString());
				rmi.registaBean(this);
				a=true;
			} catch (NotBoundException/* | MalformedURLException*/ | RemoteException e) {
				e.printStackTrace(); // what happens *after* we reach this line?
			}
		}
	}

	public void subscreve(BeanInterface b) throws RemoteException {
		rmi.registaBean(b);
	}

	public void test() throws RemoteException{
		System.out.println("hello");
	}
	public ArrayList<String> getAllUsers() throws RemoteException {
		return rmi.getAllUsers(); // are you going to throw all exceptions?
	}

	public String registerNewMan(String nome, String pass, String instituicao, int telefone, String morada, int CC, Calendar valCC) throws UsernameAlreadyExistsException, RemoteException {
		try{
			rmi.RegisterPerson(nome, pass, instituicao, telefone, morada, CC, valCC, "aluno");
			return "success";
		}
		catch(Exception e){
			return "erro";
		}
	}

	public String createNewTable(String departamento, String ip, String port) throws RemoteException {
		rmi.adicionarMesaDeVoto(departamento, ip, port);
		return "success";
	}
	public String AssociateTable(String departament, String election) throws RemoteException {
		String result = rmi.addTableToElection(departament,election);
		if(result.equals("Mesa ja se encontra associada a esta eleicao!") || result.equals("Eleicao nao existe!") || result.equals("Este departamento nao tem mesa!"))return "erro";
		return "success";
	}
	public String RemoveTable(String departament, String election) throws RemoteException {
		String result = rmi.removeTableFromElection(departament,election);
		if(result.equals("Mesa ja nao se encontra associada a esta eleicao!") || result.equals("Este departamento nao tem mesas!") || result.equals("Eleicao nao existe!"))return "erro";
		return "success";
	}
	public String CreateElection(String titulo, String descricao, String instituicao, Calendar dataI, Calendar dataF) throws ElectionAlreadyExistsException, RemoteException {
		String result=rmi.CreateElection(dataI,dataF,titulo,descricao,instituicao);
		if(result.equals("Eleicao ja existe!"))return "erro";
		return "success";
	}
	public String AddListToElection(String list, String election) throws RemoteException {
		String result = rmi.addListaToElection(list,election);
		if(result.equals("Lista ja se encontra associada a esta eleicao!") || result.equals("Eleicao nao existe!"))return "erro";
		return "success";
	}
	public String updateElection(String eleicao, String descricao, String instituicao, Calendar dataI, Calendar dataF) throws RemoteException {
		String result=rmi.updateElection(eleicao,descricao,instituicao,dataI,dataF);
		if(result.equals("success"))return "success";
		return "erro";
	}
	public String castVote(String username, String electionName, String lista) throws InvalidUsername, RemoteException {
		ws = new WebSocketAnnotation();
		String result = rmi.addVote(username,electionName,lista);
		System.out.println(username +" "+ electionName+" "+lista);
		System.out.println(result);
		if(result.equals("Obrigado por votar.")){
			ws.receiveMessage((username+" has voted on "+ electionName));
			ws.receiveMessage("user "+username+" has logged out.");
			return "Obrigado por votar.";
		}
		return "Ja votou nesta eleicao!";
	}
	public String castVoteRMI(String result, String username, String electionName) throws InvalidUsername, RemoteException {
		System.out.println(username + " votou em " + electionName + " " + result);
		ws = new WebSocketAnnotation();
		System.out.println(ws.toString());
		if(result.equals("Obrigado por votar.")){
			ws.receiveMessage((username+" has voted on "+ electionName));
			return "Obrigado por votar.";
		}
		return "Ja votou nesta eleicao!";
	}
	public void notificationFromRMI(String notif) throws RemoteException{
		System.out.println(notif);
		ws = new WebSocketAnnotation();
		System.out.println(ws.toString());
		ws.receiveMessage((notif));
	}

	public String credentialResult() throws Exception {
		return rmi.evalCredentials(this.uname, this.pass);
	}
	public ArrayList<String> getElectionLists() throws RemoteException {
		return rmi.electionListsList(this.electionToVote);
	}
	public boolean validateUnstartedElectionName(String name) throws RemoteException{
		return rmi.verifyUnstartedElection(name); //verificar se a eleicao é das passadas
	}
	public boolean validateOngoing(String name) throws RemoteException{
		return rmi.checkElectionExists(name);
	}
	public ArrayList<String> getAllTables() throws RemoteException {
		return rmi.listaDeMesas();
	}
	public ArrayList<String> getActiveElections() throws RemoteException{
		return rmi.showActiveElectionList();
	}
	public ArrayList<String> getUnstartedElections() throws RemoteException{
		return rmi.ShowUnstartedElectionsList();
	}
	public ArrayList<String> getActiveElectionsAndCorrespondingTables() throws RemoteException {
		return rmi.showEleicoesEMesas();
	}
	public ArrayList<String> getAllVotes() throws RemoteException {
		return rmi.showAllVotes();
	}
	public ArrayList<String> getActiveElectionsAndCorrespondingLists() throws RemoteException {
		return rmi.showEleicoesEListas();
	}
	public ArrayList<String> getPastElectionResultsList() throws RemoteException {
		return rmi.showResultsPastElectionsList();
	}
	public ArrayList<String> getElectionProperties() throws RemoteException {
		ArrayList<String> resultado=rmi.showElectionProperties(this.election);
		return resultado;
	}
	public void setElection(String election){this.election = election;}
	public void setElectionToVote(String electionToVote){this.electionToVote = electionToVote;}
	public void setUname(String username){this.uname = username;}
	public void setPass(String password){this.pass = password;}
	public String getUname(){return this.uname;}
	public String getPass(){return this.pass;}
	public String getElection(){return this.election;}
	public WebSocketAnnotation getWs(){return this.ws;}
	public String getElectionToVote(){return this.electionToVote;}

}
