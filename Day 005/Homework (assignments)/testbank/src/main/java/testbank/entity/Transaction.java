package testbank.entity;

import java.util.Date;

public class Transaction {

    private int id;
    private int accountId;
    private Date transactionDate;
    private int amount;
    private TransactionType transactionType;
    private String description;

    public Transaction() {
    }

    public Transaction(int id, int accountId, Date transactionDate,
                       int amount, TransactionType transactionType,
                       String description) {

        setId(id);
        setAccountId(accountId);
        setTransactionDate(transactionDate);
        setAmount(amount);
        setTransactionType(transactionType);
        setDescription(description);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException(
                    "Transaction ID must be negative."
            );
        }
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        if (accountId <= 0) {
            throw new IllegalArgumentException(
                    "Account ID must be greater than 0."
            );
        }
        this.accountId = accountId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        if (transactionDate == null) {
            throw new IllegalArgumentException(
                    "Transaction date cannot be null."
            );
        }
        this.transactionDate = transactionDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Transaction amount must be greater than 0."
            );
        }
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        if (transactionType == null) {
            throw new IllegalArgumentException(
                    "Transaction type cannot be null."
            );
        }
        this.transactionType = transactionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Transaction description cannot be empty."
            );
        }
        this.description = description.trim();
    }

    @Override
    public String toString() {
        return "Transaction [id=" + id
                + ", accountId=" + accountId
                + ", transactionDate=" + transactionDate
                + ", amount=" + amount
                + ", transactionType=" + transactionType
                + ", description=" + description + "]";
    }
}