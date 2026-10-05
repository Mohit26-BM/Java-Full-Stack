package testbank.dao;

import java.util.List;
import testbank.entity.Customer;

public interface CustomerDao {

    boolean addCustomer(Customer customer);

    Customer getCustomerById(int customerId);

    List<Customer> getAllCustomers();

    boolean updateCustomer(Customer customer);

    boolean deleteCustomer(int customerId);
}