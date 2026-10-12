package com.example.springDemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springDemo.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
	
	boolean existsByEmailIgnoreCase(String email);

	

}
