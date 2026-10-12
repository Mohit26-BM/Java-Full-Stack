package com.company.wallet.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.company.wallet.exception.DataAccessException;
import com.company.wallet.exception.TransactionFailedException;
import com.company.wallet.exception.WalletException;

/*
Runs a unit of work as ONE database transaction.

Every multi-statement operation (create wallet, deposit, withdraw,
transfer, bulk credit, register) uses this, so the commit/rollback
rules are written once instead of in every service method:

    1. borrow a pooled connection, remember auto-commit, turn it OFF
    2. run the work (all statements use this same connection)
    3. success                    -> COMMIT
       business rule broken       -> ROLLBACK, rethrow the same exception
       SQL error / any bug        -> ROLLBACK, throw TransactionFailedException
    4. ALWAYS restore auto-commit and return the connection to the pool
       (try-with-resources), on success and on failure.
*/
public class TransactionManager {

    /*
    The work to run. It receives the transaction's connection
    and passes it to every DAO call.
    */
    @FunctionalInterface
    public interface Work<T> {
        T execute(Connection connection) throws SQLException, WalletException;
    }

    private final DataSource dataSource;

    public TransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T inTransaction(Work<T> work) throws WalletException {

        try (Connection connection = dataSource.getConnection()) {

            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {

                T result = work.execute(connection);

                connection.commit();

                return result;

            } catch (WalletException e) {

                rollbackQuietly(connection, e);
                throw e;

            } catch (SQLException | RuntimeException e) {

                rollbackQuietly(connection, e);

                throw new TransactionFailedException(
                        "Operation failed and was ROLLED BACK - nothing was saved. "
                        + "Reason: " + e.getMessage(),
                        e
                );

            } finally {

                restoreAutoCommit(connection, originalAutoCommit);
            }

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Could not get a database connection: " + e.getMessage(), e);
        }
    }

    private void rollbackQuietly(Connection connection, Exception cause) {

        try {
            connection.rollback();
        } catch (SQLException rollbackError) {
            cause.addSuppressed(rollbackError);
        }
    }

    private void restoreAutoCommit(Connection connection, boolean original) {

        try {
            connection.setAutoCommit(original);
        } catch (SQLException ignored) {
            // The pool discards connections it cannot reset.
        }
    }
}
