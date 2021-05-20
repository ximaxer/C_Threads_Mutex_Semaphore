package RMI;


import webServer.model.BeanInterface;
import webServer.model.PrimesBean;
import webServer.model.Teste;
import webServer.model.TesteIF;
import ws.WebSocketAnnotation;

import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnError;
import javax.websocket.Session;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class RMIServer extends UnicastRemoteObject implements RMIInterface {
    Data data;
    private static BeanInterface bean;
    private static TesteIF CBteste;
    private static final long serialVersionUID = 1L;

    protected RMIServer() throws RemoteException {
        super();
        this.data = new Data();
        //data.createFiles();
        try{
            data.loadData();
        }catch (Exception e){}
    }

    /**
    * Adds a table to the specified department
    * @see #checkTableExists(String)
    */
    public ArrayList<String> getAllUsers() {
        ArrayList<String> nomes = new ArrayList<String>();
        for(User user: data.getUsers()){
            if(user.getLoggedIn())
                nomes.add(user.getUsername() + " - " + user.getCC());
        }
        return nomes;
    }
    public void registaBean(BeanInterface b) throws RemoteException{
        System.out.println(b.toString() + " working as intended");
        this.bean = b;
        bean.test();
    }
    public void subscreveTeste(TesteIF t) throws RemoteException{
        System.out.println(t.toString() + " working as intended");
        this.CBteste = t;
    }

    public void adicionarMesaDeVoto(String departamento,String ip,String port) throws RemoteException{
        if(!checkTableExists(departamento)){
            data.add(new Table(departamento,ip,port));
            data.updateRecords();
        }
    }
    /**
    * checks if a table already exists in this department
    */

    public boolean checkTableExists(String departamento){
        for(Table table: data.getTables()){
            if(table.getDepartamento().compareTo(departamento) == 0)
                return true;
        }
        return false;
    }
    /**
    * Checks to see if a table exists and if it does, returns the address of the corresponding table
    */
    public String atribuiAdressoMesa(String departamento) throws RemoteException{
        for(Table mesa : data.getTables()){
            if(mesa.getDepartamento().equals(departamento)){
                boolean a=false;
                while(!a) {
                    try {
                        bean.notificationFromRMI("Mesa " + departamento + " online.");
                        a = true;
                    } catch (Exception e) {
                    }
                }
                return mesa.getIP();
            }
        }
        return "Esta mesa nao existe!\n";
    }
    /**
    * Checks to see if a table exists and if it does, returns the port of the corresponding table
    */
    public String atribuiPortaMesa(String departamento) throws RemoteException{
        for(Table mesa : data.getTables()){
            if(mesa.getDepartamento().equals(departamento))return mesa.getPort();
        }
        return "Esta mesa nao existe!\n";
    }
    /**
    * Shows all tables
    */
    public String showMesas() throws RemoteException{
        String ret="";
        for(Table mesa : data.getTables()){
            ret+=mesa.getDepartamento()+"\n";
        }
        return ret;
    }

    public ArrayList<String> listaDeMesas() throws RemoteException{
        ArrayList<String> mesas = new ArrayList<String>();
        for(Table mesa : data.getTables()){
            mesas.add(mesa.getDepartamento());
        }
        if(mesas.isEmpty())mesas.add("Nao existem mesas no sistema");
        return mesas;
    }
    /**
    * Registers a person
    * @see #checkPerson(String)
    */
    public void RegisterPerson(String username, String password, String instituicao, int telefone, String morada, int CC, Calendar valCC,String type) throws RemoteException, UsernameAlreadyExistsException{
        
        if(checkPerson(username)) {
            data.add(new User(username, password, instituicao, telefone, morada, CC, valCC, false, type));
            data.updateRecords();
        }
        else
            throw new UsernameAlreadyExistsException("Username already exists!");
    }
    /**
    * Verifies if a person exists
    * @see #checkAdminExists(String)
    * @see #checkUserExists(String)
    */
    public boolean checkPerson(String username){
        return !(checkAdminExists(username) | checkUserExists(username));
    }
    /**
    * Verifies if the person is an admin
    */
    public boolean checkAdminExists(String username){
        for(Admin admin: data.getAdmins()){
            if(admin.getUsername().compareTo(username) == 0)
                return true;
        }
        return false;
    }
    /**
    * Verifies if the person is a user
    */
    public boolean checkUserExists(String username){
        for(User user: data.getUsers()){
            if(user.getUsername().compareTo(username) == 0)
                return true;
        }
        return false;
    }
    /**
    * Verifies the specified election exists and is ongoing
    */
    public boolean checkElectionExists(String electionName) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitulo().compareTo(electionName) == 0 && checkElectionIsOngoing(election))
                return true;
        }
        return false;
    }
    /**
    * Verifies the specified election exists
    */
    public boolean checkElectionExists2(String electionName) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitulo().compareTo(electionName) == 0 )
                return true;
        }
        return false;
    }
    /**
    * Shows the existing elections
    */
    public String ShowElections() throws RemoteException{
        String listaEleicoes="";
        for(Election election: data.getElections()){
            listaEleicoes+=election.getTitulo()+";";
        }
        if(listaEleicoes.equals(""))return "Nao existem eleicoes.";
        return listaEleicoes;
    }
    /**
    * Shows the current active elections
    * @see #checkElectionIsOngoing(Election)
    */
    public String ShowActiveElections() throws RemoteException{
        String listaEleicoes="";
        for(Election election: data.getElections()){
            if(checkElectionIsOngoing(election)) listaEleicoes+=election.getTitulo()+";";
        }
        if(listaEleicoes.equals(""))return "Nao existem eleicoes ativas.";
        return listaEleicoes;
    }
    /**
    * Shows the elections that are yet to start
    * @see #checkElectionIsUnstarted(Election)
    */
    public String ShowUnstartedElections() throws RemoteException{
        String listaEleicoes="";
        for(Election election: data.getElections()){
            if(checkElectionIsUnstarted(election)) listaEleicoes+=election.getTitulo()+";";
        }
        if(listaEleicoes.equals(""))return "Nao existem eleicoes nao ativas.";
        return listaEleicoes;
    }

    public ArrayList<String> ShowUnstartedElectionsList() throws RemoteException{
        ArrayList<String> listaEleicoes = new ArrayList<String>();
        for(Election election: data.getElections()){
            if(checkElectionIsUnstarted(election)) listaEleicoes.add(election.getTitulo());
        }
        if(listaEleicoes.isEmpty())listaEleicoes.add("Nao existem eleicoes nao ativas.");
        return listaEleicoes;
    }
    public boolean verifyUnstartedElection(String name) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitulo().equals(name))return true;
        }
        return false;
    }
    /**
    * Shows the elections that are done
    * @see #checkElectionIsFinished(Election)
    */
    public String ShowFinishedElections() throws RemoteException{
        String listaEleicoes="";
        for(Election election: data.getElections()){
            if(checkElectionIsFinished(election)) listaEleicoes+=election.getTitulo()+";";
        }
        if(listaEleicoes.equals(""))return "Nao existem eleicoes acabadas.";
        return listaEleicoes;
    }
    /**
    * Verifies if the elecion has started
    */
    public boolean checkElectionIsUnstarted(Election election){
        Calendar currentDate = Calendar.getInstance();
        if(election.getDataI().compareTo(currentDate) == 1) return true;
        return false;
    }
    /**
    * Verifies if the elecion has finished
    */
    public boolean checkElectionIsFinished(Election election){
        Calendar currentDate = Calendar.getInstance();
        if(election.getDataF().compareTo(currentDate) == -1) return true;
        return false;
    }
    /**
    * Verifies if the elecion if ongoing
    * @see #checkElectionIsOngoing(Election)
    */
    public boolean VerifyIfActiveElection(String nome) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitulo().compareTo(nome)==0){
                if(checkElectionIsOngoing(election)) return true;
            }
        }
        return false;
    }
    /**
    * Verifies if the elecion hasn't started
    * @see #checkElectionIsUnstarted(Election)
    */
    public boolean VerifyIfUnactiveElection(String nome) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitulo().compareTo(nome)==0){
                if(checkElectionIsUnstarted(election)) return true;
            }
        }
        return false;
    }
    /**
    * Shows where the voters have voted
    * @see #checkElectionExists2(String)
    */
    public String showWhereVoted(String electionName) throws RemoteException{
        String listaDeVotosELocais="";
        if(!checkElectionExists2(electionName))return "Eleicao inexistente";
        Election election=data.getElection(electionName);
        listaDeVotosELocais = election.showWherePeopleVoted();
        return listaDeVotosELocais;
    }

    public ArrayList<String> showAllVotes() throws RemoteException{
        ArrayList<String> apresenta =new ArrayList<>();
        for(Election election:data.getElections()){
            apresenta.add(election.getTitulo() +":");
            apresenta.add(" ");
            for(User user: election.getListaCandidatosQueVotaram().keySet()){
                apresenta.add(user.getUsername()+ " - " +user.getInstituicao()+" - "+election.getListaCandidatosQueVotaram().get(user).getTime());
            }
            apresenta.add(" ");
            apresenta.add(" ");
        }
        return apresenta;
    }
    public ArrayList<String> showActiveElectionList() throws RemoteException{
        ArrayList<String> listaEleicoes = new ArrayList<>();
        for(Election election: data.getElections()){
            if(checkElectionIsOngoing(election)) listaEleicoes.add(election.getTitulo());
        }
        if(listaEleicoes.isEmpty())listaEleicoes.add("Nao existem eleicoes ativas.");
        return listaEleicoes;
    }
    public String updateElection(String eleicao, String descricao, String instituicao, Calendar dataI, Calendar dataF) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitulo().equals(eleicao)){
                election.setDescricao(descricao);
                election.setInstituicao(instituicao);
                election.setDataI(dataI);
                election.setDataF(dataF);
                return "success";
            }
        }
        return "error";
    }

    public ArrayList<String> showElectionProperties(String electionName) throws RemoteException{
        SimpleDateFormat formatoDesejado = new SimpleDateFormat("MM/dd/yyyy;HH:mm");
        ArrayList<String> dados = new ArrayList<>();
        for(Election election: data.getElections()){
            if(election.getTitulo().equals(electionName)) {
                dados.add(election.getDescricao());
                dados.add(election.getInstituicao());
                String dataInicial=formatoDesejado.format(election.getDataI().getTime());
                String dataFinal=formatoDesejado.format(election.getDataF().getTime());
                dados.add(dataInicial);
                dados.add(dataFinal);
                return dados;
            }
        }
        return dados;
    }
    /**
    * Creates an election
    * @see #checkElections(String,ArrayList<Election>)
    */
    public String CreateElection(Calendar dataI, Calendar dataF, String titulo, String descricao, String instituicao) throws ElectionAlreadyExistsException{
        if(!checkElections(titulo, data.getElections())) {
            data.add(new Election(dataI, dataF, titulo, descricao, instituicao));
            data.updateRecords();
            return "Eleicao adicionada com sucesso.";
        }
        else
            return "Eleicao ja existe!";
    }
    /**
    * Checks if the election exists
    */
    public boolean checkElections(String titulo,ArrayList<Election> elections) {
        for(Election election: elections){
            if(election.getTitulo().compareTo(titulo) == 0)
                return true;
        }
        return false;
    }
    /**
    * Checks is the specified election is ongoing
    */
    public boolean checkElectionIsOngoing(Election election){
        Calendar currentDate = Calendar.getInstance();
        if((election.getDataI().compareTo(currentDate) == -1) && (election.getDataF().compareTo(currentDate) == 1)) return true;
        return false;
    }
    /**
    * Checks is the specified user is allowed to vote
    *@see #VerifyHasUserAlreadyVoted(User,Election)
    *@see #checkElectionIsOngoing(Election)
    */
    public boolean checkIfCanVote(String username,String electionName) throws InvalidUsername{
        User user = data.getUser(username);
        Election election = data.getElection(electionName);
        if(user.getInstituicao().compareTo(election.getInstituicao()) == 0 && checkElectionIsOngoing(election) && !VerifyHasUserAlreadyVoted(user,election)){
            return true;
        }
        return false;
    }
    /** 
    * Checks is the specified has already voted
    */
    public boolean VerifyHasUserAlreadyVoted(User user,Election election) throws InvalidUsername{
        for(User person: election.getListaCandidatosQueVotaram().keySet()){
            if(person.getUsername().compareTo(user.getUsername()) == 0)
                return true;
        }
        return false;
    }
    /**
    * Adds a vote if possible
    *@see #checkIfCanVote(String,String)
    */
    public String webVoteCallBack(String username,String electionName,String listaPretendida) throws InvalidUsername, RemoteException {
        String result = addVote(username,electionName,listaPretendida);
        boolean a=false;
        while(!a) {
            try {
                bean.castVoteRMI(result, username, electionName);
                bean.notificationFromRMI("user "+username+" has logged out.");
                a=true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String addVote(String username,String electionName,String listaPretendida) throws InvalidUsername, RemoteException{
        if(checkIfCanVote(username,electionName)){
            Election election = data.getElection(electionName);
            for(Listas lista: election.getListas()){
                if(lista.getName().compareTo(listaPretendida)==0){
                    lista.incrementaVoto();
                    election.getListaCandidatosQueVotaram().put(data.getUser(username),Calendar.getInstance());
                    data.updateRecords();
                    data.getUser(username).setLoggedIn(false);
                    return "Obrigado por votar.";
                }
            }
        }
        return "Ja votou nesta eleicao!";
    }
    /**
    * Show the candidate lists of an election
    */
    public String ShowListsFromElection(String electionName) throws RemoteException{
        String listasDaEleicao="";
        Election election = data.getElection(electionName);
        for(Listas lista: election.getListas()){
            listasDaEleicao+=lista.getName()+";";
        }
        if(listasDaEleicao.equals(""))return "Nao existem listas nesta eleicao.";
        return listasDaEleicao;
    }

    public ArrayList<String> electionListsList(String electionName) throws RemoteException{
        ArrayList<String> apresenta= new ArrayList<>();
        Election election = data.getElection(electionName);
        for(Listas lista: election.getListas()){
            apresenta.add(lista.getName());
        }
        if(apresenta.isEmpty())apresenta.add("Nao existem listas nesta eleicao.");
        return apresenta;
    }
    /**
    * Adds a candidate list to an election
    */
    public String addListaToElection(String listaNome, String electionName) throws RemoteException{
        if(!VerifyIfActiveElection(electionName))return "Eleicao nao existe!";
        for(Listas lista: data.getElection(electionName).getListas()){
            if(lista.getName().compareTo(listaNome) == 0)
                return "Lista ja se encontra associada a esta eleicao!";
        }
        data.getElection(electionName).getListas().add(new Listas(listaNome));
        return "Lista associada com sucesso!";
    }
    /**
    * Adds a table to an election
    *@see #VerifyIfActiveElection(String)
    *@see #checkTableExists(String)
    */
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

    public ArrayList<String> showEleicoesEMesas() throws RemoteException{
        ArrayList<String> apresenta = new ArrayList<>();
        String aux="";
        ArrayList<String> mesas=new ArrayList<>();
        if(data.getElections().isEmpty()){
            apresenta.add("Nao existem eleicoes.");
            return apresenta;
        }
        for(Election election : data.getElections()) {
            if(checkElectionIsOngoing(election)){
                aux = election.getTitulo() + " ";
                for (Table mesa : election.getListaMesas()) {
                    mesas.add(mesa.getDepartamento());
                }
                apresenta.add(aux + mesas);
                mesas.clear();
            }
        }
        return apresenta;
    }

    public ArrayList<String> showEleicoesEListas() throws RemoteException{
        ArrayList<String> apresenta = new ArrayList<>();
        String aux="";
        ArrayList<String> listas=new ArrayList<>();
        if(data.getElections().isEmpty()){
            apresenta.add("Nao existem eleicoes.");
            return apresenta;
        }
        for(Election election : data.getElections()) {
            if(checkElectionIsOngoing(election)){
                aux = election.getTitulo() + " ";
                for (Listas lista: election.getListas()) {
                    listas.add(lista.getName());
                }
                apresenta.add(aux + listas);
                listas.clear();
            }
        }
        return apresenta;
    }
    /**
    * Removes a table from an election
    *@see #VerifyIfActiveElection(String)
    *@see #checkTableExists(String)
    */
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
    /**
    * Formats and returns a string with the specified election's candidate list
    */
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
    /**
    * Evaluates an User's login credentials
    *@see #checkAdmins(String,String)
    *@see #checkUsers(String,String)
    */

    public String evalCredentials(String username, String password) throws Exception {
        if(data.getUser(username).getLoggedIn())return "User already voting";
        String validation="";
        validation=checkAdmins(username,password);
        if (validation.equals("Incorrect Password!")){
            return "Incorrect Password!";
        }else if(validation != "" && !data.getUser(username).getLoggedIn()){
            data.updateRecords();
            boolean a=false;
            while(!a) {
                try {
                    bean.notificationFromRMI(username + " has logged in.");
                    data.getUser(username).setLoggedIn(true);
                    a = true;
                } catch (Exception e) {
                }
            }
            return "Success!";
        }else {
            validation = checkUsers(username, password);
            if (validation.equals("")) {
                return "User not found!";
            } else if (validation.equals("Incorrect Password!")) {
                return "Incorrect Password!";
            } else if (data.getUser(username).getLoggedIn()){
                return "User already logged in";
            }else{
                data.updateRecords();
                boolean a=false;
                while(!a) {
                    try {
                        bean.notificationFromRMI(username + " has logged in.");
                        data.getUser(username).setLoggedIn(true);
                        a = true;
                    } catch (Exception e) {
                    }
                }
                return "Success!";
            }
        }
    }

    /**
    * Evaluates an Admin's password
    */
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
    /**
    * Evaluates an User's password
    */
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
    /**
    * Edits the specified election's atributes
    */
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
    /**
    * Edits the specified election's calendar
    */
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
    /**
    * Show the results of Past elections
    */
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

    public ArrayList<String> showResultsPastElectionsList() throws RemoteException{
        ArrayList<String> apresenta = new ArrayList<>();
        for(Election election : data.getElections()){
            int totalVotos = 0;
            float percentagem = 0;
            if(checkElectionIsFinished(election)){
                apresenta.add("Eleicao: "+election.getTitulo());
                apresenta.add("Votos:");
                for(Listas lista : election.getListas()) {
                    totalVotos += lista.getVotos();
                }
                for(Listas lista : election.getListas()) {
                    percentagem = (float)(((float)lista.getVotos()/(float)totalVotos)*100);
                    apresenta.add(lista.getName()+": "+lista.getVotos()+" votos("+percentagem+"%)");
                }
                apresenta.add(" ");
            }
        }
        if(apresenta.isEmpty())apresenta.add("Nao existem eleicoes terminadas.");
        return apresenta;
    }

    public boolean verifyIfVotedOnElection(String electionName,int CC) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitulo().equals(electionName))return election.checkIfVoted(CC);
        }
        return false;
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
                /*boolean a= false;
                while(!a) {
                    try {
                        CBteste.simples();
                        a=true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }*/
                alive = true;
            } catch (Exception e) {
                alive = false;
                System.out.println("To sleep I go, only 10 sec tho :(");
                Thread.sleep(10000);
            }
        }
            
    }

}