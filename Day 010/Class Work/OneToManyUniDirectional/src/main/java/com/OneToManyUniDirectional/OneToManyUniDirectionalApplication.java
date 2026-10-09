
package com.OneToManyUniDirectional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import entity.Department;
import entity.Employee;
import repository.DepartmentRepo;

@SpringBootApplication
@EntityScan(basePackages = "entity")
@EnableJpaRepositories(basePackages = "repository")
public class OneToManyUniDirectionalApplication
        implements CommandLineRunner {

    @Autowired
    private DepartmentRepo departmentRepo;

    public static void main(String[] args) {
        SpringApplication.run(
            OneToManyUniDirectionalApplication.class, args);
    }

    @Override
    public void run(String... args) {

        Department department =
                new Department("Computer Science");

        department.addEmployee(
                new Employee("Aayush", 35000));

        department.addEmployee(
                new Employee("Rahul", 40000));

        department.addEmployee(
                new Employee("Priya", 45000));

        departmentRepo.save(department);

        System.out.println("Department and employees saved!");
    }
}
