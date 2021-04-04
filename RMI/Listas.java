package RMI;

import java.io.Serializable;

public class Listas implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name;
    private int votos;

    public Listas(String name){
        this.name = name;
        this.votos = 0;
    }

    public String getName() {
        return this.name;
    }

    public int getVotos() {
        return this.votos;
    }

    public void incrementaVoto(){
        this.votos++;
    }
}
