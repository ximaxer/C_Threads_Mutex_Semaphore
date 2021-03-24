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

    public void RegisterPerson(String username, String password, String instituicao, int telefone, String morada, int CC, Date valCC, boolean voted,String type,Data data) throws UsernameAlreadyInUseException{
        
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

    /*public void CreateElection(String username, String password, String instituicao, int telefone, String morada, int CC, Date valCC, boolean voted,String type,Data data) throws UsernameAlreadyInUseException{
        
        if(checkPerson(username, data)) {
            data.add(new User(username, password, instituicao, telefone, morada, CC, valCC, voted, type));
            data.updateRecords();
        }
        else
            throw new UsernameAlreadyExistsException("Username already exists!");
    }*/
}