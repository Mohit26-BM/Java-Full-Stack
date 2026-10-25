package com.company.boot.exception;

/**
 * A business rule was broken. Thrown ONLY by the service layer.
 *
 * It deliberately contains NO HTTP status: the service does not know it is
 * being called over HTTP. The web layer (GlobalExceptionHandler) maps the Type:
 *   INVALID_VALUE  -> 400 Bad Request          (the input itself is wrong)
 *   STATE_CONFLICT -> 422 Unprocessable Entity (input is fine, current state forbids it)
 */
public class BusinessRuleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public enum Type { INVALID_VALUE, STATE_CONFLICT }

    private final String rule;
    private final Type type;

    public BusinessRuleException(String rule, String message) {
        this(rule, message, Type.INVALID_VALUE);
    }

    public BusinessRuleException(String rule, String message, Type type) {
        super(message);
        this.rule = rule;
        this.type = type;
    }

    public String getRule() {
        return rule;
    }

    public Type getType() {
        return type;
    }
}
