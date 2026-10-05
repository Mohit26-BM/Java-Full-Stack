package testbank.controller;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

import testbank.dao.AccountDao;
import testbank.dao.AccountDaoImpl;
import testbank.dao.CustomerDao;
import testbank.dao.CustomerDaoImpl;
import testbank.dao.TransactionDao;
import testbank.dao.TransactionDaoImpl;

import testbank.entity.Account;
import testbank.entity.AccountType;
import testbank.entity.Customer;
import testbank.entity.Transaction;
import testbank.exception.AccountHasBalanceException;
import testbank.exception.AccountNotFoundException;
import testbank.exception.CustomerHasAccountsException;
import testbank.exception.CustomerNotFoundException;
import testbank.exception.DuplicateAccountException;
import testbank.exception.DuplicateCustomerException;
import testbank.exception.InsufficientBalanceException;
import testbank.exception.InvalidAmountException;

import testbank.service.AccountService;
import testbank.service.AccountServiceImpl;
import testbank.service.CustomerService;
import testbank.service.CustomerServiceImpl;
import testbank.service.TransactionService;
import testbank.service.TransactionServiceImpl;

public class MainBank {

    private static final Scanner scanner =
            new Scanner(System.in);

    public static void main(String[] args) {

        // =====================================================
        // DAO OBJECTS
        // =====================================================

        CustomerDao customerDao =
                new CustomerDaoImpl();

        AccountDao accountDao =
                new AccountDaoImpl();

        TransactionDao transactionDao =
                new TransactionDaoImpl();


        // =====================================================
        // SERVICE OBJECTS
        // =====================================================

        CustomerService customerService =
                new CustomerServiceImpl(
                        customerDao,
                        accountDao
                );

        TransactionService transactionService =
                new TransactionServiceImpl(
                        transactionDao
                );

        AccountService accountService =
                new AccountServiceImpl(
                        accountDao,
                        customerDao,
                        transactionService
                );


        // =====================================================
        // MAIN APPLICATION LOOP
        // =====================================================

        while (true) {

            showMenu();

            int choice =
                    readPositiveInt("Enter your choice: ");

            switch (choice) {

                // =================================================
                // CUSTOMER OPERATIONS
                // =================================================

                case 1:
                    addCustomer(customerService);
                    break;

                case 2:
                    displayAllCustomers(customerService);
                    break;

                case 3:
                    searchCustomer(customerService);
                    break;

                case 4:
                    deleteCustomer(customerService);
                    break;


                // =================================================
                // ACCOUNT OPERATIONS
                // =================================================

                case 5:
                    createAccount(accountService);
                    break;

                case 6:
                    displayAllAccounts(accountService);
                    break;

                case 7:
                    depositMoney(accountService);
                    break;

                case 8:
                    withdrawMoney(accountService);
                    break;

                case 9:
                    transferFunds(accountService);
                    break;

                case 10:
                    closeAccount(accountService);
                    break;


                // =================================================
                // TRANSACTION OPERATIONS
                // =================================================

                case 11:
                    displayAllTransactions(transactionService);
                    break;

                case 12:
                    displayTransactionsByAccount(
                            transactionService
                    );
                    break;

                case 13:
                    displayTransactionsByType(
                            transactionService
                    );
                    break;

                case 14:
                    displayTransactionsByDateRange(
                            transactionService
                    );
                    break;


                // =================================================
                // EXIT
                // =================================================

                case 15:
                    System.out.println(
                            "\nThank you for using the Banking Management System."
                    );
                    return;


                default:
                    System.out.println(
                            "\nInvalid choice. "
                            + "Please select a number from 1 to 15."
                    );
            }
        }
    }


    // =========================================================
    // MENU
    // =========================================================

    private static void showMenu() {

        System.out.println(
                "\n========================================"
        );

        System.out.println(
                "       BANKING MANAGEMENT SYSTEM"
        );

        System.out.println(
                "========================================"
        );

        System.out.println("1.  Create Customer");
        System.out.println("2.  Display All Customers");
        System.out.println("3.  Search Customer");
        System.out.println("4.  Delete Customer");

        System.out.println("5.  Create Account");
        System.out.println("6.  Display All Accounts");
        System.out.println("7.  Deposit Money");
        System.out.println("8.  Withdraw Money");
        System.out.println("9.  Transfer Funds");
        System.out.println("10. Close Account");

        System.out.println(
                "11. Display All Transactions"
        );

        System.out.println(
                "12. Search Transactions by Account"
        );

        System.out.println(
                "13. Search Transactions by Type"
        );

        System.out.println(
                "14. Search Transactions by Date Range"
        );

        System.out.println("15. Exit");

        System.out.println(
                "========================================"
        );
    }


    // =========================================================
    // CREATE CUSTOMER
    // =========================================================

    private static void addCustomer(
            CustomerService customerService) {

        System.out.println(
                "\n========== Create Customer =========="
        );

        int customerId =
                readPositiveInt(
                        "Enter customer ID: "
                );

        String customerName =
                readName(
                        "Enter customer name: "
                );

        String customerEmail =
                readEmail(
                        "Enter customer email: "
                );

        String customerPhone =
                readPhone(
                        "Enter customer phone: "
                );

        try {

            Customer customer =
                    new Customer(
                            customerId,
                            customerName,
                            customerEmail,
                            customerPhone
                    );

            customerService.addCustomer(customer);

            System.out.println(
                    "\nCustomer added successfully."
            );

        } catch (DuplicateCustomerException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "\nValidation Error: "
                    + e.getMessage()
            );
        }
    }


    // =========================================================
    // DISPLAY ALL CUSTOMERS
    // =========================================================

    private static void displayAllCustomers(
            CustomerService customerService) {

        System.out.println(
                "\n========== Customer Details =========="
        );

        List<Customer> customers =
                customerService.getAllCustomers();

        if (customers.isEmpty()) {

            System.out.println(
                    "No customers are currently registered."
            );

            return;
        }

        for (Customer customer : customers) {

            System.out.println(customer);
        }
    }


    // =========================================================
    // SEARCH CUSTOMER
    // =========================================================

    private static void searchCustomer(
            CustomerService customerService) {

        System.out.println(
                "\n========== Search Customer =========="
        );

        int customerId =
                readPositiveInt(
                        "Enter customer ID: "
                );

        try {

            Customer customer =
                    customerService.getCustomerById(
                            customerId
                    );

            System.out.println(
                    "\nCustomer found successfully."
            );

            System.out.println(customer);

        } catch (CustomerNotFoundException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );
        }
    }


    // =========================================================
    // DELETE CUSTOMER
    // =========================================================

    private static void deleteCustomer(
            CustomerService customerService) {

        System.out.println(
                "\n========== Delete Customer =========="
        );

        int customerId =
                readPositiveInt(
                        "Enter customer ID: "
                );

        try {

            customerService.deleteCustomer(
                    customerId
            );

            System.out.println(
                    "Customer deleted successfully."
            );

        } catch (CustomerNotFoundException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );

        } catch (CustomerHasAccountsException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }
    }


    // =========================================================
    // CREATE ACCOUNT
    // =========================================================

    private static void createAccount(
            AccountService accountService) {

        System.out.println(
                "\n========== Create Account =========="
        );

        int customerId =
                readPositiveInt(
                        "Enter customer ID: "
                );

        AccountType accountType =
                readAccountType();

        int initialBalance =
                readNonNegativeInt(
                        "Enter initial balance: ₹"
                );

        Date dateOfCreation =
                new Date();

        try {

            int accountNumber =
                    accountService.createAccount(
                            customerId,
                            accountType,
                            initialBalance,
                            dateOfCreation
                    );

            System.out.println(
                    "\nAccount created successfully."
            );

            System.out.println(
                    "Account Number: "
                    + accountNumber
            );

            System.out.println(
                    "Customer ID: "
                    + customerId
            );

            System.out.println(
                    "Account Type: "
                    + accountType
            );

            System.out.println(
                    "Initial Balance: ₹"
                    + initialBalance
            );

            System.out.println(
                    "Date of Creation: "
                    + dateOfCreation
            );

        } catch (CustomerNotFoundException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );

        } catch (DuplicateAccountException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "\nValidation Error: "
                    + e.getMessage()
            );
        }
    }


    // =========================================================
    // READ ACCOUNT TYPE
    // =========================================================

    private static AccountType readAccountType() {

        while (true) {

            System.out.println(
                    "\nSelect Account Type:"
            );

            System.out.println(
                    "1. SAVINGS"
            );

            System.out.println(
                    "2. CURRENT"
            );

            int choice =
                    readPositiveInt(
                            "Enter account type: "
                    );

            switch (choice) {

                case 1:
                    return AccountType.SAVINGS;

                case 2:
                    return AccountType.CURRENT;

                default:

                    System.out.println(
                            "Invalid account type. "
                            + "Please select 1 or 2."
                    );
            }
        }
    }


    // =========================================================
    // DISPLAY ALL ACCOUNTS
    // =========================================================

    private static void displayAllAccounts(
            AccountService accountService) {

        System.out.println(
                "\n========== Account Details =========="
        );

        List<Account> accounts =
                accountService.getAllAccounts();

        if (accounts.isEmpty()) {

            System.out.println(
                    "No accounts are currently registered."
            );

            return;
        }

        for (Account account : accounts) {

            System.out.println(account);
        }
    }


    // =========================================================
    // DEPOSIT MONEY
    // =========================================================

    private static void depositMoney(
            AccountService accountService) {

        System.out.println(
                "\n========== Deposit Money =========="
        );

        int accountNumber =
                readPositiveInt(
                        "Enter account number: "
                );

        int amount =
                readPositiveInt(
                        "Enter deposit amount: ₹"
                );

        try {

            accountService.deposit(
                    accountNumber,
                    amount
            );

            System.out.println(
                    "\nDeposit successful."
            );

            System.out.println(
                    "Amount deposited: ₹"
                    + amount
            );

        } catch (AccountNotFoundException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );

        } catch (InvalidAmountException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "\nValidation Error: "
                    + e.getMessage()
            );
        }
    }


    // =========================================================
    // WITHDRAW MONEY
    // =========================================================

    private static void withdrawMoney(
            AccountService accountService) {

        System.out.println(
                "\n========== Withdraw Money =========="
        );

        int accountNumber =
                readPositiveInt(
                        "Enter account number: "
                );

        int amount =
                readPositiveInt(
                        "Enter withdrawal amount: ₹"
                );

        try {

            accountService.withdraw(
                    accountNumber,
                    amount
            );

            System.out.println(
                    "\nWithdrawal successful."
            );

            System.out.println(
                    "Amount withdrawn: ₹"
                    + amount
            );

        } catch (AccountNotFoundException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );

        } catch (InvalidAmountException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );

        } catch (InsufficientBalanceException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "\nValidation Error: "
                    + e.getMessage()
            );
        }
    }


    // =========================================================
    // TRANSFER FUNDS
    // =========================================================

    private static void transferFunds(
            AccountService accountService) {

        System.out.println(
                "\n========== Transfer Funds =========="
        );

        int fromAccountNumber =
                readPositiveInt(
                        "Enter source account number: "
                );

        int toAccountNumber =
                readPositiveInt(
                        "Enter destination account number: "
                );

        int amount =
                readPositiveInt(
                        "Enter transfer amount: ₹"
                );

        try {

            accountService.fundsTransfer(
                    fromAccountNumber,
                    toAccountNumber,
                    amount
            );

            System.out.println(
                    "\nFunds transferred successfully."
            );

            System.out.println(
                    "Amount transferred: ₹"
                    + amount
            );

        } catch (AccountNotFoundException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );

        } catch (InvalidAmountException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );

        } catch (InsufficientBalanceException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "\nValidation Error: "
                    + e.getMessage()
            );
        }
    }


    // =========================================================
    // CLOSE ACCOUNT
    // =========================================================

    private static void closeAccount(
            AccountService accountService) {

        System.out.println(
                "\n========== Close Account =========="
        );

        int accountNumber =
                readPositiveInt(
                        "Enter account number: "
                );

        try {

            accountService.closeAccount(accountNumber);

            System.out.println(
                    "\nAccount closed successfully."
            );

        } catch (AccountNotFoundException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );

        } catch (AccountHasBalanceException e) {

            System.out.println(
                    "\nError: " + e.getMessage()
            );
        
        } catch (IllegalArgumentException e) {

            System.out.println(
                    "\nValidation Error: "
                    + e.getMessage()
            );
        }
    }


    // =========================================================
    // DISPLAY ALL TRANSACTIONS
    // =========================================================

    private static void displayAllTransactions(
            TransactionService transactionService) {

        System.out.println(
                "\n========== All Transactions =========="
        );

        List<Transaction> transactions =
                transactionService.getAllTransactions();

        if (transactions.isEmpty()) {

            System.out.println(
                    "No transactions found."
            );

            return;
        }

        for (Transaction transaction : transactions) {

            System.out.println(transaction);
        }
    }


    // =========================================================
    // TRANSACTIONS BY ACCOUNT
    // =========================================================

    private static void displayTransactionsByAccount(
            TransactionService transactionService) {

        System.out.println(
                "\n========== Transactions by Account =========="
        );

        int accountId =
                readPositiveInt(
                        "Enter account number: "
                );

        try {

            List<Transaction> transactions =
                    transactionService
                            .getTransactionsByAccountId(
                                    accountId
                            );

            if (transactions.isEmpty()) {

                System.out.println(
                        "No transactions found for account "
                        + accountId
                        + "."
                );

                return;
            }

            for (Transaction transaction : transactions) {

                System.out.println(transaction);
            }

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Validation Error: "
                    + e.getMessage()
            );
        }
    }


    // =========================================================
    // TRANSACTIONS BY TYPE
    // =========================================================

    private static void displayTransactionsByType(
            TransactionService transactionService) {

        System.out.println(
                "\n========== Transactions by Type =========="
        );

        System.out.println("1. DEPOSIT");
        System.out.println("2. WITHDRAWAL");
        System.out.println("3. TRANSFER");

        int choice =
                readPositiveInt(
                        "Enter transaction type: "
                );

        String type;

        switch (choice) {

            case 1:
                type = "DEPOSIT";
                break;

            case 2:
                type = "WITHDRAWAL";
                break;

            case 3:
                type = "TRANSFER";
                break;

            default:

                System.out.println(
                        "Invalid transaction type."
                );

                return;
        }

        try {

            List<Transaction> transactions =
                    transactionService
                            .getTransactionsByType(type);

            if (transactions.isEmpty()) {

                System.out.println(
                        "No "
                        + type
                        + " transactions found."
                );

                return;
            }

            for (Transaction transaction : transactions) {

                System.out.println(transaction);
            }

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Validation Error: "
                    + e.getMessage()
            );
        }
    }


    // =========================================================
    // TRANSACTIONS BY DATE RANGE
    // =========================================================

    private static void displayTransactionsByDateRange(
            TransactionService transactionService) {

        System.out.println(
                "\n========== Transactions by Date Range =========="
        );

        System.out.print(
                "Enter start date (dd-MM-yyyy): "
        );

        String startDate =
                scanner.nextLine().trim();

        System.out.print(
                "Enter end date (dd-MM-yyyy): "
        );

        String endDate =
                scanner.nextLine().trim();

        try {

            List<Transaction> transactions =
                    transactionService
                            .getTransactionsByDateRange(
                                    startDate,
                                    endDate
                            );

            if (transactions.isEmpty()) {

                System.out.println(
                        "No transactions found "
                        + "for the given date range."
                );

                return;
            }

            for (Transaction transaction : transactions) {

                System.out.println(transaction);
            }

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Validation Error: "
                    + e.getMessage()
            );
        }
    }


    // =========================================================
    // READ POSITIVE INTEGER
    // =========================================================

    private static int readPositiveInt(
            String message) {

        while (true) {

            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

            try {

                int value =
                        Integer.parseInt(input);

                if (value <= 0) {

                    System.out.println(
                            "Value must be greater than 0. "
                            + "Please try again."
                    );

                    continue;
                }

                return value;

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid input. "
                        + "Please enter a valid number."
                );
            }
        }
    }


    // =========================================================
    // READ NON-NEGATIVE INTEGER
    // =========================================================

    private static int readNonNegativeInt(
            String message) {

        while (true) {

            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

            try {

                int value =
                        Integer.parseInt(input);

                if (value < 0) {

                    System.out.println(
                            "Value cannot be negative. "
                            + "Please try again."
                    );

                    continue;
                }

                return value;

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid input. "
                        + "Please enter a valid number."
                );
            }
        }
    }


    // =========================================================
    // READ NAME
    // =========================================================

    private static String readName(
            String message) {

        while (true) {

            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

            if (input.isEmpty()) {

                System.out.println(
                        "Name cannot be empty. "
                        + "Please try again."
                );

                continue;
            }

            if (!input.matches("[a-zA-Z ]+")) {

                System.out.println(
                        "Name can contain only "
                        + "alphabets and spaces."
                );

                continue;
            }

            return input;
        }
    }


    // =========================================================
    // READ EMAIL
    // =========================================================

    private static String readEmail(
            String message) {

        while (true) {

            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

            if (input.isEmpty()) {

                System.out.println(
                        "Email cannot be empty. "
                        + "Please try again."
                );

                continue;
            }

            if (!input.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                System.out.println(
                        "Please enter a valid email address."
                );

                continue;
            }

            return input;
        }
    }


    // =========================================================
    // READ PHONE
    // =========================================================

    private static String readPhone(
            String message) {

        while (true) {

            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

            if (!input.matches("\\d{10}")) {

                System.out.println(
                        "Phone number must contain "
                        + "exactly 10 digits."
                );

                continue;
            }

            return input;
        }
    }
}