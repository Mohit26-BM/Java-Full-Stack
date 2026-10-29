package com.example.cicd.service;

import java.util.List;

import com.example.cicd.excpetion.EmployeeNotFoundException;
import com.example.cicd.model.Employee;

public class EmployeeService {

	public List<Employee> getAllEmployees(){
		return List.of(
		new Employee(1,"Ravi","IT", 6000),
		new Employee(2,"Priya","HR", 50000),
		new Employee(3,"Sunil","Operations", 78000),
		new Employee(4,"Arun","HR", 45000)

		);
	}

	public Employee findById(int id){

		return getAllEmployees()
				.stream()
				.filter(employee -> employee.getId() == id)
				.findAny()
				.orElseThrow(() -> new EmployeeNotFoundException());
	}

	public List<Employee> findEmployeesByDept(String dept){

		return getAllEmployees()
				.stream()
				.filter(employee -> employee.getDept().equals(dept))
				.toList();

	}

}
