package com.example.springDemo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.springDemo.dto.CustomerRequest;
import com.example.springDemo.dto.CustomerResponse;
import com.example.springDemo.entity.Customer;
import com.example.springDemo.exception.CustomerNotFoundException;
import com.example.springDemo.repository.CustomerRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {
	
	
	private final CustomerRepository repository;
	
	public CustomerServiceImpl(CustomerRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional
	public CustomerResponse addCustomer(CustomerRequest request) {
		if(request.getDateOfBirth() == null || !request.getDateOfBirth().isBefore(LocalDate.now().minusYears(18))) {
			throw new  IllegalArgumentException("Customer must be at least 18 years old");
		}
		
		if(repository.existsByEmailIgnoreCase(request.getEmail().trim())) {
			throw new  IllegalArgumentException("Email address already exists");
		}
		
		Customer customer = new Customer();
		customer.setName(request.getName().trim());
		customer.setEmail(request.getEmail().trim());
		customer.setMobile(request.getMobile());
		customer.setDateOfBirth(request.getDateOfBirth());
		customer.setGender(request.getGender());
		customer.setCity(request.getCity());
		
		Customer saved = repository.save(customer);
		
		return CustomerResponse.from(saved);
	}
	
	@Override
	@Transactional(readOnly = true)
	public CustomerResponse getCustomer(Long customerId) {
		
		Customer customer = repository.findById(customerId)
				            .orElseThrow(() -> 
				                   new CustomerNotFoundException(customerId));
		return CustomerResponse.from(customer);
				            		
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CustomerResponse> getAllCustomers(){
		return repository.findAll()
			             .stream()
			             .map(CustomerResponse::from)
			             .toList();
	}
	

}
