package br.com.fiap.api.utils;

import br.com.fiap.api.dto.VideoRequest;
import br.com.fiap.api.model.Videos;
import br.com.fiap.api.repository.VideoRepository;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class VideoHelper {

    public static VideoRequest gerarVideoRequest() {
        return VideoRequest.builder()
                .titulo("joe")
                .descricao("xpto test")
                .url("xpto")
                .build();
    }

    public static Videos gerarVideo() {
        return Videos.builder()
                .titulo("joe")
                .descricao("xpto test")
                .url("xpto")
                .build();
    }

    public static Videos gerarVideoCompleto() {
        var timestamp = LocalDateTime.now();
        return Videos.builder()
                .id(UUID.randomUUID())
                .titulo("joe")
                .descricao("xpto test")
                .url("xpto")
                .dataPublicacao(timestamp)
                .dataCriacao(timestamp)
                .dataAlteracao(timestamp)
                .build();
    }

    public static Videos registrarVideo(VideoRepository repository) {
        var video = gerarVideo();
        video.setId(UUID.randomUUID());
        return repository.save(video);
    }
}
