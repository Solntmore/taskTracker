package exceptions;

public class ServerLoadException extends RuntimeException {
    public ServerLoadException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
