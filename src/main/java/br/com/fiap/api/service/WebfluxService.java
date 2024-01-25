package br.com.fiap.api.service;

import br.com.fiap.api.dto.WebfluxDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WebfluxService {
    Mono<WebfluxDto> saveWebflux(WebfluxDto webfluxDto);

    Mono<WebfluxDto> getWebflux(String employeeId);

    Flux<WebfluxDto> getAllEmployees();

    Mono<WebfluxDto> updateWebflux(WebfluxDto webfluxDto, String employeeId);

    Mono<Void> deleteWebflux(String employeeId);
}