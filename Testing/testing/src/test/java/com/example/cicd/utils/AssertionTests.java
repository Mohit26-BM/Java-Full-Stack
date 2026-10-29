package com.example.cicd.utils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class AssertionTests {

	@Test
	void test() {
		String name = "Anil";
		int age = 20;
		String dept = "IT";
		Mathematics m = new Mathematics();

		assertAll(
				() -> assertEquals("Anil",name),
				() -> assertEquals(20,age),
				() -> assertEquals("IT",dept)

		);


	Exception e = assertThrows(
					ArithmeticException.class,

					() -> m.div(10, 0)

				);

		assertEquals("/ by zero", e.getMessage());


	}


	@RepeatedTest(3)
	public void repeatedTest() {
		System.out.println("Inside the repeated test");

	}

	@ParameterizedTest
	@CsvSource({
		"10,20,30",
		"-10,-20,-30",
		"-10,20,10",
		"10,-20,-10"
	})
	public void shouldAddTwoNumbers(int a , int b, int expected) {
		Mathematics c = new Mathematics();
		assertEquals(expected,c.add(a, b));

	}

	@ParameterizedTest
	@ValueSource(ints = {2,4,6,8,10})
	public void identifyEvenNumbers(int number) {
		assertEquals(0, number%2);
	}

}
