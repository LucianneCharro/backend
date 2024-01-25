package br.com.fiap.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class WebfluxDto {
  @Id
  private String id;
  private String titulo;
  private String descricao;
  private String url;
  private LocalDateTime dataPublicacao;
  private LocalDateTime dataCriacao;
  private LocalDateTime dataAlteracao;
}