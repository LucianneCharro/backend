package br.com.fiap.api.service;

import br.com.fiap.api.dto.EmployeeDto;
import br.com.fiap.api.model.Employee;
import br.com.fiap.api.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;
    @Override
    public Flux<EmployeeDto> getAllEmployees() {

        Flux<Employee> employeeFlux  = employeeRepository.findAll();
        return employeeFlux
                .map((employee) -> EmployeeMapper.mapToEmployeeDto(employee))
                .switchIfEmpty(Flux.empty());
    }

}
