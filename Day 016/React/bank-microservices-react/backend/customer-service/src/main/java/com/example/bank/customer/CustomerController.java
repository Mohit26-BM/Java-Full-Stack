package com.example.bank.customer;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Who may do what: tellers and admins work with any customer; a customer login can read only the record it is
 * linked to; nobody else gets anything (a token without a role is refused).
 */
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private static final int MINIMUM_AGE = 18;

    private final CustomerRepository customers;

    public CustomerController(CustomerRepository customers) {
        this.customers = customers;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<CustomerResponse> list(HttpServletRequest request) {
        if (!Caller.from(request).isStaff()) {
            throw forbidden("Only bank staff can list all customers");
        }
        return customers.findAll().stream().map(CustomerResponse::from).collect(Collectors.toList());
    }

    @GetMapping("/{customerId}")
    @Transactional(readOnly = true)
    public CustomerResponse get(@PathVariable Long customerId, HttpServletRequest request) {
        Caller caller = Caller.from(request);
        if (!caller.isStaff() && !caller.owns(customerId)) {
            throw forbidden("You can only view your own customer record");
        }
        return customers.findById(customerId).map(CustomerResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer " + customerId + " does not exist"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public CustomerResponse create(@Valid @RequestBody CustomerRequest request, HttpServletRequest http) {
        if (!Caller.from(http).isStaff()) {
            throw forbidden("Only bank staff can register customers");
        }
        if (request.dateOfBirth().isAfter(LocalDate.now().minusYears(MINIMUM_AGE))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Customer must be at least " + MINIMUM_AGE + " years old");
        }
        String email = request.email().trim();
        if (customers.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A customer with this email already exists");
        }
        Customer saved = customers.save(new Customer(request.name().trim(), email, request.dateOfBirth(),
                request.gender(), request.city().trim()));
        return CustomerResponse.from(saved);
    }

    private ResponseStatusException forbidden(String message) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }
}
