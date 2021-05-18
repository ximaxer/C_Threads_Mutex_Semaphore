package webServer.model;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Calendar;

import RMI.ElectionAlreadyExistsException;
import RMI.InvalidUsername;
import RMI.RMIInterface;
import RMI.UsernameAlreadyExistsException;
import ws.WebSocketAnnotation;

public class PrimesBean {
	private RMIInterface rmi;
	private ArrayList<String> activeElections;
	private String uname; // username and password supplied by the user
	private String pass;
	private String election, electionToVote;
	private WebSocketAnnotation ws;

	public PrimesBean() {
		ws = new WebSocketAnnotation();
		try {
			rmi = (RMIInterface) LocateRegistry.getRegistry("localhost",7001).lookup("server");
		}
		catch(NotBoundException/* | MalformedURLException*/ | RemoteException e) {
			e.printStackTrace(); // what happens *after* we reach this line?
		}
	}

	public ArrayList<String> getAllUsers() throws RemoteException {
		return rmi.getAllUsers(); // are you going to throw all exceptions?
	}

	public String registerNewMan(String nome, String pass, String instituicao, int telefone, String morada, int CC, Calendar valCC) throws UsernameAlreadyExistsException, RemoteException {
		rmi.RegisterPerson(nome, pass, instituicao, telefone, morada, CC, valCC, "aluno");
		return "success";
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
		String result = rmi.addVote(username, electionName, lista);
		if(result.equals("Obrigado por votar."))ws.receiveMessage("["+electionName+"] "+username +" voted on" + lista);
		return result;
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
	public String getElectionToVote(){return this.electionToVote;}

}
