package RMI;

import java.io.Serializable;

public class Table implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
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
