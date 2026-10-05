package testbank.entity;

import java.util.Date;

public class Account {

    private int accountNumber;
    private int customerId;
    private AccountType accountType;
    private int balance;
    private Date dateOfCreation;

    public Account() {
        super();
    }

    public Account(int customerId, AccountType accountType,
                   int balance, Date dateOfCreation) {

        setCustomerId(customerId);
        setAccountType(accountType);
        setBalance(balance);
        setDateOfCreation(dateOfCreation);
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        if (accountNumber <= 0) {
            throw new IllegalArgumentException(
                    "Account number must be greater than 0."
            );
        }

        this.accountNumber = accountNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        if (customerId <= 0) {
            throw new IllegalArgumentException(
                    "Customer ID must be greater than 0."
            );
        }

        this.customerId = customerId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        if (accountType == null) {
            throw new IllegalArgumentException(
                    "Account type cannot be null."
            );
        }

        this.accountType = accountType;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        if (balance < 0) {
            throw new IllegalArgumentException(
                    "Account balance cannot be negative."
            );
        }

        this.balance = balance;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        if (dateOfCreation == null) {
            throw new IllegalArgumentException(
                    "Date of creation cannot be null."
            );
        }

        this.dateOfCreation = dateOfCreation;
    }

    @Override
    public String toString() {
        return "Account [accountNumber=" + accountNumber
                + ", customerId=" + customerId
                + ", accountType=" + accountType
                + ", balance=" + balance
                + ", dateOfCreation=" + dateOfCreation + "]";
    }
}