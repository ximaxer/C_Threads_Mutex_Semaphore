package RMI;


public class Login {
    private String username;
    private String password;
    private Data sessionData;
    private boolean isAdmin;
    private User sessionUser;

    public Login(String username, String password, Data sessionData) throws Exception {
        this.username = username;
        this.password = password;
        this.sessionData = sessionData;
        evalCredentials();
    }

    private void evalCredentials() throws Exception {
        // check if it is a user
        this.sessionUser = checkAdmins();
        if (sessionUser != null){
            this.isAdmin = true;
        }
        else{
            // check if it is admin
            this.sessionUser = checkUsers();
            if (sessionUser != null)
                this.isAdmin = false;
            // its neither user nor admin
            else
                throw new InvalidUsername("User not found!");
        }

        this.sessionUser.setLoggedIn(true);
        this.sessionData.updateRecords();
    }

    private User checkAdmins() throws WrongPassword{
        for (Admin admin: this.sessionData.getAdmins()){
            if (admin.getUsername().compareTo(this.username) == 0){
                if (!admin.checkPassword(this.password))
                    throw new WrongPassword("Incorrect Password!");
                else
                    return admin;
            }
        }
        return null;
    }

    private User checkUsers() throws WrongPassword{
        for (User user: this.sessionData.getUsers()){
            if (user.getUsername().compareTo(this.username) == 0){
                if (!user.checkPassword(this.password))
                    throw new WrongPassword("Incorrect Password!");
                else
                    return user;
            }
        }
        return null;
    }

    public User getUser(){
        return this.sessionUser;
    }
}
