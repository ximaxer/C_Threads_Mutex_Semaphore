package RMI;

import java.util.*;

public class Admin extends User {

    public Admin(String username, String password, String instituicao, int telefone, String morada, int CC, Date valCC, boolean voted,String type){
        super(username, password, instituicao, telefone, morada, CC, valCC, voted, type);
    }

    @Override
    public String toString() {
        return "Admin: " + this.getUsername();
    }
}