package com.company.spring.customer.component;

import java.util.List;

import org.springframework.stereotype.Component;

import com.company.spring.customer.model.Customer;
import com.company.spring.customer.service.CustomerService;

/*
@Component = a GENERIC Spring-managed class that is not specifically
a repository, service or web controller - here, a report printer.

CustomerService is injected through the constructor.
Chain: CustomerComponent -> CustomerService -> CustomerRepository -> DataSource
*/
@Component
public class CustomerComponent {

    private final CustomerService customerService;

    public CustomerComponent(CustomerService customerService) {

        this.customerService = customerService;

        System.out.println("   [@Component  CustomerComponent]  created, CustomerService injected");
    }

    public void printCustomerReport() {

        List<Customer> customers = customerService.getAllCustomers();

        System.out.println("ALL CUSTOMERS (from table a03_customers)");
        printTable(customers);

        System.out.println("\nCUSTOMERS IN PUNE");
        printTable(customerService.getCustomersInCity("Pune"));

        System.out.println("\nCUSTOMER 2: " + customerService.getCustomer(2).getCustomerName());
        System.out.println("TOTAL CUSTOMERS: " + customers.size());
    }

    private void printTable(List<Customer> customers) {

        System.out.println("-----------------------------------------------------------------");
        System.out.println(String.format("%-4s %-15s %-28s %s", "ID", "Name", "Email", "City"));
        System.out.println("-----------------------------------------------------------------");

        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }
}
