package com.example.demo.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


@Service
public class MyService {

	@PreAuthorize("hasAnyAuthority('admin','user')")
	//@PreAuthorize("#usename == authentication.principal.username")
	public String getLoanDetails(String username) {

		return "user loan details of "+username+" are returned";

	}

}
