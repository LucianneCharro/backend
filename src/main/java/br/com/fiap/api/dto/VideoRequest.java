package br.com.fiap.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class VideoRequest {
  private String titulo;
  private String descricao;
  private String url;
}