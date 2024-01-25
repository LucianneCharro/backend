package br.com.fiap.api.repository;

import br.com.fiap.api.entity.Webflux;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;


public interface EmployeeRepository extends ReactiveCrudRepository<Webflux, String> {
}
