package bankSimulator;

public abstract class BankAccount
        implements TransactionOperations {

    private String accountNumber;
    private String accountHolder;
    protected double balance;

    /*
    Constructor
    */

    BankAccount(String accountNumber, String accountHolder,
                double initialBalance)
            throws BankAccountException {

        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new BankAccountException(
                    "Account number cannot be empty");
        }

        if (accountHolder == null || accountHolder.trim().isEmpty()) {
            throw new BankAccountException(
                    "Account holder name cannot be empty");
        }

        if (initialBalance < 0) {
            throw new BankAccountException(
                    "Initial balance cannot be negative");
        }

        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    /*
    Deposit operation
    */

    @Override
    public void deposit(double amount) {

        if (amount <= 0) {
            System.out.println(
                    "Deposit failed: Amount must be greater than 0");
            return;
        }

        balance = balance + amount;

        System.out.println(
                "Deposit successful: Rs. " + amount);
    }

    /*
    Withdrawal operation
    */

    @Override
    public abstract void withdraw(double amount) throws InsufficientBalanceException;

    /*
    Transfer operation
    */

    @Override
    public void transfer(BankAccount receiver, double amount) {

        if (amount <= 0) {
            System.out.println(
                    "Transfer failed: Amount must be greater than 0");
            return;
        }

        try {

            withdraw(amount);

            receiver.deposit(amount);

            System.out.println(
                    "Transfer successful: Rs. "
                    + amount
                    + " transferred to "
                    + receiver.getAccountNumber());

        }
        catch (BankAccountException e) {

            System.out.println(
                    "Transfer failed: " + e.getMessage());
        }
    }

    /*
    Display account information
    */

    public void displayAccountDetails() {

        System.out.println("Account Number: "
                + accountNumber);

        System.out.println("Account Holder: "
                + accountHolder);

        System.out.println("Account Type: "
                + getClass().getSimpleName());

        System.out.println("Balance: Rs. "
                + balance);
    }
}