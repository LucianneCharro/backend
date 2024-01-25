package br.com.fiap.api.service;

import br.com.fiap.api.dto.WebfluxDto;
import br.com.fiap.api.mapper.EmployeeMapper;
import br.com.fiap.api.repository.EmployeeRepository;
import br.com.fiap.api.entity.Webflux;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements WebfluxService {

    private EmployeeRepository webfluxRepository;

    @Override
    public Mono<WebfluxDto> saveWebflux(WebfluxDto webfluxDto) {
        Webflux webflux = EmployeeMapper.mapToEmployee(webfluxDto);
        Mono<Webflux> savedEmployee = webfluxRepository.save(webflux);
        return savedEmployee
                .map((webfluxEntity) -> EmployeeMapper.mapToEmployeeDto(webfluxEntity));
    }

    @Override
    public Mono<WebfluxDto> getWebflux(String employeeId) {
        Mono<Webflux> employeeMono = webfluxRepository.findById(employeeId);
        return employeeMono.map((webflux -> EmployeeMapper.mapToEmployeeDto(webflux)));
    }

    @Override
    public Flux<WebfluxDto> getAllEmployees() {

        Flux<Webflux> employeeFlux  = webfluxRepository.findAll();
        return employeeFlux
                .map((webflux) -> EmployeeMapper.mapToEmployeeDto(webflux))
                .switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<WebfluxDto> updateWebflux(WebfluxDto webfluxDto, String employeeId) {

        Mono<Webflux> employeeMono = webfluxRepository.findById(employeeId);

        return employeeMono.flatMap((existingWebflux) -> {
            existingWebflux.setUrl(webfluxDto.getUrl());
            existingWebflux.setDescricao(webfluxDto.getDescricao());
            existingWebflux.setTitulo(webfluxDto.getTitulo());
            return webfluxRepository.save(existingWebflux);
        }).map((webflux -> EmployeeMapper.mapToEmployeeDto(webflux)));
    }

    @Override
    public Mono<Void> deleteWebflux(String id) {
        return webfluxRepository.deleteById(id);
    }
}