package webServer.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import webServer.model.PrimesBean;

import java.util.Calendar;
import java.util.Map;

public class AddTableAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 5590830L;
    private Map<String, Object> session;
    private String departament, election;


    @Override
    public String execute() throws Exception {
        System.out.printf("departamento: %s\n: eleicao: %s\n-----------------------\n",departament, election);

        if (!departament.equals("") && !election.equals("") ){
            if (this.getPrimesBean().AssociateTable(departament,election).equals("success")) {
                return SUCCESS;
            }
        }
        return "erro";   // volta para a pagina de login
    }

    public PrimesBean getPrimesBean() {
        if(!session.containsKey("primesBean"))  // needs a fix :D
            this.setPrimesBean(new PrimesBean());
        return (PrimesBean) session.get("primesBean");
    }

    public void setPrimesBean(PrimesBean primesBean) {
        this.session.put("primesBean", primesBean);
    }
    public void setDepartament(String departament) { this.departament = departament; }
    public void setElection(String election) {
        this.election = election;
    }

    @Override
    public void setSession(Map<String, Object> session){
        this.session=session;
    }
}
