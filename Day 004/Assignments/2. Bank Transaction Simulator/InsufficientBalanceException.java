package bankSimulator;

public class InsufficientBalanceException extends BankAccountException {

    InsufficientBalanceException(String message) {
        super(message);
    }
}