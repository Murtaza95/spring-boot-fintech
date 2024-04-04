package com.fin.tech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fin.tech.model.Person;

public interface UserRepository extends JpaRepository<Person, Long> {
	Optional<Person> findByEmail(String email);
	List<Person> findAll();
}
