package RMI;

import java.rmi.*;
import java.rmi.server.*;
import java.io.IOException;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;


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

    public void RegisterPerson(String username, String password, String instituicao, int telefone, String morada, int CC, Date valCC,String type) throws RemoteException, UsernameAlreadyExistsException{
        
        if(checkPerson(username, data)) {
            data.add(new User(username, password, instituicao, telefone, morada, CC, valCC, false, type));
            data.updateRecords();
        }
        else
            throw new UsernameAlreadyExistsException("Username already exists!");
    }

    public boolean checkPerson(String username,Data data){
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
    
    public void CreateElection(Date dataI, Date dataF, String titulo, String descricao, String instituicao, Data data) throws ElectionAlreadyExistsException{
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
        Date currentDate = new Date(); 
        if(election.getDataI().before(currentDate) && election.getDataF().after(currentDate))return true;
        return false;
    }

    public boolean checkIfCanVote(User user,Election election){
        if(user.getInstituicao().compareTo(election.getInstituicao()) == 0 && checkElectionIsOngoing(election)){
            return true;
        }
        return false;
    } 

    public void addUserToElection(User user, Election election){
        election.getListaCandidatos().add(user);
    }

    public void addTableToElection(Table table, Election election) throws RemoteException{
        election.getListaMesas().add(table);
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

    public static void main(String[] args) throws AccessException, RemoteException, NotBoundException{
        System.getProperties().put("java.security.policy", "policy.all");
        boolean exitFlag = false;
        long primaryRMIPid = 0;
        while(!exitFlag) {
            try {
                RMIServer rmi = new RMIServer();
                Registry r = LocateRegistry.createRegistry(7420);
                r.rebind("server", rmi);
                System.out.println("RMI Server ready.");
                // Makes the connected Server the principal server
                exitFlag = true;
            } catch (RemoteException re) {
                System.err.println("Backup Server Running...");
                exitFlag = false;
                RMIInterface rmi = (RMIInterface) LocateRegistry.getRegistry(7420).lookup("server");
                boolean errorDetected = false;
                // Backup server sends 5 messages to the principal server each 0.5s to check if there is a response
                while (!errorDetected){
                    for (int i=0; i<5; i++){
                        try {
                            primaryRMIPid = rmi.backupServer();
                            sleep(500);
                        } catch (Exception e) {
                            System.err.println("Primary Server Stopped. Escalating Secondary to Primary...");
                            errorDetected = true;
                            break;
                        }
                    }
                }
                // STONITH if there is no response
                String cmd = "taskkill /F /PID " + primaryRMIPid;
                try {
                    Runtime.getRuntime().exec(cmd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
}

}