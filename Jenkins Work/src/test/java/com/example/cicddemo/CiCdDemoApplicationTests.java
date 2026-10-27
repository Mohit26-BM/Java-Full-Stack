package com.example.cicddemo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CiCdDemoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void helloReturnsGreeting() {
		assertEquals("Hello, World!", new HelloController().hello());
	}

	@Test
	void greetUsesTheGivenName() {
		assertEquals("Hello, Mohit!", new HelloController().greet("Mohit"));
	}

}
