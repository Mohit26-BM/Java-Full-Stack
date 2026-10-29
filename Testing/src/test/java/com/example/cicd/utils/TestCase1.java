package com.example.cicd.utils;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class TestCase1 {

	@Test
	@Tag("smoke")
	void test1() {
		System.out.println("for smoke test1 related..!");
	}

	@Test
	@Tag("sanity")
	void test2() {
		System.out.println("for sanitry test1 related..!");
	}

	@Test
	@Tag("health")
	void test3() {
		System.out.println("for health test1 related..!");
	}

	@Test
	@Tag("smoke")
	void test4() {
		System.out.println("for smoke test2 related..!");
	}

	@Test
	@Tag("health")
	void test5() {
		System.out.println("for health test2 related..!");
	}
}
