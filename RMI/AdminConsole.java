
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

    public static boolean isAddressValid(String address){
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
        String ip = "";
        String port ="";
        String titulo = "";
        String descricao = "";
        String eleicao = "";
        String result = "";
        String altString = "";
        Calendar  altCalendar = Calendar.getInstance();
        System.out.printf("1 - Registar Pessoas\n2 - Criar mesa de voto\n3 - Associar mesa de voto\n4 - Desassociar mesa de voto\n5 - Criar eleicao\n6 - Adicionar lista a eleicao\n7 - Mostrar local e momento no qual votou cada eleitor\n8 - Editar eleicao\n");
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
                    valCC.set(ano,mes-1,dia,hora,minuto);
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
                System.out.printf("Introduza o ip da mesa de voto\n");
                try{   
                    ip = reader.readLine();       //departamento
                    System.out.printf("Introduza a porta da mesa de voto\n");
                    port = reader.readLine();       //departamento
                }catch(Exception e){}
                    if(isAddressValid(ip))rmi.adicionarMesaDeVoto(departamento,ip,port);
                    else System.out.printf("Endereco invalido!\n");
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
            case "4":       //Desassociar mesa de voto
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
                    dataI.set(ano,mes-1,dia,hora,minuto);
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
                    dataF.set(ano,mes-1,dia,hora,minuto);
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
            case "7":    //Saber onde cada pessoa votou
                String listaDeVotos="";
                System.out.printf("Qual a eleicao que quer saber onde cada pessoa votou?\n");
                lista = rmi.ShowActiveElections();
                ShowListas(lista);
                try{
                    eleicao = reader.readLine();
                }catch(Exception e){}
                listaDeVotos=rmi.showWhereVoted(eleicao);
                System.out.printf("%s\n",listaDeVotos);
            break;
            case "8":       //Editar eleicao
                System.out.printf("Qual a eleicao que quer editar?\n");
                eleicoes = rmi.ShowUnstartedElections();
                ShowListas(eleicoes);
                if(eleicoes.compareTo("Nao existem eleicoes nao ativas.")==0)break;
                try{
                    eleicao = reader.readLine();
                }catch(Exception e){}
                System.out.printf("Qual a propriedade da eleicao que quer editar?(titulo, descricao, instituicao, dataI, dataF)\n");
                try{
                    texto = reader.readLine();
                }catch(Exception e){}
                System.out.printf("Nova/novo %s: ",texto);
                try{
                    if(texto.compareTo("titulo")==0 || texto.compareTo("descricao")==0 || texto.compareTo("instituicao")==0){ 
                    altString = reader.readLine();
                    result = rmi.editElection1(altString, texto, eleicao);
                    System.out.println(result);
                    }
                    else if(texto.compareTo("dataI")==0 || texto.compareTo("dataF")==0){
                        do{
                            altString = reader.readLine();          
                            String[] segment = altString.split(";");
                            String[] dataFi = segment[0].split("/");
                            String[] timeF = segment[1].split(":");
                            mes=Integer.parseInt(dataFi[0]);
                            dia=Integer.parseInt(dataFi[1]);
                            ano=Integer.parseInt(dataFi[2]);
                            hora=Integer.parseInt(timeF[0]);
                            minuto=Integer.parseInt(timeF[1]);
                        }while((mes>12 || mes<1 )||(dia>31 || dia<1 )||(ano>2100 || ano<1980 )||(hora>23 || hora<0 )||(minuto>59 || minuto<0 ));
                        altCalendar.set(ano,mes-1,dia,hora,minuto);
                        result = rmi.editElection2(altCalendar, texto, eleicao);
                        System.out.println(result);
                    }
                }catch(Exception e){}
                break;
        }
    }
}
