package fileAuditLogger;

public class AuditLogException extends Exception {

    AuditLogException(String message) {
        super(message);
    }

    AuditLogException(String message, Throwable cause) {
        super(message, cause);
    }
}