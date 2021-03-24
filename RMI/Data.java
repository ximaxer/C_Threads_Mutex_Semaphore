package RMI;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;



public class Data {
    private ArrayList<User> users;
    private ArrayList<Admin> admins;
    private ArrayList<Election> elections;

    private HashMap<String, String> sessionFiles;
    private static String DEFAULT_USERS_FILE = "files/usersObjFile";
    private static String DEFAULT_ADMINS_FILE = "files/adminsObjFile";
    private static String DEFAULT_ELECTIONS_FILE = "files/electionsObjFile";


    public Data(){
        this.users = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.elections = new ArrayList<>();
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

    public synchronized void loadData(){
        readFile(DEFAULT_USERS_FILE, 1);
        readFile(DEFAULT_ADMINS_FILE, 2);
        readFile(DEFAULT_ELECTIONS_FILE, 3);
        logoutEveryone();
        fillQueue();

        this.sessionFiles.put("users", DEFAULT_USERS_FILE);
        this.sessionFiles.put("admins", DEFAULT_ADMINS_FILE);
        this.sessionFiles.put("elections", DEFAULT_ELECTIONS_FILE);

    }

    private void logoutEveryone() {
        for (User user: this.users)
            user.forceSetLoggedIn(false);
        for (Admin admin: this.admins)
            admin.forceSetLoggedIn(false);
    }

    public synchronized void loadData(String userFileName, String adminFileName, String electionFileName){
        readFile(electionFileName, 3);
        readFile(adminFileName, 2);
        readFile(userFileName, 1);
        logoutEveryone();
        this.sessionFiles.put("users", userFileName);
        this.sessionFiles.put("admins", adminFileName);
        this.sessionFiles.put("elections", electionFileName);
    p}

    public ArrayList<User> getUsers() {
        return users;
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

    public ArrayList<Admin> getAdmins() {
        return admins;
    }

    public ArrayList<Election> getElections() {
        return elections;
    }



    private void fillQueue(){
        HashSet<URL> uniqueUrls = new HashSet<>();
        for (HashSet<URL> urls: this.reverseIndex.values())
            uniqueUrls.addAll(urls);

        ArrayList<URL> shuffledList = new ArrayList<>();

        for (URL url: uniqueUrls) {
            if (!url.getVisited())
                shuffledList.add(url);
            if (url.getLinksFromThis() != null)
                for (URL innerUrl : url.getLinksFromThis())
                    if (!innerUrl.getVisited())
                        shuffledList.add(innerUrl);
        }

        Collections.shuffle(shuffledList);
        this.toIndex.addAll(shuffledList);
    }

    public HashSet<URL> getUrls(){
        return this.indexedUrls;
    }

    public synchronized void updateUrls(URL newUrl){
        for (URL indexed: this.indexedUrls){
            if (newUrl.getLinksFromThis().contains(indexed)){
                indexed.addLinkToThis(newUrl);
            }
        }
    }

    public synchronized void updateDataToSync(String urlName, ArrayList<String> words,ArrayList<String> links){
        ArrayList<URL> linksInUrl = new ArrayList<>();
        for (String link : links)
            linksInUrl.add(new URL(link));
        URL url = new URL(urlName, linksInUrl);
        this.toSync.put(url, words);
    }

    public HashMap<URL, ArrayList<String>> getDataToSync(){
        return this.toSync;
    }

    public void clearDataToSync(){
        this.toSync = new HashMap<>();
    }
   

    public synchronized void updateRecords(){
        writeFile(this.sessionFiles.get("users"), 1);
        writeFile(this.sessionFiles.get("admins"), 2);
        writeFile(this.sessionFiles.get("elections"), 3);
    }

    public void createFiles(String userFileName, String adminFileName, String electionFileName){
        writeFile(electionFileName, 3);
        writeFile(userFileName, 1);
        writeFile(adminFileName, 2);
    }

    public void createFiles(){
        writeFile(DEFAULT_USERS_FILE, 1);
        writeFile(DEFAULT_ADMINS_FILE, 2);
        writeFile(DEFAULT_ELECTIONS_FILE, 3);
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
