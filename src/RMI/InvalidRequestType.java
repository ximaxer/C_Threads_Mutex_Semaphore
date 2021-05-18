package RMI;

public class InvalidRequestType extends Throwable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidRequestType(String type) {
        super(type);
    }
}
