package logic.miscellaneous.Exceptions;

public class InvalidRequestType extends Throwable {
    public InvalidRequestType(String type) {
        super(type);
    }
}
