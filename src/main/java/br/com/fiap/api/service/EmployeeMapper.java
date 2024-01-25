package br.com.fiap.api.service;

import br.com.fiap.api.dto.EmployeeDto;
import br.com.fiap.api.model.Employee;

import java.time.LocalDateTime;
import java.util.UUID;

public class EmployeeMapper {

    public static EmployeeDto mapToEmployeeDto(Employee employee){
        return new EmployeeDto(
                employee.getId()
        );
    }

    public static Employee mapToEmployee(EmployeeDto employeeDto){
        return new Employee(
                employeeDto.getId()
        );
    }
}