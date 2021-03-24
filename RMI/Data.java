package RMI;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;



public class Data {
    private ArrayList<User> users;
    private ArrayList<Admin> admins;
    private HashMap<String, HashSet<URL>> reverseIndex;
    private HashMap<String, String> sessionFiles;
    private Queue<URL> toIndex;
    private HashSet<URL> indexedUrls;
    private static String DEFAULT_USERS_FILE = "files/usersObjFile";
    private static String DEFAULT_ADMINS_FILE = "files/adminsObjFile";
    private static String DEFAULT_REVERSED_INDEX_FILE = "files/reversedIndexObjFile";
    private HashMap<URL,ArrayList<String>> toSync;
    private ArrayList<String> topTenSearches;
    private ArrayList<URL> topTenPages;

    public Data(){
        this.users = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.sessionFiles = new HashMap<>();
        this.reverseIndex = new HashMap<>();
        this.toIndex = new LinkedList<>();
        this.toSync = new HashMap<>();
        this.topTenSearches =new ArrayList<>();
        this.topTenPages = new ArrayList<>();

    }

    private void exitProgram(){ System.exit(1); }

    public void add(User user){
        this.users.add(user);
    }

    public void add(Admin admin){
        this.admins.add(admin);
    }

    public void addToReversedIndexes(URL newUrl, ArrayList<String> words){
        for (String word: words){
            if (this.reverseIndex.get(word) == null){
                this.reverseIndex.put(word, new HashSet<>());
            }
            this.reverseIndex.get(word).add(newUrl);
        }
        this.reverseIndex.remove("");
        updateRecords();
    }

    public synchronized void loadData(){
        readFile(DEFAULT_USERS_FILE, 1);
        readFile(DEFAULT_ADMINS_FILE, 2);
        readFile(DEFAULT_REVERSED_INDEX_FILE, 3);
        logoutEveryone();
        innerGetIndexedUrls();
        fillQueue();
        updateAllLinksToThis();
        getTopSearches();
        getTopPages();
        this.sessionFiles.put("users", DEFAULT_USERS_FILE);
        this.sessionFiles.put("admins", DEFAULT_ADMINS_FILE);
        this.sessionFiles.put("reverseIndex", DEFAULT_REVERSED_INDEX_FILE);
        //System.out.println("LOADED Users: "+this.users + " AND Admins: "+ this.admins); // DEBUG
        //System.out.println("LOADED Reverse Index("+this.reverseIndex.size()+")"); // DEBUG
        //System.out.println("QUEUE of URLS("+ this.toIndex.size() + ")"); //DEBUG
    }

    private void logoutEveryone() {
        for (User user: this.users)
            user.forceSetLoggedIn(false);
        for (Admin admin: this.admins)
            admin.forceSetLoggedIn(false);
    }

    public synchronized void loadData(String userFileName, String adminFileName, String reverseIndexFileName){
        readFile(reverseIndexFileName, 3);
        readFile(adminFileName, 2);
        readFile(userFileName, 1);
        logoutEveryone();
        this.sessionFiles.put("users", userFileName);
        this.sessionFiles.put("admins", adminFileName);
        this.sessionFiles.put("reverseIndex", reverseIndexFileName);
    }

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

    public HashMap<String, HashSet<URL>> getReverseIndex(){
        return this.reverseIndex;
    }

    public String getUrlToIndex(){
        return this.toIndex.remove().getUrlName();
    }

    private void updateAllLinksToThis(){
        for (URL currentUrl: this.indexedUrls){
            for(URL other : this.indexedUrls){
                if (!other.getUrlName().equals(currentUrl.getUrlName()))
                    if (other.getLinksNamesFromThis().contains(currentUrl.getUrlName()))
                        currentUrl.addLinkToThis(other);
            }
        }
    }

    public List<URL> getIndexEntry(String word){
        try {
            return orderByImportance(this.reverseIndex.get(word));
        }
        catch (Exception e){
            return null;
        }
    }

    public List<URL> orderByImportance(HashSet<URL> urls) {
        List<URL> toSort = new ArrayList<>(urls);
        toSort.sort(Collections.reverseOrder());
        return toSort;
    }

    public HashSet<String> getUrlsNames(){
        HashSet<String> urlsSet = new HashSet<>();

        for(String index: this.reverseIndex.keySet()) {
            for (URL url : this.reverseIndex.get(index)){
                urlsSet.add(url.getUrlName());
            }
        }
        return urlsSet;
    }

    private void innerGetIndexedUrls(){
        this.indexedUrls = new HashSet<>();

        for(String index: this.reverseIndex.keySet()) {
            for (URL url : this.reverseIndex.get(index)){
                this.indexedUrls.add(url);
            }
        }
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

    public synchronized void updateToIndexQueue(URL newUrl){
        HashSet<URL> uniqueLinks = new HashSet<>(newUrl.getLinksFromThis());
        this.toIndex.addAll(uniqueLinks);
    }

    public synchronized void updateRecords(){
        writeFile(this.sessionFiles.get("users"), 1);
        writeFile(this.sessionFiles.get("admins"), 2);
        writeFile(this.sessionFiles.get("reverseIndex"), 3);
    }

    public void createFiles(String userFileName, String adminFileName, String reverseIndexFileName){
        writeFile(reverseIndexFileName, 3);
        writeFile(userFileName, 1);
        writeFile(adminFileName, 2);
    }

    public void createFiles(){
        writeFile(DEFAULT_USERS_FILE, 1);
        writeFile(DEFAULT_ADMINS_FILE, 2);
        writeFile(DEFAULT_REVERSED_INDEX_FILE, 3);
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
                    this.reverseIndex = (HashMap<String, HashSet<URL>>) objStream.readObject();
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
                    this.reverseIndex.remove("");
                    objStream.writeObject(this.reverseIndex);
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

    public URL getUrl(String queryUrl) {
        URL tempURL = new URL(queryUrl);
        for (URL url: this.indexedUrls)
            if (tempURL.getUrlName().compareTo(url.getUrlName()) == 0)
                return  url;

        for (URL url: this.toIndex)
            if (tempURL.getUrlName().compareTo(url.getUrlName()) == 0)
                return url;
        return null;
    }

    public synchronized ArrayList<String> getTopSearches(){
        ArrayList<String> topSearches = new ArrayList<>();
        for(User user: this.users)
            topSearches.addAll(user.getSearchHistory());

        Map<String, Integer> countMap = new HashMap<>();
        for (String word : topSearches) {
            Integer count = countMap.get(word);
            if(count == null)
                count = 0;
            countMap.put(word, (count +1));
        }
        Map<String,Integer> topTen =
                countMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(10)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        this.topTenSearches = new ArrayList<>(topTen.keySet());
        return this.topTenSearches;
    }

    public synchronized ArrayList<URL> getTopPages(){
        List<URL> sortedIndexedUrls = orderByImportance(this.indexedUrls);
        ArrayList<URL> topTen = new ArrayList<>();
        int max = Math.min(sortedIndexedUrls.size(), 10);
        for(int i=0; i<max; i++)
            topTen.add(sortedIndexedUrls.get(i));

        this.topTenPages = topTen;
        return this.topTenPages;
    }
}
