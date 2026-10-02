package bankSimulator;

public class CurrentAccount extends BankAccount {

    private static final double OVERDRAFT_LIMIT = 5000;

    CurrentAccount(String accountNumber, String accountHolder,
                   double initialBalance)
            throws BankAccountException {

        super(accountNumber, accountHolder, initialBalance);
    }

    /*
    Current account allows withdrawal up to
    Rs. 5000 beyond the available balance.
    */

    @Override
    public void withdraw(double amount)
            throws InsufficientBalanceException {

        if (amount <= 0) {
            throw new InsufficientBalanceException(
                    "Withdrawal amount must be greater than 0");
        }

        if (balance - amount < -OVERDRAFT_LIMIT) {
            throw new InsufficientBalanceException(
                    "Overdraft limit of Rs. "
                    + OVERDRAFT_LIMIT
                    + " exceeded");
        }

        balance = balance - amount;

        System.out.println(
                "Withdrawal successful: Rs. " + amount);
    }
}
