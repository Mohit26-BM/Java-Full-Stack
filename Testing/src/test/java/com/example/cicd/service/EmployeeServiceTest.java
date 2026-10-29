package com.example.cicd.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.cicd.excpetion.EmployeeNotFoundException;
import com.example.cicd.model.Employee;

class EmployeeServiceTest {

	EmployeeService service;

	@BeforeEach
	public void createService() {
		service = new EmployeeService();
	}

	@AfterEach
	public void nullService() {
		service = null;
	}

	@Test
	void shouldReturnZeroEmployeeCountIfNoEmployees() {
		assertEquals(0, service.findEmployeesByDept("Finance").size());
	}


	@Test
	void shouldReturnAllEmployeeCount() {
		assertEquals(4, service.getAllEmployees().size());
	}

	@Test
	void shouldGetEmployeeByIdIfExists() {
		Employee e = service.findById(1);
		//Employee e = emp.get();
		assertAll(
				() -> assertEquals("Ravi",e.getName()),
				() -> assertEquals(6000,e.getSalary()),
				() -> assertEquals("IT",e.getDept())

		);
	}

	@Test
	void shouldThrowExceptionIfIdNotExists() {

		Exception e = assertThrows(
				EmployeeNotFoundException.class,

				() -> service.findById(9)

			);

	assertEquals("Employee with given id is not found", e.getMessage());


}

	@Test
	void shouldGetEmployeesByDept() {
		List<Employee> employees  = service.findEmployeesByDept("IT");
		assertEquals(1,employees.size());
	}

}
