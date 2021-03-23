import java.net.*;
import java.io.*;
import java.io.Serializable;

public class DB implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String instituicao;
    private int telefone;
    private String morada;
    private int CC;
    private Date valCC;
    private boolean voted;
    
    public DB(String username, String password, String instituicao, int telefone, String morada, int CC, Date valCC, boolean voted) {
        this.username = username;
        this.password = password;
        this.instituicao = instituicao;
        this.telefone = telefone;
        this.morada = morada;
        this.CC = CC;
        this.valCC = valCC;
        this.voted = voted;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public int getTelefone() {
        return telefone;
    }

    public String getMorada() {
        return morada;
    }

    public int getCC() {
        return CC;
    }

    public Date getValCC() {
        return valCC;
    }

    public boolean isVoted() {
        return voted;
    }
