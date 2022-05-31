package woowacourse.shoppingcart.dto;

public class ErrorResponse {

    private int errorCode;
    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}

