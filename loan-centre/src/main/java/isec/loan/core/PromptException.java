package isec.loan.core;

public class PromptException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private StatusCode statusCode;

    public PromptException(StatusCodeEnum statusCodeEnum) {
        super(statusCodeEnum.name() + "[" + statusCodeEnum.getCode() + "," + statusCodeEnum.getMessage() + "]");
        statusCode = new StatusCode(statusCodeEnum.getCode(), statusCodeEnum.getMessage());
    }

    public PromptException(String code, String message) {
        super("PromptException[" + code + "," + message + "]");
        statusCode = new StatusCode(code, message);
    }

    public PromptException(String message) {
        super("PromptException[" + StatusCodeEnum.PARAMS_ILLEGAL.getCode() + "," + message + "]");
        statusCode = new StatusCode(StatusCodeEnum.PARAMS_ILLEGAL.getCode(), message);
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

}
