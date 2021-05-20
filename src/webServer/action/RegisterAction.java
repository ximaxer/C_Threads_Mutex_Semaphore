package webServer.action;

import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;
import webServer.model.PrimesBean;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 5590830L;
    private Map<String, Object> session;
    private String username, password, instituicao, telefone, morada, CC, valCC;
    private Calendar validadeCC;

    public static boolean isInteger(String string) {
        try {
            Integer.valueOf(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String execute() throws Exception {
        System.out.printf("username: %s\npassword: %s\ninstituicao: %s\ntelefone: %s\nmorada: %s\nCC: %s\nvalCC: %s\n-----------------------\n",username, password, instituicao, telefone, morada, CC, valCC);
        validadeCC = Calendar.getInstance();
        String[] segment = valCC.split(";");
        String[] data = segment[0].split("/");
        String[] time = segment[1].split(":");
        int mes=Integer.parseInt(data[0]);
        int dia=Integer.parseInt(data[1]);
        int ano=Integer.parseInt(data[2]);
        int hora=Integer.parseInt(time[0]);
        int minuto=Integer.parseInt(time[1]);
        if (!username.equals("") && !password.equals("") && !instituicao.equals("") &&
            isInteger(telefone) && !morada.equals("") && isInteger(CC) &&
            (mes<=12 && mes>=1 && dia<=31 && dia>=1 && ano<=2100 && ano>=1980 &&
            hora<=23 && hora>=0 && minuto<=59 && minuto>=0 )) {

            validadeCC.set(ano,mes-1,dia,hora,minuto);
            if (this.getPrimesBean().registerNewMan(username,password,instituicao,Integer.parseInt(telefone),morada,Integer.parseInt(CC),validadeCC).equals("success")) {
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
    public void setUsername(String uname) {
        this.username = uname;
    }
    public void setPassword(String pass) {
        this.password = pass;
    }
    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }
    public void setMorada(String morada) {
        this.morada = morada;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    public void setValCC(String valCC) {
        this.valCC = valCC;
    }
    public void setCC(String CC) {
        this.CC = CC;
    }

    @Override
    public void setSession(Map<String, Object> session){
        this.session=session;
    }
}