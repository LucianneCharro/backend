
package br.com.fiap.api.controller;

import br.com.fiap.api.dto.WebfluxDto;
import br.com.fiap.api.service.WebfluxService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/webflux")
@AllArgsConstructor
public class WebFluxController {

    private WebfluxService webfluxService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Mono<WebfluxDto> saveWebflux(@RequestBody WebfluxDto webfluxDto){
        return webfluxService.saveWebflux(webfluxDto);
    }

    @GetMapping("{id}")
    public Mono<WebfluxDto> getWebflux(@PathVariable("id") String employeeId){
        return webfluxService.getWebflux(employeeId);
    }

    @GetMapping
    public Flux<WebfluxDto> getAllWebflux(){
        return webfluxService.getAllEmployees();
    }

    @PutMapping("{id}")
    public Mono<WebfluxDto> updateWebflux(@RequestBody WebfluxDto webfluxDto,
                                          @PathVariable("id") String id){
        return webfluxService.updateWebflux(webfluxDto, id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Mono<Void> deleteWebflux(@PathVariable("id") String employeeId){
        return webfluxService.deleteWebflux(employeeId);
    }
}