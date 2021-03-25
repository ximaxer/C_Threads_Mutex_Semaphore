
package RMI;
import java.io.*;
import java.net.*;
import java.rmi.*;
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


    public static void main(String args[]) throws AccessException, RemoteException, NotBoundException, UsernameAlreadyExistsException{ 
        
       

        
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
        Date valCC = new Date(null);
        String type = "";
        String departamento = "";
        int nrterminais;

        System.out.printf("\n1 - Registar Pessoas\n2 - Criar mesa de voto\n3 - Associar mesa de voto\n4 - Criar eleicao");
        try{
            texto = reader.readLine();
        }catch(Exception e){}
        switch(texto){
                case "1":       //Registar Pessoas
                System.out.printf("\nIntroduza as suas credenciais da seguinte forma:");
                System.out.printf("\nUsername\nPassword\nInstituicao\nTelefone\nMorada\nNumero de CC\nValidade de CC(mm/dd/aaaa)\nTipo de Individuo(aluno/membro/administrador)");
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
                    texto = reader.readLine();          //valCC
                    valCC = new Date(texto);
                    do{
                        type = reader.readLine();           //tipo
                    }while(!type.equals("aluno") || !type.equals("membro") || !type.equals("administrador"));
                }catch(Exception e){}
                rmi.RegisterPerson(username, password, instituicao, telefone, morada, CC, valCC, type);
                break;
            case "2":       //Criar mesa de voto
                System.out.printf("\nIntroduza a mesa de voto da seguinte forma:");
                System.out.printf("\nDepartamento onde a mesa de voto se encontra\nNumero de terminais de voto da respetiva mesa");
                try{   
                    departamento = reader.readLine();       //departamento
                    do{
                        texto = reader.readLine();       //nr_terminais
                    }while(!isInteger(texto));
                    nrterminais = Integer.parseInt(texto);
                }catch(Exception e){}

                break;
            case "3":       //Associar mesa de voto
                break;
            case "4":       //Criar eleicao
                break;
        }
    }
}
