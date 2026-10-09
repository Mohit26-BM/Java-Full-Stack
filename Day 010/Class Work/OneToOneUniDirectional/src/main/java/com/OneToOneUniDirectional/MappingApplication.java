package com.OneToOneUniDirectional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import entity.AadhaarCard;
import entity.StudentInformation;
import repository.StudentRepo;

@SpringBootApplication
@EntityScan(basePackages = "entity")
@EnableJpaRepositories(basePackages = "repository")
public class MappingApplication implements CommandLineRunner {

    @Autowired
    private StudentRepo studentRepo;

    public static void main(String[] args) {
        SpringApplication.run(MappingApplication.class, args);
    }

    @Override
    public void run(String... args) {
        AadhaarCard aadhaar = new AadhaarCard("1234-5678-9012");
        StudentInformation student = new StudentInformation("Aayush", aadhaar);
        studentRepo.save(student);
        System.out.println("Student saved successfully!");
    }
}
