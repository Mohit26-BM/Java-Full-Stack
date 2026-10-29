package com.example.demo.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.model.User;


@DataJpaTest
class TestUserRepository {

	@Autowired
	private UserRepository userRepository;

	@Test
	void testSaveAndFindById() {
		userRepository.save(new User(200001L, "kiran", "kiran@gmail.com"));
		Optional<User> found = userRepository.findById(200001L);
		assertTrue(found.isPresent());
		assertEquals("kiran", found.get().getUsername());
	}

	@Test
	void testFindByIdWhenMissing() {
		assertTrue(userRepository.findById(999999L).isEmpty());
	}

}
