package com.puzzle;

import com.puzzle.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
//@EntityScan(basePackages = {"com.puzzle.model"})
public class PuzzleApplication {

    public static void main(String[] args) {
        SpringApplication.run(PuzzleApplication.class, args);
    }
    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void testDatabaseConnection() {
        try {
            userRepository.count();
            System.out.println("Database connection test successful");
        } catch (Exception e) {
            System.err.println("Database connection test failed: " + e.getMessage());
        }
    }
}
