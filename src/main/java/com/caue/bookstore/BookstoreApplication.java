package com.caue.bookstore;

import com.caue.bookstore.entities.User;
import com.caue.bookstore.enums.UserRole;
import com.caue.bookstore.repositories.UserRepository;
import com.caue.bookstore.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.Date;

@SpringBootApplication
public class BookstoreApplication implements CommandLineRunner {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public BookstoreApplication(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        User user = userRepository.findUserByAuthorities("caue@email.com", "ADMIN");

        if (user == null){
            User adminUser = new User();
            adminUser.setName("Cauê");
            adminUser.setLastName("Ribeiro");
            adminUser.setPassword(passwordEncoder.encode("CaueAdmin!123"));
            adminUser.setEmail("caue@email.com");adminUser.setBirthdate(LocalDate.parse("1996-01-29"));
            adminUser.setRole(UserRole.ADMIN);

            userRepository.save(adminUser);
            System.out.println("✅ Admin user created!");
        }else {
            System.out.println("User already exists");
        }
    }

    @Configuration
    public class RestClientConfig {

        @Bean
        public RestClient.Builder restClientBuilder() {
            return RestClient.builder();
        }
    }

}
