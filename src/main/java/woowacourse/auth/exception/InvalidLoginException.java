package woowacourse.auth.exception;

public class InvalidLoginException extends RuntimeException {

    public InvalidLoginException(final String message) {
        super(message);
    }
}
