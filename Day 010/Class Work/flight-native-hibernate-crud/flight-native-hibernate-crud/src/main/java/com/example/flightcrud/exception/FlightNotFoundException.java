package com.example.flightcrud.exception;

public class FlightNotFoundException extends RuntimeException {
	public FlightNotFoundException(String m) {
		super(m);
	}
}
