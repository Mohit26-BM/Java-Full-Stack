package testbank.exception;

public class AccountHasBalanceException extends Exception {

    public AccountHasBalanceException(String message) {
        super(message);
    }
}