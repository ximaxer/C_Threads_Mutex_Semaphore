package RMI;

import java.net.*;
import java.io.*;
import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String instituicao;
    private int telefone;
    private String morada;
    private int CC;
    private Date valCC;
    private boolean voted;
    private String type;
    
    public User(String username, String password, String instituicao, int telefone, String morada, int CC, Date valCC, boolean voted,String type) {
        this.username = username;
        this.password = password;
        this.instituicao = instituicao;
        this.telefone = telefone;
        this.morada = morada;
        this.CC = CC;
        this.valCC = valCC;
        this.voted = voted;
        this.type = type;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getInstituicao() {
        return this.instituicao;
    }

    public int getTelefone() {
        return this.telefone;
    }

    public String getMorada() {
        return this.morada;
    }

    public int getCC() {
        return this.CC;
    }

    public Date getValCC() {
        return this.valCC;
    }

    public boolean isVoted() {
        return this.voted;
    }

    public String getType() {
        return this.type;
    }
}
