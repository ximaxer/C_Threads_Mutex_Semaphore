package RMI;

import java.io.Serializable;
import java.net.InetAddress;

public class Table implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String departamento;
    private String IP;
    private int terminaisAtivos;
    
    public Table(String departamento,String ip){
        this.departamento = departamento;
        this.IP=ip;
        this.terminaisAtivos = 0;
    }

    public String getDepartamento() {
        return departamento;
    }

    public String getIP() {
        return IP;
    }

    public int getTerminaisAtivos() {
        return terminaisAtivos;
    }
}
