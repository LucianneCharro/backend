package br.com.fiap.api.service;

import br.com.fiap.api.dto.EmployeeDto;

import reactor.core.publisher.Flux;

public interface EmployeeService {

    Flux<EmployeeDto> getAllEmployees();

}
