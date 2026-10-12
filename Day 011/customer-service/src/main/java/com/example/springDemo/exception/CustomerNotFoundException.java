package com.example.springDemo.exception;

public class CustomerNotFoundException extends RuntimeException{
	
	public CustomerNotFoundException(Long customerId) {
		super("Customer not found with Id : " + customerId);
	}

}
