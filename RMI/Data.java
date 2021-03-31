package RMI;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;



public class Data {
    private ArrayList<User> users;
    private ArrayList<Admin> admins;
    private ArrayList<Election> elections;
    private ArrayList<Table> tables;
    private ArrayList<Listas> listas;

    private HashMap<String, String> sessionFiles;
    private static String USERS_FILE = "files/usersObjFile";
    private static String ADMINS_FILE = "files/adminsObjFile";
    private static String ELECTIONS_FILE = "files/electionsObjFile";
    private static String TABLES_FILE = "files/tablesObjFile";
    private static String LISTAS_FILE = "files/listasObjFile";


    public Data(){
        this.users = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.elections = new ArrayList<>();
        this.tables = new ArrayList<>();
        this.listas = new ArrayList<>();
        this.sessionFiles = new HashMap<>();
    }

    private void exitProgram(){ System.exit(1); }

    public void add(User user){
        this.users.add(user);
    }

    public void add(Admin admin){
        this.admins.add(admin);
    }

    public void add(Election election){
        this.elections.add(election);
    }

    public void add(Table table){
        this.tables.add(table);
    } 
    
    
    public void remove(Table table){
        this.tables.remove(table);
    } 

    public void add(Listas lista){
        this.listas.add(lista);
    } 

    public synchronized void loadData() throws Exception{
        readFile(USERS_FILE, 1);
        readFile(ADMINS_FILE, 2);
        readFile(ELECTIONS_FILE, 3);
        readFile(TABLES_FILE, 4);
        readFile(LISTAS_FILE, 5);

        this.sessionFiles.put("users", USERS_FILE);
        this.sessionFiles.put("admins", ADMINS_FILE);
        this.sessionFiles.put("elections", ELECTIONS_FILE);
        this.sessionFiles.put("tables", TABLES_FILE);
        this.sessionFiles.put("listas", LISTAS_FILE);

    }


    public synchronized void loadData(String userFileName, String adminFileName, String electionFileName, String tablesFileName, String listasFileName) throws Exception{
        readFile(userFileName, 1);
        readFile(adminFileName, 2);
        readFile(electionFileName, 3);
        readFile(tablesFileName, 4);
        readFile(listasFileName, 5);
        
        this.sessionFiles.put("users", userFileName);
        this.sessionFiles.put("admins", adminFileName);
        this.sessionFiles.put("elections", electionFileName);
        this.sessionFiles.put("tables", tablesFileName);
        this.sessionFiles.put("listas", listasFileName);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Table> getTables(){
        return tables;
    }

    public ArrayList<Admin> getAdmins() {
        return admins;
    }

    public ArrayList<Election> getElections() {
        return elections;
    }

    public ArrayList<Listas> getListas() {
        return listas;
    }

    public Election getElection(String name) {
        for(Election election: elections){
            if(election.getTitulo().compareTo(name) == 0){
                return election;
            }
        }
        return null;
    }


    public Table getTable(String departamento) {
        for(Table table: tables){
            if(table.getDepartamento().compareTo(departamento) == 0){
                return table;
            }
        }
        return null;
    }

    public User getUser(String username) throws InvalidUsername {
        for (Admin admin: this.admins)
            if(admin.getUsername().compareTo(username) == 0)
                return admin;

        for (User user: this.users)
            if (user.getUsername().compareTo(username) == 0)
                return user;

        throw new InvalidUsername("User not found!");
    }

    

    public synchronized void updateRecords(){
        writeFile(this.sessionFiles.get("users"), 1);
        writeFile(this.sessionFiles.get("admins"), 2);
        writeFile(this.sessionFiles.get("elections"), 3);
        writeFile(this.sessionFiles.get("tables"), 4);
        writeFile(this.sessionFiles.get("listas"), 5);
    }

    public void createFiles(String userFileName, String adminFileName, String electionFileName, String tablesFileName, String listasFileName){
        writeFile(userFileName, 1);
        writeFile(adminFileName, 2);
        writeFile(electionFileName, 3);
        writeFile(tablesFileName, 4);
        writeFile(listasFileName, 5);
    }

    public void createFiles(){
        writeFile(USERS_FILE, 1);
        writeFile(ADMINS_FILE, 2);
        writeFile(ELECTIONS_FILE, 3);
        writeFile(TABLES_FILE, 4);
        writeFile(LISTAS_FILE, 5);
    }

    private synchronized void readFile(String filename, int variableFlag){
        try {
            FileInputStream file = new FileInputStream(new File(filename));
            ObjectInputStream objStream = new ObjectInputStream(file);
            
            switch (variableFlag) {
                case 1:
                    this.users = (ArrayList<User>) objStream.readObject();
                    break;
                case 2:
                    this.admins = (ArrayList<Admin>) objStream.readObject();
                    break;
                case 3:
                    this.elections = (ArrayList<Election>) objStream.readObject();
                    break;
                case 4:
                    this.tables = (ArrayList<Table>) objStream.readObject();
                    break;
                case 5:
                    this.listas = (ArrayList<Listas>) objStream.readObject();
                    break;
            }
            
            objStream.close();
            file.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            exitProgram();
        }
    
    }

    private synchronized void writeFile(String filename, int variableFlag){
        if (filename == null){
            System.err.println("[ERROR] Filename is null");
            return;
        }

        try {
            FileOutputStream file = new FileOutputStream(new File(filename));
            ObjectOutputStream objStream = new ObjectOutputStream(file);

            switch (variableFlag){
                case 1:
                    objStream.writeObject(this.users);
                    break;
                case 2:
                    objStream.writeObject(this.admins);
                    break;
                case 3:
                    objStream.writeObject(this.elections);
                    break;
                case 4:
                    objStream.writeObject(this.tables);
                    break;
                case 5:
                    objStream.writeObject(this.listas);
                    break;
                
            }
            objStream.flush();
            objStream.close();
            file.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }




 
}
