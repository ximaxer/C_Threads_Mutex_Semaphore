package misc.excep;

public class UsernameAlreadyInUseException extends Exception{
    public UsernameAlreadyInUseException(String s){
        super(s);
    }
}
