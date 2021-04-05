package RMI;

import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Calendar;


public class RMIServer extends UnicastRemoteObject implements RMIInterface {
    Data data;
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected RMIServer() throws RemoteException {
        super();
        this.data = new Data();
        //data.createFiles();
        try{
            data.loadData();
        }catch (Exception e){}
    }

    public void adicionarMesaDeVoto(String departamento,String ip,String port) throws RemoteException{
        if(!checkTableExists(departamento)){
            data.add(new Table(departamento,ip,port));
            data.updateRecords();
        }
    }

    public boolean checkTableExists(String departamento){
        for(Table table: data.getTables()){
            if(table.getDepartamento().compareTo(departamento) == 0)
                return true;
        }
        return false;
    }

    public String atribuiAdressoMesa(String departamento) throws RemoteException{
        for(Table mesa : data.getTables()){
            System.out.println(mesa.getDepartamento());
            if(mesa.getDepartamento().equals(departamento))return mesa.getIP();
        }
        return "Esta mesa nao existe!\n";
    }

    public String atribuiPortaMesa(String departamento) throws RemoteException{
        for(Table mesa : data.getTables()){
            if(mesa.getDepartamento().equals(departamento))return mesa.getPort();
        }
        return "Esta mesa nao existe!\n";
    }

    public String showMesas() throws RemoteException{
        String ret="";
        for(Table mesa : data.getTables()){
            ret+=mesa.getDepartamento()+"\n";
        }
        return ret;
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
        return !(checkAdminExists(username) | checkUserExists(username));
    }

    public boolean checkAdminExists(String username){
        for(Admin admin: data.getAdmins()){
            if(admin.getUsername().compareTo(username) == 0)
                return true;
        }
        return false;
    }

    public boolean checkUserExists(String username){
        for(User user: data.getUsers()){
            if(user.getUsername().compareTo(username) == 0)
                return true;
        }
        return false;
    }

    public boolean checkElectionExists(String electionName) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitulo().compareTo(electionName) == 0 && checkElectionIsOngoing(election))
                return true;
        }
        return false;
    }
    public boolean checkElectionExists2(String electionName) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitulo().compareTo(electionName) == 0 )
                return true;
        }
        return false;
    }
    
    public String ShowElections() throws RemoteException{
        String listaEleicoes="";
        for(Election election: data.getElections()){
            listaEleicoes+=election.getTitulo()+";";
        }
        if(listaEleicoes.equals(""))return "Nao existem eleicoes.";
        return listaEleicoes;
    }

    public String ShowActiveElections() throws RemoteException{
        String listaEleicoes="";
        for(Election election: data.getElections()){
            if(checkElectionIsOngoing(election)) listaEleicoes+=election.getTitulo()+";";
        }
        if(listaEleicoes.equals(""))return "Nao existem eleicoes ativas.";
        return listaEleicoes;
    }

    public String ShowUnstartedElections() throws RemoteException{
        String listaEleicoes="";
        for(Election election: data.getElections()){
            if(checkElectionIsUnstarted(election)) listaEleicoes+=election.getTitulo()+";";
        }
        if(listaEleicoes.equals(""))return "Nao existem eleicoes nao ativas.";
        return listaEleicoes;
    }

    public String ShowFinishedElections() throws RemoteException{
        String listaEleicoes="";
        for(Election election: data.getElections()){
            if(checkElectionIsFinished(election)) listaEleicoes+=election.getTitulo()+";";
        }
        if(listaEleicoes.equals(""))return "Nao existem eleicoes acabadas.";
        return listaEleicoes;
    }

    public boolean checkElectionIsUnstarted(Election election){
        Calendar currentDate = Calendar.getInstance();
        if(election.getDataI().compareTo(currentDate) == 1) return true;
        return false;
    }

    public boolean checkElectionIsFinished(Election election){
        Calendar currentDate = Calendar.getInstance();
        if(election.getDataF().compareTo(currentDate) == -1) return true;
        return false;
    }

    public boolean VerifyIfActiveElection(String nome) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitulo().compareTo(nome)==0){
                if(checkElectionIsOngoing(election)) return true;
            }
        }
        return false;
    }

    public boolean VerifyIfUnactiveElection(String nome) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitulo().compareTo(nome)==0){
                if(checkElectionIsUnstarted(election)) return true;
            }
        }
        return false;
    }
    
    public String showWhereVoted(String electionName) throws RemoteException{
        String listaDeVotosELocais="";
        if(!checkElectionExists2(electionName))return "Eleicao inexistente";
        Election election=data.getElection(electionName);
        listaDeVotosELocais = election.showWherePeopleVoted();
        return listaDeVotosELocais;
    }
    public String CreateElection(Calendar dataI, Calendar dataF, String titulo, String descricao, String instituicao) throws ElectionAlreadyExistsException{
        if(!checkElections(titulo, data.getElections())) {
            data.add(new Election(dataI, dataF, titulo, descricao, instituicao));
            data.updateRecords();
            return "Eleicao adicionada com sucesso.";
        }
        else
            return "Eleicao ja existe!";
    }

    public boolean checkElections(String titulo,ArrayList<Election> elections) {
        for(Election election: elections){
            if(election.getTitulo().compareTo(titulo) == 0)
                return true;
        }
        return false;
    }

    public boolean checkElectionIsOngoing(Election election){
        Calendar currentDate = Calendar.getInstance();
        //if(election.getDataI().getTime().before(currentDate.getTime()) && election.getDataF().getTime().after(currentDate.getTime()))return true;
        //System.out.println(election.getTitulo()+" data atual:" + currentDate.getTime() +"  data inicio:" + election.getDataI().getTime()+" data fim:" + election.getDataF().getTime());
        if((election.getDataI().compareTo(currentDate) == -1) && (election.getDataF().compareTo(currentDate) == 1)) return true;
        return false;
    }

    public boolean checkIfCanVote(String username,String electionName) throws InvalidUsername{
        User user = data.getUser(username);
        Election election = data.getElection(electionName);
        if(user.getInstituicao().compareTo(election.getInstituicao()) == 0 && checkElectionIsOngoing(election) && !VerifyHasUserAlreadyVoted(user,election)){
            return true;
        }
        return false;
    }

    public boolean VerifyHasUserAlreadyVoted(User user,Election election) throws InvalidUsername{
        for(User person: election.getListaCandidatosQueVotaram().keySet()){
            if(person.getUsername().compareTo(user.getUsername()) == 0)
                return true;
        }
        return false;
    }

    public String addVote(String username,String electionName,String listaPretendida) throws InvalidUsername, RemoteException{
        if(checkIfCanVote(username,electionName)){
            Election election = data.getElection(electionName);
            for(Listas lista: election.getListas()){
                if(lista.getName().compareTo(listaPretendida)==0){
                    lista.incrementaVoto();
                    election.getListaCandidatosQueVotaram().put(data.getUser(username),Calendar.getInstance());
                    data.updateRecords();
                    return "Obrigado por votar.";
                }
            }
        }
        return "Ja votou nesta eleicao!";
    }

    public String ShowListsFromElection(String electionName) throws RemoteException{
        String listasDaEleicao="";
        Election election = data.getElection(electionName);
        for(Listas lista: election.getListas()){
            listasDaEleicao+=lista.getName()+";";
        }
        if(listasDaEleicao.equals(""))return "Nao existem listas nesta eleicao.";
        return listasDaEleicao;
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
    
    public String formatListsMap(String electionName){
        Election election = data.getElection(electionName);
        int i=0;
        String formatted ="item_count|";
        String saving="";
        for(Listas lista: election.getListas()){
            saving+="item_"+i+"_name|"+lista.getName()+";";
            i++;
        }
        if(saving.length()!=0){
            saving=saving.substring(0, saving.length()-1);
            formatted=formatted+i+";"+saving;
        }else{
            formatted=formatted+i;
        }
        return formatted;
    }

    public void removeTableFromElection(Table table, Election election) throws RemoteException{
        election.getListaMesas().remove(table);
    }

    public String evalCredentials(String username, String password) throws RemoteException,Exception {
        if(data.getUser(username).getLoggedIn())return "User already voting";
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
                    return username;
                }
            }
        }
        return "";
    }

    public String editElection1(String change, String property,String electionName) throws RemoteException{
        if(!VerifyIfUnactiveElection(electionName)) return "Eleicao nao existe/nao pode ser editada";
        for(Election election: data.getElections()){
            if(election.getTitulo().compareTo(electionName)==0){
                if(property.compareTo("titulo")==0){
                    election.setTitulo(change);
                    data.updateRecords();
                    return "Titulo mudado com sucesso!";
                }
                else if(property.compareTo("descricao")==0){
                    election.setDescricao(change);
                    data.updateRecords();
                    return "Descricao mudada com sucesso!";
                }
                else if(property.compareTo("instituicao")==0){
                    election.setInstituicao(change);
                    data.updateRecords();
                    return "Instituicao mudada com sucesso!";
                }
            }
            
        }
        return "Eleicao nao foi editada.";
    }

    public String editElection2(Calendar change, String property,String electionName) throws RemoteException{
        if(!VerifyIfUnactiveElection(electionName)) return "Eleicao nao existe/nao pode ser editada";
        for(Election election: data.getElections()){
            if(election.getTitulo().compareTo(electionName)==0){
                if(property.compareTo("dataI")==0){
                    election.setDataI(change);
                    data.updateRecords();
                    return "Data inicial mudada com sucesso!";
                }
                else if(property.compareTo("dataF")==0){
                    election.setDataF(change);
                    data.updateRecords();
                    return "Data final mudada com sucesso!";
                }
            }
        }
        return "Eleicao nao foi editada.";
    }

    public String showResultsPastElection(String electionName)throws RemoteException{
        String results="";
        int totalVotos=0;
        double perc=0;
        for(Listas lista: data.getElection(electionName).getListas()){
            totalVotos += lista.getVotos();
        }
        for(Listas lista: data.getElection(electionName).getListas()){
            perc = (double)(((double)lista.getVotos()/(double)totalVotos)*100);
            results += lista.getName()+" : "+ lista.getVotos()+" votos("+perc+"%)\n";
        }
        return results;
    }

    public long backupServer(){
        return ProcessHandle.current().pid();
    }

    public static void main(String[] args) throws AccessException, RemoteException, NotBoundException, MalformedURLException, InterruptedException{
        Boolean alive = false;
        //System.getProperties().put("java.security.policy", "policy.all");
        while(!alive){
            try {
                RMIServer rmi = new RMIServer();
                //Naming.rebind("server", rmi);
                LocateRegistry.createRegistry(7001).rebind("server", rmi);
                System.out.println("RMI Server ready.");
                alive = true;
            } catch (Exception e) {
                alive = false;
                System.out.println("To sleep I go, only 10 sec tho :(");
                Thread.sleep(10000);
            }
        }
            
    }

}