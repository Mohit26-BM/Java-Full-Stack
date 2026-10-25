package com.company.spring.customer.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.company.spring.customer.model.Customer;

/*
@Repository = DATA ACCESS layer.

Semantically: "this class talks to the database". Technically it is
a @Component (so scanning registers it), and Spring can also apply
persistence-exception translation to @Repository beans.

The DataSource (a @Bean in AppConfig) is injected through the
constructor - the repository never builds its own connection settings.
*/
@Repository
public class CustomerRepository {

    private static final String SELECT_CUSTOMER =
            "SELECT customer_id, customer_name, email, city FROM a03_customers ";

    private final DataSource dataSource;

    public CustomerRepository(DataSource dataSource) {

        this.dataSource = dataSource;

        System.out.println("   [@Repository CustomerRepository] created, DataSource injected");
    }

    public List<Customer> findAll() {
        return query(SELECT_CUSTOMER + "ORDER BY customer_id", null);
    }

    public List<Customer> findByCity(String city) {
        return query(SELECT_CUSTOMER + "WHERE city = ? ORDER BY customer_name", city);
    }

    public Customer findById(int customerId) {

        List<Customer> result =
                query(SELECT_CUSTOMER + "WHERE customer_id = ?", customerId);

        return result.isEmpty() ? null : result.get(0);
    }

    private List<Customer> query(String sql, Object parameter) {

        List<Customer> customers = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (parameter != null) {
                statement.setObject(1, parameter);
            }

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    customers.add(new Customer(
                            resultSet.getInt("customer_id"),
                            resultSet.getString("customer_name"),
                            resultSet.getString("email"),
                            resultSet.getString("city")
                    ));
                }
            }

        } catch (SQLException e) {

            throw new IllegalStateException("Database error: " + e.getMessage()
                    + " (did you run schema.sql?)", e);
        }

        return customers;
    }
}
