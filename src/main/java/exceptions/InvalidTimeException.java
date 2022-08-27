package exceptions;

public class InvalidTimeException extends RuntimeException {

    public InvalidTimeException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}