package com.example.springDemo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springDemo.dto.CustomerRequest;
import com.example.springDemo.dto.CustomerResponse;
import com.example.springDemo.dto.LoanResponse;
import com.example.springDemo.dto.SavingsAccountResponse;
import com.example.springDemo.entity.Customer;
import com.example.springDemo.exception.AccountServiceUnavailableException;
import com.example.springDemo.exception.CustomerNotFoundException;
import com.example.springDemo.exception.DuplicateEmailException;
import com.example.springDemo.exception.UnderageCustomerException;
import com.example.springDemo.proxy.FeignProxy;
import com.example.springDemo.repository.CustomerRepository;

import feign.FeignException;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final int MINIMUM_AGE = 18;

    private final CustomerRepository repository;
    private final FeignProxy proxy;

    public CustomerServiceImpl(CustomerRepository repository, FeignProxy proxy) {
        this.repository = repository;
        this.proxy = proxy;
    }

    @Override
    @Transactional
    public CustomerResponse addCustomer(CustomerRequest request) {
        LocalDate latestAllowedBirthDate = LocalDate.now().minusYears(MINIMUM_AGE);
        if (request.getDateOfBirth().isAfter(latestAllowedBirthDate)) {
            throw new UnderageCustomerException(MINIMUM_AGE);
        }

        String email = request.getEmail().trim();
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException();
        }

        Customer customer = new Customer();
        customer.setName(request.getName().trim());
        customer.setEmail(email);
        customer.setMobile(request.getMobile());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setGender(request.getGender());
        customer.setCity(request.getCity().trim());

        return CustomerResponse.from(repository.save(customer));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long customerId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        return CustomerResponse.from(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return repository.findAll()
                .stream()
                .map(CustomerResponse::from)
                .toList();
    }

    @Override
    public List<SavingsAccountResponse> getSavings(Long customerId) {
        checkCustomerExists(customerId);
        return callAccountService(() -> proxy.getSavingsByCustomerId(customerId));
    }

    @Override
    public List<LoanResponse> getLoans(Long customerId) {
        checkCustomerExists(customerId);
        return callAccountService(() -> proxy.getLoansByCustomerId(customerId));
    }

    @Override
    public String getAccountServicePort() {
        return callAccountService(proxy::getPort);
    }

    private <T> T callAccountService(Supplier<T> call) {
        try {
            return call.get();
        } catch (FeignException ex) {
            throw new AccountServiceUnavailableException(ex);
        }
    }

    private void checkCustomerExists(Long customerId) {
        if (!repository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
    }
}
