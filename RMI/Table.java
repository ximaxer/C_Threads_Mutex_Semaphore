package RMI;

import java.util.*;
import java.net.*;
import java.io.*;
import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;

public class Table {
    private String departamento;
    private int maxNumTerminais,terminaisAtivos;
    
    public Table(String departamento,String idMesa, int maxNumTerminais){
        this.departamento = departamento;
        this.maxNumTerminais = maxNumTerminais;
        this.terminaisAtivos = 0;
    }

    public String getDepartamento() {
        return departamento;
    }

    public int getMaxNumTerminais() {
        return maxNumTerminais;
    }
    public int getTerminaisAtivos() {
        return terminaisAtivos;
    }
}
