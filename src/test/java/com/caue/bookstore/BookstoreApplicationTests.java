package com.caue.bookstore;

import com.caue.bookstore.dto.UserDTO;
import com.caue.bookstore.entities.User;
import com.caue.bookstore.utils.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(BookstoreApplicationTests.TestConfig.class)
class BookstoreApplicationTests {

	@Test
	void contextLoads() {
	}

	@TestConfiguration
	static class TestConfig {
		@Bean
		UserMapper userMapper() {
			return new UserMapper() {
				@Override
				public void updateUserFromDto(UserDTO source, User user) {
				}
			};
		}
	}

}
