package RMI;

import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;

public class RMIServer extends UnicastRemoteObject implements RMIInterface {
    
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected RMIServer() throws RemoteException {
        super();
    }

    public void RegisterPerson(String username, String password, String instituicao, int telefone, String morada, int CC, Date valCC, boolean voted,String type,Data data) throws UsernameAlreadyExistsException{
        
        if(checkPerson(username, data)) {
            data.add(new User(username, password, instituicao, telefone, morada, CC, valCC, voted, type));
            data.updateRecords();
        }
        else
            throw new UsernameAlreadyExistsException("Username already exists!");
    }

    private boolean checkPerson(String username,Data data){
        return !(checkAdminExists(username, data.getAdmins()) | checkUserExists(username, data.getUsers()));
    }

    private boolean checkAdminExists(String username,ArrayList<Admin> admins){
        for(Admin admin: admins){
            if(admin.getUsername().compareTo(username) == 0)
                return true;
        }
        return false;
    }

    private boolean checkUserExists(String username,ArrayList<User> users){
        for(User user: users){
            if(user.getUsername().compareTo(username) == 0)
                return true;
        }
        return false;
    }
    
    public void CreateElection(Date dataI, Date dataF, String titulo, String descricao, String instituicao, Data data) throws ElectionAlreadyExistsException{
        if(!checkElections(titulo, data.getElections())) {
            data.add(new Election(dataI, dataF, titulo, descricao, instituicao));
            data.updateRecords();
        }
        else
            throw new ElectionAlreadyExistsException("Election already exists!");
    }

    private boolean checkElections(String titulo,ArrayList<Election> elections){
        for(Election election: elections){
            if(election.getTitulo().compareTo(titulo) == 0)
                return true;
        }
        return false;
    }

    private boolean checkElectionIsOngoing(Election election){
        Date currentDate = new Date(); 
        if(election.getDataI().before(currentDate) && election.getDataF().after(currentDate))return true;
        return false;
    }

    private boolean checkIfCanVote(User user,Election election){
        if(user.getInstituicao().compareTo(election.getInstituicao()) == 0 && checkElectionIsOngoing(election)){
            return true;
        }
        return false;
    } 

    private void addUserToElection(User user, Election election){
        election.listaCandidatos.add(user);
    }
    
}