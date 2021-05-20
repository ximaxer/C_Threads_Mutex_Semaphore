package webServer.action;

import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;
import webServer.model.PrimesBean;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateElectionAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 5590830L;
    private Map<String, Object> session;
    private String titulo, descricao, instituicao, dataI, dataF;
    private Calendar dataInicio, dataFim;

    @Override
    public String execute() throws Exception {
        System.out.printf("Titulo: %s\nDescricao: %s\nInstituicao: %s\nData Inicial: %s\nData Final: %s\n-----------------------\n",titulo , descricao, instituicao, dataI, dataF);
        int mesi,diai,anoi,horai,minutoi,mesf,diaf,anof,horaf,minutof;

        dataInicio = Calendar.getInstance();
        String[] segmenti = dataI.split(";");
        String[] datai = segmenti[0].split("/");
        String[] timei = segmenti[1].split(":");
        mesi = Integer.parseInt(datai[0]);
        diai = Integer.parseInt(datai[1]);
        anoi = Integer.parseInt(datai[2]);
        horai = Integer.parseInt(timei[0]);
        minutoi = Integer.parseInt(timei[1]);

        dataFim = Calendar.getInstance();
        String[] segmentf = dataF.split(";");
        String[] dataf = segmentf[0].split("/");
        String[] timef = segmentf[1].split(":");
        mesf = Integer.parseInt(dataf[0]);
        diaf = Integer.parseInt(dataf[1]);
        anof = Integer.parseInt(dataf[2]);
        horaf = Integer.parseInt(timef[0]);
        minutof = Integer.parseInt(timef[1]);
        if (!titulo.equals("") && !descricao.equals("") && !instituicao.equals("") &&
        (mesi<=12 && mesi>=1 && diai<=31 && diai>=1 && anoi<=2100 && anoi>=1980 && horai<=23 && horai>=0 && minutoi<=59 && minutoi>=0 ) &&
        (mesf<=12 && mesf>=1 && diaf<=31 && diaf>=1 && anof<=2100 && anof>=1980 && horaf<=23 && horaf>=0 && minutof<=59 && minutof>=0 ))
        {
            dataInicio.set(anoi,mesi-1,diai,horai,minutoi);
            dataFim.set(anof,mesf-1,diaf,horaf,minutof);
            System.out.printf("teste\n");
            if (this.getPrimesBean().CreateElection(titulo,descricao,instituicao,dataInicio,dataFim).equals("success")) {
                System.out.printf("inicio: %d/%d/%d;%d:%d\nfim: %d/%d/%d;%d:%d\n",mesi,diai,anoi,horai,minutoi,mesf,diaf,anof,horaf,minutof);
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

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setInstituicao(String instituicao) { this.instituicao = instituicao; }
    public void setDataI(String dataI) { this.dataI = dataI; }
    public void setDataF(String dataF) { this.dataF = dataF; }

    @Override
    public void setSession(Map<String, Object> session){
        this.session=session;
    }
}