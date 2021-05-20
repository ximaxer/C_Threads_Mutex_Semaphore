package webServer.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import webServer.model.PrimesBean;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Map;

public class CreateTableAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 5590830L;
    private Map<String, Object> session;
    private String departamento, ip, port;

    public static boolean isAddressValid(String address) {
        if(address.length()>15)return false;
        String repartida[] =address.split("\\.");
        System.out.println(address);
        if(repartida.length>4 || repartida.length<=0)return false;
        int primeiro, segundo, terceiro, quarto;
        try {
            primeiro=Integer.parseInt(repartida[0]);
            segundo=Integer.parseInt(repartida[1]);
            terceiro=Integer.parseInt(repartida[2]);
            quarto=Integer.parseInt(repartida[3]);
        } catch (final NumberFormatException e) {
            return false;
        }
        if(primeiro>239 || primeiro<224 || segundo>255 || segundo<0 || terceiro>255 || terceiro<0 || quarto>255 || quarto<0)return false;
        return true;
    }

    @Override
    public String execute() throws Exception {
        System.out.printf("departamento: %s\nIP: %s\nporta: %s\n-----------------------\n",departamento, ip, port);

        if (!departamento.equals("") && !ip.equals("") && !port.equals("") && isAddressValid(ip)){
            if (this.getPrimesBean().createNewTable(departamento,ip,port).equals("success")) {
                return SUCCESS;
            }
        }
        return "erro";   // volta para a pagina de login
    }

    public PrimesBean getPrimesBean() throws RemoteException {
        if(!session.containsKey("primesBean"))  // needs a fix :D
            this.setPrimesBean(new PrimesBean());
        return (PrimesBean) session.get("primesBean");
    }

    public void setPrimesBean(PrimesBean primesBean) {
        this.session.put("primesBean", primesBean);
    }
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public void setSession(Map<String, Object> session){
        this.session=session;
    }
}
