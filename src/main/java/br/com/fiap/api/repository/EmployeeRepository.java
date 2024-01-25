package br.com.fiap.api.repository;

import br.com.fiap.api.model.Employee;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;


public interface EmployeeRepository extends ReactiveCrudRepository<Employee, String> {
}
