package com.thaid.asylum.api;

import com.thaid.asylum.R;

public class APIError {

    public static final ErrorType NOT_CONNECTED        = new ErrorType(1, R.string.api_not_connected);
    public static final ErrorType PARSE_ERROR          = new ErrorType(2, R.string.api_parse_error);
    public static final ErrorType AUTHORIZATION_FAILED = new ErrorType(3, R.string.api_authorization_failed);
    public static final ErrorType NETWORK_ERROR        = new ErrorType(4, R.string.api_network_error);
    public static final ErrorType UNKNOWN_ERROR        = new ErrorType(5, R.string.api_unknown_eror);

    private final ErrorType type;
    private final String message;

    public APIError(ErrorType type) {
        this(type, "");
    }

    public APIError(ErrorType type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getCode() {
        return type.code;
    }

    public int getTranslationId() {
        return type.translationId;
    }

    public String getMessage() {
        return message;
    }

    private static class ErrorType {
        private int code;
        private int translationId;

        private ErrorType(int code, int translationId) {
            this.code = code;
            this.translationId = translationId;
        }
    }
}
