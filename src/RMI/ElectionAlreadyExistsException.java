package RMI;

public class ElectionAlreadyExistsException extends Exception{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ElectionAlreadyExistsException(String s){
        super(s);
    }
}