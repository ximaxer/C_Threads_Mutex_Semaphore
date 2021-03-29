package RMI;

import java.rmi.*;
import java.rmi.server.*;
import java.io.IOException;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Calendar;


import static java.lang.Thread.sleep;

public class RMIServer extends UnicastRemoteObject implements RMIInterface {
    Data data;
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected RMIServer() throws RemoteException {
        super();
        this.data = new Data();
        try{
            data.loadData();
        }catch (Exception e){}
    }

    public void adicionarMesaDeVoto(String departamento) throws RemoteException{
        if(!checkTableExists(departamento)){
            data.add(new Table(departamento));
        }
    }

    public boolean checkTableExists(String departamento){
        for(Table table: data.getTables()){
            if(table.getDepartamento().compareTo(departamento) == 0)
                return true;
        }
        return false;
    }

    public void RegisterPerson(String username, String password, String instituicao, int telefone, String morada, int CC, Calendar valCC,String type) throws RemoteException, UsernameAlreadyExistsException{
        
        if(checkPerson(username)) {
            data.add(new User(username, password, instituicao, telefone, morada, CC, valCC, false, type));
            data.updateRecords();
        }
        else
            throw new UsernameAlreadyExistsException("Username already exists!");
    }

    public boolean checkPerson(String username){
        return !(checkAdminExists(username, data.getAdmins()) | checkUserExists(username, data.getUsers()));
    }

    public boolean checkAdminExists(String username,ArrayList<Admin> admins){
        for(Admin admin: admins){
            if(admin.getUsername().compareTo(username) == 0)
                return true;
        }
        return false;
    }

    public boolean checkUserExists(String username,ArrayList<User> users){
        for(User user: users){
            if(user.getUsername().compareTo(username) == 0)
                return true;
        }
        return false;
    }
    
    public String ShowActiveElections() throws RemoteException{
        String listaEleicoes="";
        for(Election election: data.getElections()){
            if(checkElectionIsOngoing(election)) listaEleicoes+=election.getTitulo()+";";
        }
        return listaEleicoes;
    }

    public boolean VerifyIfActiveElection(String nome) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitulo().compareTo(nome)==0){
                if(checkElectionIsOngoing(election)) return true;
            }
        }
        return false;
    }
    
    
    public void CreateElection(Calendar dataI, Calendar dataF, String titulo, String descricao, String instituicao) throws ElectionAlreadyExistsException{
        if(!checkElections(titulo, data.getElections())) {
            data.add(new Election(dataI, dataF, titulo, descricao, instituicao));
            data.updateRecords();
        }
        else
            throw new ElectionAlreadyExistsException("Election already exists!");
    }

    public boolean checkElections(String titulo,ArrayList<Election> elections){
        for(Election election: elections){
            if(election.getTitulo().compareTo(titulo) == 0)
                return true;
        }
        return false;
    }

    public boolean checkElectionIsOngoing(Election election){
        Calendar currentDate = Calendar.getInstance();
        if(election.getDataI().before(currentDate.getTime()) && election.getDataF().after(currentDate.getTime()))return true;
        return false;
    }

    public boolean checkIfCanVote(User user,Election election){
        if(user.getInstituicao().compareTo(election.getInstituicao()) == 0 && checkElectionIsOngoing(election)){
            return true;
        }
        return false;
    } 

    public String addListaToElection(String listaNome, String electionName) throws RemoteException{
        if(!VerifyIfActiveElection(electionName))return "Eleicao nao existe!";
        for(Listas lista: data.getElection(electionName).getListas()){
            if(lista.getName().compareTo(listaNome) == 0)
                return "Lista ja se encontra associada a esta eleicao!";
        }
        data.getElection(electionName).getListas().add(new Listas(listaNome));
        return "Lista associada com sucesso!";
    }


    public void addUserToElection(User user, Election election){
        election.getListaCandidatosQueVotaram().add(user);
    }

    public String addTableToElection(String table, String electionName) throws RemoteException{
        if(!VerifyIfActiveElection(electionName))return "Eleicao nao existe!";
        if(!checkTableExists(table))return "Este departamento nao tem mesa!";
        for(Table mesa: data.getElection(electionName).getListaMesas()){
            if(mesa.getDepartamento().compareTo(table) == 0)
                return "Mesa ja se encontra associada a esta eleicao!";
        }
        data.getElection(electionName).getListaMesas().add(data.getTable(table));
        return "Mesa associada com sucesso!";
    }
    
    public String removeTableFromElection(String table, String electionName) throws RemoteException{
        if(!VerifyIfActiveElection(electionName))return "Eleicao nao existe!";
        if(!checkTableExists(table))return "Este departamento nao tem mesas!";
        for(Table mesa: data.getElection(electionName).getListaMesas()){
            if(mesa.getDepartamento().compareTo(table) == 0){
                data.getElection(electionName).getListaMesas().remove(data.getTable(table));
                return "Mesa desassociada com sucesso!";
            }
        }
        return "Mesa ja nao se encontra associada a esta eleicao!";
        
    }
    
    public void removeTableFromElection(Table table, Election election) throws RemoteException{
        election.getListaMesas().remove(table);
    }

    public String evalCredentials(String username, String password) throws RemoteException,Exception {
        // check if it is a user
        String validation="";
        validation=checkAdmins(username,password);
        if (validation.equals("Incorrect Password!")){
            return "Incorrect Password!";
        }else if(validation != ""){
            data.updateRecords();
            return "Success!";
        }else{
            validation = checkUsers(username,password);
            if (validation.equals("")){
                return "User not found!";
            }else if(validation.equals("Incorrect Password!")){
                return "Incorrect Password!";
            }else{
                data.updateRecords();
                return "Success!";
            }
        }
    }

    private String checkAdmins(String username,String password) throws WrongPassword,Exception{
        for (Admin admin: data.getAdmins()){
            if (admin.getUsername().compareTo(username) == 0){
                if (!admin.checkPassword(password))
                    return "Incorrect Password!";
                else{
                    admin.setLoggedIn(true);
                    return username;
                }
            }
        }
        return "";
    }

    private String checkUsers(String username,String password) throws WrongPassword,Exception{
        for (User user: data.getUsers()){
            if (user.getUsername().compareTo(username) == 0){
                if (!user.checkPassword(password))
                    return "Incorrect Password!";
                else{
                    user.setLoggedIn(true);
                    return username;
                }
            }
        }
        return "";
    }


    public long backupServer(){
        return ProcessHandle.current().pid();
    }

    public static void main(String[] args) throws AccessException, RemoteException, NotBoundException, MalformedURLException{
        //System.getProperties().put("java.security.policy", "policy.all");
        try {
            RMIServer rmi = new RMIServer();
            Naming.rebind("server", rmi);
            System.out.println("RMI Server ready.");
        } catch (RemoteException re) {
            System.out.println("Exception in RMIServer.main: " + re);
        }
        catch (MalformedURLException e) {
            System.out.println("MalformedURLException in RMIServer.main: " + e);
        }
           
           
    }

}