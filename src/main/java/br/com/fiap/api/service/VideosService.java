package br.com.fiap.api.service;

import br.com.fiap.api.model.Videos;

import java.time.LocalDateTime;
import java.util.Dictionary;
import java.util.List;
import java.util.UUID;

import org.springframework.core.convert.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * registrar video.
 */

public interface VideosService {

  Videos criarVideo(Videos video);

  Videos buscarVideo(UUID id);
  Videos alterarVideo(UUID id, Videos VideoNova);

  boolean apagarVideo(UUID id);
  Videos incrementarGostei(UUID id);

  Page<Videos> listarVideo(Pageable pageable);

  List<Videos> buscarVideoTitulo(String titulo, LocalDateTime dataPublicacao);
}
