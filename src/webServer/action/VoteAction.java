package webServer.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import webServer.model.PrimesBean;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Map;

public class VoteAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 5590830L;
    private Map<String, Object> session;
    private String electionToVote, lista;



    @Override
    public String execute() throws Exception {
        this.electionToVote=this.getPrimesBean().getElectionToVote();
        System.out.printf("Username: %s\nEleicao: %s\nLista: %s\n-----------------------\n",getPrimesBean().getUname(),electionToVote,lista);


        if (!lista.equals("") && !electionToVote.equals("") && !getPrimesBean().getUname().equals("")){
            if (this.getPrimesBean().castVote(getPrimesBean().getUname(),electionToVote,lista).equals("Obrigado por votar.")) {
                this.getPrimesBean().setElection(null);
                this.removeSession();
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
    public void setLista(String lista) { this.lista = lista; }
    public void setElectionToVote(String electionToVote) { this.electionToVote = electionToVote; }
    public void removeSession(){
                this.session.remove("primesBean");
                this.session=null;
    }
    @Override
    public void setSession(Map<String, Object> session){
        this.session=session;
    }
}
