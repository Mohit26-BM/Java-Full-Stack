package com.example.cicd.utils;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class TestCase2 {

	@Test
	@Tag("smoke")
	void test1() {
		System.out.println("for smoke test3 related..!");
	}



	@Test
	@Tag("sanity")
	void test2() {
		System.out.println("for sanity test3 related..!");
	}



	@Test
	@Tag("smoke")
	void test3() {
		System.out.println("for smoke test3 related..!");
	}


	@Test
	@Tag("integration")
	void test4() {
		System.out.println("for Integration test1 related..!");
	}

	@Test
	@Tag("integration")
	void test5() {
		System.out.println("for Integaration test2 related..!");
	}


}
