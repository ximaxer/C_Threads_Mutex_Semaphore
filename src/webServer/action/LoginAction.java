package webServer.action;

import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;
import webServer.model.PrimesBean;

import java.util.HashMap;
import java.util.Map;

public class LoginAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 5590830L;
    private Map<String, Object> session;
    private String uname, pass;

    @Override
	public String execute() throws Exception {
        System.out.printf("username: %s\npassword: %s\n-----------------------\n",this.uname,pass);
        if (uname != null && !uname.equals("")) {
            if (uname.equals("admin") && pass.equals("admin")) return "admin";
            this.getPrimesBean().setUname(uname);
            this.getPrimesBean().setPass(pass);
            if (this.getPrimesBean().credentialResult().equals("Success!")) {
                session.put("username", uname);
                session.put("loggedin", true); // this marks the user as logged in
                return SUCCESS;
            }
        }
        return LOGIN;   // volta para a pagina de login
    }

    public PrimesBean getPrimesBean() {
        if(!session.containsKey("primesBean"))  // needs a fix :D
            this.setPrimesBean(new PrimesBean());
        return (PrimesBean) session.get("primesBean");
    }

    public void setPrimesBean(PrimesBean primesBean) {
        this.session.put("primesBean", primesBean);
    }
    public void setUname(String uname) {
        this.uname = uname;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public void setSession(Map<String, Object> session){
    this.session=session;
    }
}