package com.company.wallet.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.company.wallet.exception.DuplicateCustomerException;
import com.company.wallet.model.Customer;

/*
DAO convention used in this project:
- methods WITH a Connection parameter run inside the caller's
  transaction and throw SQLException (the caller rolls back);
- methods WITHOUT one borrow their own pooled connection and
  wrap errors in DataAccessException.
*/
public interface CustomerDao {

    /*
    Returns the generated customer_id.
    */
    long insert(Connection connection, Customer customer)
            throws SQLException, DuplicateCustomerException;

    boolean exists(Connection connection, long customerId) throws SQLException;

    /*
    Return null when not found.
    */
    Customer findById(long customerId);

    Customer findByEmail(String email);

    List<Customer> findAll();
}
