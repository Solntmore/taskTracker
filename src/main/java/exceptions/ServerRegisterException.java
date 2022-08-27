package exceptions;

public class ServerRegisterException extends RuntimeException {

    public ServerRegisterException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
