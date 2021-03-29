package RMI;

public class Listas {
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
