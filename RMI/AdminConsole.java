
package RMI;
import java.io.*;
import java.net.*;
import java.rmi.*;
import java.text.*;
import java.rmi.registry.LocateRegistry;
import java.util.*;
public class AdminConsole {
    private static RMIInterface rmi;
    public static boolean isInteger(String string) {
        try {
            Integer.valueOf(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void ShowListas(String lista){
        if (lista.equals("Nao existem eleicoes ativas")){
            System.out.printf(lista);
            return;
        }
        for (String fragmento_lista : lista.split(";")) {
            System.out.printf("\n"+fragmento_lista);
        }
        System.out.printf("\n");
    }


    public static void main(String args[]) throws AccessException, RemoteException, NotBoundException, UsernameAlreadyExistsException, ElectionAlreadyExistsException, MalformedURLException{ 
        
        rmi = (RMIInterface) Naming.lookup("server");
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
        
        System.out.printf("1 - Registar Pessoas\n2 - Criar mesa de voto\n3 - Associar mesa de voto\n4 - Desassociar mesa de voto\n5 - Criar eleicao\n6 - Adicionar lista a eleicao\n");
        try{
            texto = reader.readLine();
        }catch(Exception e){}
        switch(texto){
            case "1":       //Registar Pessoas
                System.out.printf("Introduza as suas credenciais:\n");
                //System.out.printf("Username\nPassword\nInstituicao\nTelefone\nMorada\nNumero de CC\nValidade de CC (mm/dd/aaaa;hh:mm)\nTipo de Individuo(aluno/membro/administrador)\n");
                try{
                    System.out.print("username: ");
                    username = reader.readLine();       //username
                    System.out.print("Password: ");
                    password = reader.readLine();       //password
                    System.out.print("Instituicao: ");
                    instituicao = reader.readLine();    //instituicao
                    System.out.print("Telefone: ");
                    do{
                        texto = reader.readLine();          //telefone
                    }while(!isInteger(texto));
                    telefone = Integer.parseInt(texto, 10);
                    System.out.print("Morada: ");
                    morada = reader.readLine();         //morada
                    System.out.print("Numero de CC: ");
                    do{
                        texto = reader.readLine();          //CC
                    }while(!isInteger(texto));
                    CC = Integer.parseInt(texto, 10);
                    System.out.print("Validade de CC (mm/dd/aaaa;hh:mm): ");
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
                        System.out.print("Tipo de Individuo(aluno/membro/administrador): ");
                        type = reader.readLine();           //tipo
                        
                    }while(type.compareTo("aluno") != 0 && type.compareTo("membro") != 0 && type.compareTo("administrador") != 0);
                }catch(Exception e){}
                rmi.RegisterPerson(username, password, instituicao, telefone, morada, CC, valCC, type);
                break;
            case "2":       //Criar mesa de voto
                System.out.printf("Introduza o departamento onde a  mesa de voto se encontra:\n");
                try{   
                    departamento = reader.readLine();       //departamento
                }catch(Exception e){}
                    rmi.adicionarMesaDeVoto(departamento);
                break;
                case "3":       //Associar mesa de voto
                System.out.printf("Qual a eleicao a qual quer associar mesas?\n");
                String eleicoes = rmi.ShowActiveElections();
                ShowListas(eleicoes);
                try{
                    eleicao = reader.readLine();
                }catch(Exception e){}
                System.out.printf("\nQual o nome do departamento da mesa desejada?\n");
                try{
                    texto = reader.readLine();
                }catch(Exception e){}
                    result= rmi.addTableToElection(texto, eleicao);
                    System.out.printf("\n%s",result);
                break;
            case "4":       //Associar mesa de voto
                System.out.printf("Qual a eleicao da qual quer desassociar mesas?\n");
                eleicoes = rmi.ShowActiveElections();
                ShowListas(eleicoes);
                try{
                    eleicao = reader.readLine();
                }catch(Exception e){}
                System.out.printf("Qual o nome do departamento da mesa desejada?\n");
                try{
                    texto = reader.readLine();
                }catch(Exception e){}
                    result= rmi.removeTableFromElection(texto, eleicao);
                    System.out.printf("%s\n",result);
                break;
            case "5":       //criar eleicao
                try{
                    System.out.printf("Introduza a informacao da seguinte forma:\n");
                    //System.out.printf("Titulo\nBreve descricao\nInstituicao\nData de Inicio (mm/dd/aaaa;hh:mm)\nData de Fim (mm/dd/aaaa;hh:mm)\n");
                    System.out.printf("Titulo: ");
                    titulo = reader.readLine();         //Titulo
                    System.out.printf("Breve descricao: ");
                    descricao = reader.readLine();      //Descricao
                    System.out.printf("Instituicao: ");
                    instituicao = reader.readLine();    //Instituicao
                    System.out.printf("Data de Inicio (mm/dd/aaaa;hh:mm): ");
                    do{
                        texto = reader.readLine();          //Data Inicial
                        String[] segment = texto.split(";");
                        String[] dataIn = segment[0].split("/");
                        String[] timeI = segment[1].split(":");
                        mes=Integer.parseInt(dataIn[0]);
                        dia=Integer.parseInt(dataIn[1]);
                        ano=Integer.parseInt(dataIn[2]);
                        hora=Integer.parseInt(timeI[0]);
                        minuto=Integer.parseInt(timeI[1]);
                    }while((mes>12 || mes<1 )||(dia>31 || dia<1 )||(ano>2100 || ano<1980 )||(hora>23 || hora<0 )||(minuto>59 || minuto<0 ));
                    dataI.set(ano,mes,dia,hora,minuto);
                    mes=13;
                    dia=32;
                    ano=0;
                    hora=25;
                    minuto=61;
                    System.out.printf("Data de Fim (mm/dd/aaaa;hh:mm): ");
                    do{
                        texto = reader.readLine();          //Data Final
                        String[] segment = texto.split(";");
                        String[] dataFi = segment[0].split("/");
                        String[] timeF = segment[1].split(":");
                        mes=Integer.parseInt(dataFi[0]);
                        dia=Integer.parseInt(dataFi[1]);
                        ano=Integer.parseInt(dataFi[2]);
                        hora=Integer.parseInt(timeF[0]);
                        minuto=Integer.parseInt(timeF[1]);
                    }while((mes>12 || mes<1 )||(dia>31 || dia<1 )||(ano>2100 || ano<1980 )||(hora>23 || hora<0 )||(minuto>59 || minuto<0 ));
                    dataF.set(ano,mes,dia,hora,minuto);
                }catch(Exception e){}
                String b = rmi.CreateElection(dataI, dataF, titulo, descricao, instituicao);
                System.out.println(b);

                break;
            case "6":       //Associar lista a eleicao
            String lista="";
            System.out.printf("Qual a eleicao a qual quer adicionar uma lista?\n");
            lista = rmi.ShowActiveElections();
            ShowListas(lista);
            try{
                eleicao = reader.readLine();
            }catch(Exception e){}
            System.out.printf("Qual o nome da lista que quer adicionar?\n");
            try{
                texto = reader.readLine();
            }catch(Exception e){}
                result= rmi.addListaToElection(texto, eleicao);
                System.out.printf("%s\n",result);
            break;
        }
    }
}
