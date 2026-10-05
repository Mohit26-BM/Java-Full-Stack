package testbank.dao;

import java.util.ArrayList;
import java.util.List;

import testbank.entity.Customer;

public class CustomerDaoImpl implements CustomerDao {

    private final List<Customer> customers;

    public CustomerDaoImpl() {
        customers = new ArrayList<>();
    }

    @Override
    public boolean addCustomer(Customer customer) {

        if (customer == null) {
            return false;
        }

        // Check whether customer ID already exists
        Customer existingCustomer = getCustomerById(customer.getCustomerId());

        if (existingCustomer != null) {
            return false;
        }

        customers.add(customer);
        return true;
    }

    @Override
    public Customer getCustomerById(int customerId) {

        for (Customer customer : customers) {

            if (customer.getCustomerId() == customerId) {
                return customer;
            }
        }

        return null;
    }

    @Override
    public List<Customer> getAllCustomers() {

        return new ArrayList<>(customers);
    }

    @Override
    public boolean updateCustomer(Customer customer) {

        if (customer == null) {
            return false;
        }

        Customer existingCustomer =
                getCustomerById(customer.getCustomerId());

        if (existingCustomer == null) {
            return false;
        }

        existingCustomer.setName(customer.getName());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setPhone(customer.getPhone());

        return true;
    }

    @Override
    public boolean deleteCustomer(int customerId) {

        Customer customer = getCustomerById(customerId);

        if (customer == null) {
            return false;
        }

        customers.remove(customer);
        return true;
    }
}