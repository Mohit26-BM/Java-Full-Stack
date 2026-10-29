package com.example.cicd.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MathematicsTest {

	Mathematics m;
	@BeforeEach
	void createMathemtics() {
		m = new Mathematics();
	}


	@AfterEach
	void nullObject() {
		m = null;
	}

	@Test
	void testBothNegatives() {
		//m = new Mathematics();
		assertEquals(-30,m.add(-10,-20));
	}

	@Test
	void testOneZero() {
		//m = new Mathematics();
		assertEquals(20,m.add(0,20));
	}

	@Test
	void testTwoPositives() {
		//m = new Mathematics();
		assertEquals(30,m.add(10,20));
	}


	@Test
	void testOnePositives() {
		//m = new Mathematics();
		assertEquals(10,m.add(-10,20));
	}

	@Test
	void testOneNegative() {
		//m = new Mathematics();
		assertEquals(-10,m.add(10,-20));
	}

	@Test
	void testBothZeros() {
		//m = new Mathematics();
		assertEquals(0,m.add(0,0));
	}
}
