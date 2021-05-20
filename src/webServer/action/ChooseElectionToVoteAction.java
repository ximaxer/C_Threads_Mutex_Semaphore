package webServer.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import webServer.model.PrimesBean;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Map;

public class ChooseElectionToVoteAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 5590830L;
    private Map<String, Object> session;
    private String electionToVote;



    @Override
    public String execute() throws Exception {
        System.out.printf("eleicao: %s\n-----------------------\n",electionToVote);

        if (!electionToVote.equals("")){
            if (this.getPrimesBean().validateOngoing(electionToVote)) {
                this.getPrimesBean().setElectionToVote(electionToVote);
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
    public void setElectionToVote(String election) {
        this.electionToVote = election;
    }

    @Override
    public void setSession(Map<String, Object> session){
        this.session=session;
    }
}
