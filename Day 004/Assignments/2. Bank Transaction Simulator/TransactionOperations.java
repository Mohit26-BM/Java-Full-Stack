package bankSimulator;

public interface TransactionOperations {

    void deposit(double amount);

    void withdraw(double amount) throws InsufficientBalanceException;

    void transfer(BankAccount receiver, double amount);
}