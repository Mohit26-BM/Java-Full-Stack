package com.example.cicd.excpetion;

public class EmployeeNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmployeeNotFoundException() {
		super("Employee with given id is not found");
	}

}
