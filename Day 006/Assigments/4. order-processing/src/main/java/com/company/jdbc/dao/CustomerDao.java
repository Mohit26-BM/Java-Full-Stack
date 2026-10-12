package com.company.jdbc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.company.jdbc.exception.DuplicateCustomerException;
import com.company.jdbc.model.Customer;

public interface CustomerDao {

    /*
    Returns the generated customer_id.
    */
    long add(Customer customer)
            throws DuplicateCustomerException;

    /*
    Returns null when not found.
    */
    Customer findById(long customerId);

    /*
    Same, but inside the caller's transaction.
    */
    Customer findById(Connection connection, long customerId)
            throws SQLException;

    List<Customer> findAll();
}
