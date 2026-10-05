package testbank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import testbank.exception.CustomerNotFoundException;
import testbank.exception.DuplicateCustomerException;
import testbank.exception.InsufficientBalanceException;
import testbank.exception.InvalidAmountException;

import testbank.service.AccountService;
import testbank.service.AccountServiceImpl;
import testbank.service.CustomerService;
import testbank.service.CustomerServiceImpl;
import testbank.service.TransactionService;
import testbank.service.TransactionServiceImpl;


public class BankingApplicationTest {

    private CustomerService customerService;
    private AccountService accountService;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {

        CustomerDao customerDao = new CustomerDaoImpl();
        AccountDao accountDao = new AccountDaoImpl();
        TransactionDao transactionDao = new TransactionDaoImpl();

        customerService =
                new CustomerServiceImpl(
                        customerDao,
                        accountDao
                );

        transactionService =
                new TransactionServiceImpl(
                        transactionDao
                );

        accountService =
                new AccountServiceImpl(
                        accountDao,
                        customerDao,
                        transactionService
                );
    }


    // =========================================================
    // CUSTOMER TESTS
    // =========================================================

    @Test
    void testCreateCustomer() throws Exception {

        Customer customer =
                new Customer(
                        101,
                        "Mohit",
                        "mohit@gmail.com",
                        "9876543210"
                );

        customerService.addCustomer(customer);

        Customer result =
                customerService.getCustomerById(101);

        assertEquals(
                "Mohit",
                result.getName()
        );

        System.out.println(
                "[PASS] Customer creation"
        );
    }

    @Test
    void testDuplicateCustomer() throws Exception {

        Customer customer =
                new Customer(
                        101,
                        "Mohit",
                        "mohit@gmail.com",
                        "9876543210"
                );

        customerService.addCustomer(customer);

        assertThrows(
                DuplicateCustomerException.class,
                () -> customerService.addCustomer(customer)
        );

        System.out.println(
                "[PASS] Duplicate customer rejected"
        );
    }
    
    @Test
    void testCustomerNotFound() {

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomerById(999)
        );

        System.out.println(
                "[PASS] Non-existing customer rejected"
        );
    }


    // =========================================================
    // ACCOUNT TESTS
    // =========================================================

    @Test
    void testCreateAccount()
            throws Exception {

        Customer customer =
                new Customer(
                        101,
                        "Mohit",
                        "mohit@gmail.com",
                        "9876543210"
                );

        customerService.addCustomer(customer);

        int accountNumber =
                accountService.createAccount(
                        101,
                        AccountType.SAVINGS,
                        10000,
                        new Date()
                );

        Account account =
                accountService.getAllAccounts()
                        .get(0);

        assertTrue(accountNumber > 0);

        assertEquals(
                10000,
                account.getBalance()
        );

        assertEquals(
                101,
                account.getCustomerId()
        );

        System.out.println(
                "[PASS] Account creation"
        );
    }


    @Test
    void testAccountRequiresExistingCustomer() {

        assertThrows(
                CustomerNotFoundException.class,
                () -> accountService.createAccount(
                        999,
                        AccountType.SAVINGS,
                        10000,
                        new Date()
                )
        );

        System.out.println(
                "[PASS] Account creation rejected for invalid customer"
        );
    }


    // =========================================================
    // DEPOSIT TEST
    // =========================================================

    @Test
    void testDeposit() throws Exception {

        createTestAccount(10000);

        int accountNumber =
                getFirstAccountNumber();

        accountService.deposit(
                accountNumber,
                5000
        );

        Account account =
                getFirstAccount();

        assertEquals(
                15000,
                account.getBalance()
        );

        System.out.println(
                "[PASS] Deposit"
        );
    }


    // =========================================================
    // WITHDRAWAL TEST
    // =========================================================

    @Test
    void testWithdrawal() throws Exception {

        createTestAccount(10000);

        int accountNumber =
                getFirstAccountNumber();

        accountService.withdraw(
                accountNumber,
                3000
        );

        Account account =
                getFirstAccount();

        assertEquals(
                7000,
                account.getBalance()
        );

        System.out.println(
                "[PASS] Withdrawal"
        );
    }


    // =========================================================
    // INSUFFICIENT BALANCE TEST
    // =========================================================

    @Test
    void testInsufficientBalance()
            throws Exception {

        createTestAccount(5000);

        int accountNumber =
                getFirstAccountNumber();

        assertThrows(
                InsufficientBalanceException.class,
                () -> accountService.withdraw(
                        accountNumber,
                        10000
                )
        );

        System.out.println(
                "[PASS] Insufficient balance rejected"
        );
    }


    // =========================================================
    // TRANSFER TEST
    // =========================================================

    @Test
    void testFundTransfer()
            throws Exception {

        createTestCustomer(101);
        createTestCustomer(102);

        accountService.createAccount(
                101,
                AccountType.SAVINGS,
                10000,
                new Date()
        );

        accountService.createAccount(
                102,
                AccountType.SAVINGS,
                5000,
                new Date()
        );

        List<Account> accounts =
                accountService.getAllAccounts();

        int account1 =
                accounts.get(0).getAccountNumber();

        int account2 =
                accounts.get(1).getAccountNumber();

        accountService.fundsTransfer(
                account1,
                account2,
                3000
        );

        assertEquals(
                7000,
                accountService
                        .getAllAccounts()
                        .get(0)
                        .getBalance()
        );

        assertEquals(
                8000,
                accountService
                        .getAllAccounts()
                        .get(1)
                        .getBalance()
        );

        System.out.println(
                "[PASS] Fund transfer"
        );
    }


    // =========================================================
    // CLOSE ACCOUNT WITH BALANCE
    // =========================================================

    @Test
    void testCannotCloseAccountWithBalance()
            throws Exception {

        createTestAccount(10000);

        int accountNumber =
                getFirstAccountNumber();

        assertThrows(
                AccountHasBalanceException.class,
                () -> accountService.closeAccount(
                        accountNumber
                )
        );

        System.out.println(
                "[PASS] Account with balance cannot be closed"
        );
    }


    // =========================================================
    // CLOSE ZERO-BALANCE ACCOUNT
    // =========================================================

    @Test
    void testCloseZeroBalanceAccount()
            throws Exception {

        createTestAccount(0);

        int accountNumber =
                getFirstAccountNumber();

        accountService.closeAccount(
                accountNumber
        );

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAllAccounts()
                        .stream()
                        .filter(a ->
                                a.getAccountNumber()
                                == accountNumber)
                        .findFirst()
                        .orElseThrow(
                                () -> new AccountNotFoundException(
                                        "Account not found"
                                )
                        )
        );

        System.out.println(
                "[PASS] Zero-balance account can be closed"
        );
    }


    // =========================================================
    // TRANSACTION TEST
    // =========================================================

    @Test
    void testTransactionRecording()
            throws Exception {

        createTestAccount(10000);

        int accountNumber =
                getFirstAccountNumber();

        accountService.deposit(
                accountNumber,
                5000
        );

        List<Transaction> transactions =
                transactionService
                        .getTransactionsByAccountId(
                                accountNumber
                        );

        assertEquals(
                2,
                transactions.size()
        );

        assertEquals(
                10000,
                transactions.get(0).getAmount()
        );

        assertEquals(
                5000,
                transactions.get(1).getAmount()
        );

        System.out.println(
                "[PASS] Transaction recording"
        );
    }


    // =========================================================
    // HELPER METHODS
    // =========================================================

    private void createTestCustomer(int customerId)
            throws DuplicateCustomerException {

        Customer customer =
                new Customer(
                        customerId,
                        "Test Customer",
                        "test" + customerId + "@gmail.com",
                        "9876543210"
                );

        customerService.addCustomer(customer);
    }


    private void createTestAccount(int balance)
            throws Exception {

        createTestCustomer(101);

        accountService.createAccount(
                101,
                AccountType.SAVINGS,
                balance,
                new Date()
        );
    }


    private Account getFirstAccount() {

        return accountService
                .getAllAccounts()
                .get(0);
    }


    private int getFirstAccountNumber() {

        return getFirstAccount()
                .getAccountNumber();
    }
}