package RMI;

public class UsernameAlreadyExistsException extends Exception{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UsernameAlreadyExistsException(String s){
        super(s);
    }
}
