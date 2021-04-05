package RMI;

import java.io.*;
import java.util.*;

public class Data {
    private ArrayList<User> users;
    private ArrayList<Admin> admins;
    private ArrayList<Election> elections;
    private ArrayList<Table> tables;

    private HashMap<String, String> files;
    private static String USERS_FILE = "files/usersObjFile";
    private static String ADMINS_FILE = "files/adminsObjFile";
    private static String ELECTIONS_FILE = "files/electionsObjFile";
    private static String TABLES_FILE = "files/tablesObjFile";


    public Data(){
        this.users = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.elections = new ArrayList<>();
        this.tables = new ArrayList<>();
        this.files = new HashMap<>();
    }

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
 


    public synchronized void loadData() throws Exception{
        readFile(USERS_FILE, 1);
        readFile(ADMINS_FILE, 2);
        readFile(ELECTIONS_FILE, 3);
        readFile(TABLES_FILE, 4);

        this.files.put("users", USERS_FILE);
        this.files.put("admins", ADMINS_FILE);
        this.files.put("elections", ELECTIONS_FILE);
        this.files.put("tables", TABLES_FILE);

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
        writeFile(this.files.get("users"), 1);
        writeFile(this.files.get("admins"), 2);
        writeFile(this.files.get("elections"), 3);
        writeFile(this.files.get("tables"), 4);
    }


    public void createFiles(){
        writeFile(USERS_FILE, 1);
        writeFile(ADMINS_FILE, 2);
        writeFile(ELECTIONS_FILE, 3);
        writeFile(TABLES_FILE, 4);
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
            }
            
            objStream.close();
            file.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    
    }

    private synchronized void writeFile(String filename, int variableFlag){
        if (filename == null){
            System.out.println("Filename is null");
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
