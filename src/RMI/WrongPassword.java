package RMI;

public class WrongPassword extends Exception{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public WrongPassword(String s){
        super(s);
    }
}
