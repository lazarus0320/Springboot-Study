package com.example.ormjpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrmjpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrmjpaApplication.class, args);
	}
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");

	EntityManager em = emf.createEntityManager();
}
