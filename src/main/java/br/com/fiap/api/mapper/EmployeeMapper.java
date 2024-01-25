package br.com.fiap.api.mapper;

import br.com.fiap.api.dto.WebfluxDto;
import br.com.fiap.api.entity.Webflux;

public class EmployeeMapper {

    public static WebfluxDto mapToEmployeeDto(Webflux webflux){
        return new WebfluxDto(
                webflux.getId(),
                webflux.getTitulo(),
                webflux.getUrl(),
                webflux.getDescricao(),
                webflux.getDataAlteracao(),
                webflux.getDataCriacao(),
                webflux.getDataPublicacao()
                );
    }

    public static Webflux mapToEmployee(WebfluxDto webfluxDto){
        return new Webflux(
                webfluxDto.getId(),
                webfluxDto.getTitulo(),
                webfluxDto.getUrl(),
                webfluxDto.getUrl(),
                webfluxDto.getDataAlteracao(),
                webfluxDto.getDataCriacao(),
                webfluxDto.getDataPublicacao()
        );
    }
}