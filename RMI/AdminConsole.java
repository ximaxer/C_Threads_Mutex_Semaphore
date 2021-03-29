
package RMI;
import java.io.*;
import java.net.*;
import java.rmi.*;
import java.text.*;
import java.rmi.registry.LocateRegistry;
import java.util.*;
public class AdminConsole {

    public static boolean isInteger(String string) {
        try {
            Integer.valueOf(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void ShowListas(String lista){
        for (String fragmento_lista : lista.split(";")) {
            System.out.printf("\n"+fragmento_lista);
        }
    }


    public static void main(String args[]) throws AccessException, RemoteException, NotBoundException, UsernameAlreadyExistsException, ElectionAlreadyExistsException{ 
        
        RMIInterface rmi = (RMIInterface) LocateRegistry.getRegistry(7420).lookup("server");
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		String texto = "";
        String username = "";
        String password = "";
        String instituicao = "";
        int telefone=0;
        String morada = "";
        int CC=0;
        int mes=13,dia=32,ano=0,hora=25,minuto=61;
        Calendar  valCC = Calendar.getInstance();
        Calendar  dataI = Calendar.getInstance();
        Calendar  dataF = Calendar.getInstance();
        String type = "";
        String departamento = "";
        String titulo = "";
        String descricao = "";
        String eleicao = "";
        String result = "";

        System.out.printf("\n1 - Registar Pessoas\n2 - Criar mesa de voto\n3 - Associar mesa de voto\n4 - Desassociar mesa de voto\n5 - Criar eleicao\n6 - Adicionar lista a eleicao");
        try{
            texto = reader.readLine();
        }catch(Exception e){}
        switch(texto){
            case "1":       //Registar Pessoas
                System.out.printf("\nIntroduza as suas credenciais da seguinte forma:");
                System.out.printf("\nUsername\nPassword\nInstituicao\nTelefone\nMorada\nNumero de CC\nValidade de CC (mm/dd/aaaa;hh:mm)\nTipo de Individuo(aluno/membro/administrador)");
                try{
                    username = reader.readLine();       //username
                    password = reader.readLine();       //password
                    instituicao = reader.readLine();    //instituicao
                    do{
                        texto = reader.readLine();          //telefone
                    }while(!isInteger(texto));
                    telefone = Integer.parseInt(texto, 10);
                    morada = reader.readLine();         //morada
                    do{
                        texto = reader.readLine();          //CC
                    }while(!isInteger(texto));
                    CC = Integer.parseInt(texto, 10);
                    do{
                        texto = reader.readLine();          //valCC
                        String[] segment = texto.split(";");
                        String[] data = segment[0].split("/");
                        String[] time = segment[1].split(":");
                        mes=Integer.parseInt(data[0]);
                        dia=Integer.parseInt(data[1]);
                        ano=Integer.parseInt(data[2]);
                        hora=Integer.parseInt(time[0]);
                        minuto=Integer.parseInt(time[1]);
                    }while((mes>12 || mes<1 )||(dia>31 || dia<1 )||(ano>2100 || ano<1980 )||(hora>23 || hora<0 )||(minuto>59 || minuto<0 ));
                    valCC.set(ano,mes,dia,hora,minuto);
                    do{
                        type = reader.readLine();           //tipo
                    }while(!type.equals("aluno") || !type.equals("membro") || !type.equals("administrador"));
                }catch(Exception e){}
                rmi.RegisterPerson(username, password, instituicao, telefone, morada, CC, valCC, type);
                break;
            case "2":       //Criar mesa de voto
                System.out.printf("\nIntroduza o departamento onde a  mesa de voto se encontra:");
                try{   
                    departamento = reader.readLine();       //departamento
                }catch(Exception e){}
                    rmi.adicionarMesaDeVoto(departamento);
                break;
                case "3":       //Associar mesa de voto
                System.out.printf("\nQual a eleicao a qual quer associar mesas?");
                rmi.ShowActiveElections();
                try{
                    eleicao = reader.readLine();
                }catch(Exception e){}
                System.out.printf("\nQual o nome do departamento da mesa desejada?");
                try{
                    texto = reader.readLine();
                }catch(Exception e){}
                    result= rmi.addTableToElection(texto, eleicao);
                    System.out.printf("\n%s",result);
                break;
            case "4":       //Associar mesa de voto
                System.out.printf("\nQual a eleicao da qual quer desassociar mesas?");
                rmi.ShowActiveElections();
                try{
                    eleicao = reader.readLine();
                }catch(Exception e){}
                System.out.printf("\nQual o nome do departamento da mesa desejada?");
                try{
                    texto = reader.readLine();
                }catch(Exception e){}
                    result= rmi.removeTableFromElection(texto, eleicao);
                    System.out.printf("\n%s",result);
                break;
            case "5":       //criar eleicao
                try{
                    System.out.printf("\nIntroduza a informacao da seguinte forma:");
                    System.out.printf("\nTitulo\nBreve descricao\nInstituicao\nData de Inicio (mm/dd/aaaa;hh:mm)\nData de Fim (mm/dd/aaaa;hh:mm)");
                    titulo = reader.readLine();         //Titulo
                    descricao = reader.readLine();      //Descricao
                    instituicao = reader.readLine();    //Instituicao
                    do{
                        texto = reader.readLine();          //Data Inicial
                        String[] segment = texto.split(";");
                        String[] data = segment[0].split("/");
                        String[] time = segment[1].split(":");
                        mes=Integer.parseInt(data[0]);
                        dia=Integer.parseInt(data[1]);
                        ano=Integer.parseInt(data[2]);
                        hora=Integer.parseInt(time[0]);
                        minuto=Integer.parseInt(time[1]);
                    }while((mes>12 || mes<1 )||(dia>31 || dia<1 )||(ano>2100 || ano<1980 )||(hora>23 || hora<0 )||(minuto>59 || minuto<0 ));
                    dataI.set(ano,mes,dia,hora,minuto);
                    do{
                        texto = reader.readLine();          //Data Final
                        String[] segment = texto.split(";");
                        String[] data = segment[0].split("/");
                        String[] time = segment[1].split(":");
                        mes=Integer.parseInt(data[0]);
                        dia=Integer.parseInt(data[1]);
                        ano=Integer.parseInt(data[2]);
                        hora=Integer.parseInt(time[0]);
                        minuto=Integer.parseInt(time[1]);
                    }while((mes>12 || mes<1 )||(dia>31 || dia<1 )||(ano>2100 || ano<1980 )||(hora>23 || hora<0 )||(minuto>59 || minuto<0 ));
                    dataF.set(ano,mes,dia,hora,minuto);
                }catch(Exception e){}
                rmi.CreateElection(dataI, dataF, titulo, descricao, instituicao);
                break;
            case "6":       //Associar lista a eleicao
            String lista="";
            System.out.printf("\nQual a eleicao a qual quer adicionar uma lista?");
            lista = rmi.ShowActiveElections();
            ShowListas(lista);
            try{
                eleicao = reader.readLine();
            }catch(Exception e){}
            System.out.printf("\nQual o nome da lista que quer adicionar?");
            try{
                texto = reader.readLine();
            }catch(Exception e){}
                result= rmi.addListaToElection(texto, eleicao);
                System.out.printf("\n%s",result);
            break;
        }
    }
}
