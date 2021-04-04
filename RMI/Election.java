package RMI;

import java.util.*;

import java.net.*;
import java.io.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.ArrayList;

public class Election implements Serializable {
    private Calendar dataI;
    private Calendar dataF;
    private String titulo;
    private String descricao;
    private String instituicao;
    private HashMap<User,Calendar> hasVoted;
    private ArrayList<Table> listaMesas = new ArrayList<>();
    private ArrayList<Listas> listas = new ArrayList<>();

    public Election(Calendar dataI, Calendar dataF, String titulo, String descricao, String instituicao){
        this.dataI = dataI;
        this.dataF = dataF;
        this.titulo = titulo;
        this.descricao = descricao;
        this.instituicao = instituicao;
        this.hasVoted = new HashMap<>();
        this.listaMesas = new ArrayList<>();
        this.listas = new ArrayList<>();
    
    }
    
    public Calendar getDataI() {
        return dataI;
    }

    public Calendar getDataF() {
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

    public HashMap<User,Calendar> getListaCandidatosQueVotaram() {
        return hasVoted;
    }

    public ArrayList<Table> getListaMesas() {
        return listaMesas;
    }

    public ArrayList<Listas> getListas() {
        return listas;
    }
  

    public void setDataI(Calendar newDataI) {
        this.dataI = newDataI;
    }

    public void setDataF(Calendar newDataF) {
        this.dataF = newDataF;
    }

    public void setTitulo(String newTitulo) {
        this.titulo = newTitulo;
    }

    public void setDescricao(String newDescricao) {
        this.descricao = newDescricao;;
    }

    public void setInstituicao(String newInstituicao) {
        this.instituicao = newInstituicao;
    }

    public String showWherePeopleVoted(){
        String info="";
        for(User user: this.getListaCandidatosQueVotaram().keySet()){
            info+=user.getUsername()+ " - " +user.getInstituicao()+" - "+this.getListaCandidatosQueVotaram().get(user).getTime()+"\n";
        }
        return info;
    }
}