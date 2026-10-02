package bankSimulator;

public class SavingsAccount extends BankAccount {

    private static final double MINIMUM_BALANCE = 1000;

    SavingsAccount(String accountNumber, String accountHolder,
                   double initialBalance)
            throws BankAccountException {

        super(accountNumber, accountHolder, initialBalance);

        if (initialBalance < MINIMUM_BALANCE) {
            throw new BankAccountException(
                    "Savings account requires minimum balance of Rs. "
                    + MINIMUM_BALANCE);
        }
    }

    /*
    Savings account withdrawal rule:
    Minimum balance of Rs. 1000 must be maintained.
    */

    @Override
    public void withdraw(double amount)
            throws InsufficientBalanceException {

        if (amount <= 0) {
            throw new InsufficientBalanceException(
                    "Withdrawal amount must be greater than 0");
        }

        if (balance - amount < MINIMUM_BALANCE) {
            throw new InsufficientBalanceException(
                    "Savings account must maintain minimum balance of Rs. "
                    + MINIMUM_BALANCE);
        }

        balance = balance - amount;

        System.out.println(
                "Withdrawal successful: Rs. " + amount);
    }
}