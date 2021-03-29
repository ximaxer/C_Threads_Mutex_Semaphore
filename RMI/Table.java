package RMI;

public class Table {
    private String departamento;
    private int terminaisAtivos;
    
    public Table(String departamento){
        this.departamento = departamento;
        this.terminaisAtivos = 0;
    }

    public String getDepartamento() {
        return departamento;
    }

    public int getTerminaisAtivos() {
        return terminaisAtivos;
    }
}
