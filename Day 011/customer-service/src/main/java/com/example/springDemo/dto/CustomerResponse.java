package com.example.springDemo.dto;

import java.time.LocalDate;

import com.example.springDemo.entity.Customer;
import com.example.springDemo.entity.Gender;

public record CustomerResponse(
	 Long customerId,
	 String name,
	 String email,
	 String mobile,
	 LocalDate dateOfBirth,
	 Gender gender,
	 String city) {
	
	
	public static CustomerResponse from(Customer customer) {
		return new CustomerResponse(
				customer.getCustomerId(),
				customer.getName(),
				customer.getEmail(),
				customer.getMobile(),
				customer.getDateOfBirth(),
				customer.getGender(),
				customer.getCity()
				
				
				);
	}
	
	 
	

}
