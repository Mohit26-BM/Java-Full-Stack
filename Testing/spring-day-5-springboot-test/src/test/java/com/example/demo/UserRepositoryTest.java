package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@DataJpaTest
class UserRepositoryTest {
	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	void testFindAll() {
		
		User user1 = new User(100001L,"anil","anil@gmail.com");
		User user2 = new User(100002L,"sunil","sunil@gmail.com");
		User user3 = new User(100003L,"mahesh","mahesh@gmail.com");
		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.persist(user3);
		List<User> users = userRepository.findAll();
		assertEquals(users.size(),3);
		assertEquals(users.get(0).getUsername(),"anil");
		assertEquals(users.get(2).getEmail(),"mahesh@gmail.com");
		
	}

	@Test
	void testFindById() {
		User user1 = new User(100001L,"anil","anil@gmail.com");
		entityManager.persist(user1);
		Optional<User> user2 = Optional.of(new User(100001L,"anil","anil@gmail.com"));
		Optional<User> usr = userRepository.findById(100001L);
		assertEquals(user2.get().getId(),usr.get().getId());
	}

}
