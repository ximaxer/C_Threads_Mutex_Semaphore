package RMI;

public class InvalidUsername extends Exception{
    private static final long serialVersionUID = 3L;
    public InvalidUsername(String s){
        super(s);
    }
}
