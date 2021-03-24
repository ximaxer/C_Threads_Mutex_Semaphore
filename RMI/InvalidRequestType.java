package RMI;

public class InvalidRequestType extends Throwable {
    public InvalidRequestType(String type) {
        super(type);
    }
}
