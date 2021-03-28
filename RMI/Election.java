package RMI;

import java.util.*;

import java.net.*;
import java.io.*;
import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;

public class Election implements Serializable {
    private Date dataI;
    private Date dataF;
    private String titulo;
    private String descricao;
    private String instituicao;
    private ArrayList<User> listaCandidatos = new ArrayList<>();
    private ArrayList<Table> listaMesas = new ArrayList<>();

    public Election(Date dataI, Date dataF, String titulo, String descricao, String instituicao){
        this.dataI = dataI;
        this.dataF = dataF;
        this.titulo = titulo;
        this.descricao = descricao;
        this.instituicao = instituicao;
        this.listaCandidatos = new ArrayList<>();
        this.listaMesas = new ArrayList<>();
    
    }
    
    public Date getDataI() {
        return dataI;
    }

    public Date getDataF() {
        return dataF;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public ArrayList<User> getListaCandidatos() {
        return listaCandidatos;
    }

    public ArrayList<Table> getListaMesas() {
        return listaMesas;
    }
}