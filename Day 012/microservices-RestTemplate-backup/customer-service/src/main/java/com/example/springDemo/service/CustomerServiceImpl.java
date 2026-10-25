package com.example.springDemo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.springDemo.dto.CustomerRequest;
import com.example.springDemo.dto.CustomerResponse;
import com.example.springDemo.dto.LoanResponse;
import com.example.springDemo.dto.SavingsAccountResponse;
import com.example.springDemo.entity.Customer;
import com.example.springDemo.exception.AccountServiceUnavailableException;
import com.example.springDemo.exception.CustomerNotFoundException;
import com.example.springDemo.exception.DuplicateEmailException;
import com.example.springDemo.exception.UnderageCustomerException;
import com.example.springDemo.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final int MINIMUM_AGE = 18;
    private static final String ACCOUNT_SERVICE_URL = "http://account-service/api/v1/accounts";

    private final CustomerRepository repository;
    private final RestTemplate restTemplate;

    public CustomerServiceImpl(CustomerRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
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
        return callAccountService("/savings/" + customerId,
                new ParameterizedTypeReference<List<SavingsAccountResponse>>() {
                });
    }

    @Override
    public List<LoanResponse> getLoans(Long customerId) {
        checkCustomerExists(customerId);
        return callAccountService("/loans/" + customerId,
                new ParameterizedTypeReference<List<LoanResponse>>() {
                });
    }

    private <T> List<T> callAccountService(String path, ParameterizedTypeReference<List<T>> type) {
        try {
            return restTemplate.exchange(ACCOUNT_SERVICE_URL + path, HttpMethod.GET, null, type).getBody();
        } catch (RestClientException | IllegalStateException ex) {
            throw new AccountServiceUnavailableException(ex);
        }
    }

    private void checkCustomerExists(Long customerId) {
        if (!repository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
    }
}
