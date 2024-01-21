package br.com.fiap.api.repository;

import static br.com.fiap.api.utils.VideoHelper.registrarVideo;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import br.com.fiap.api.model.Videos;
import br.com.fiap.api.utils.VideoHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class VideosRepositoryIT {

    @Autowired
    private VideoRepository videoRepository;

    @Test
    void devePermitirCriarTabela() {
        long totalTabelasCriada = videoRepository.count();
        assertThat(totalTabelasCriada).isNotNegative();
    }

    @Test
    void devePermitirRegistrarVideo() {
        // Arrange
        var id = "f8faeba0-1f6b-46b7-bb68-3d8ebcc7f658";
        var video = VideoHelper.gerarVideo();
        video.setId(UUID.fromString(id));
        // Act
        var videoArmazenada = videoRepository.save(video);
        // Assert
        assertThat(videoArmazenada)
                .isInstanceOf(Videos.class)
                .isNotNull();
        assertThat(videoArmazenada.getId())
                .isEqualTo(video.getId());
        assertThat(videoArmazenada.getTitulo())
                .isEqualTo(video.getTitulo());
        assertThat(videoArmazenada.getDescricao())
                .isEqualTo(video.getDescricao());
        assertThat(videoArmazenada.getUrl())
                .isEqualTo(video.getUrl());
        assertThat(videoArmazenada.getDataPublicacao())
                .isNotNull();
        assertThat(videoArmazenada.getDataCriacao())
                .isNotNull();
        assertThat(videoArmazenada.getDataAlteracao())
                .isNotNull();
    }

    @Test
    void devePermitirConsultarVideo() {
        // Arrange
        var video = registrarVideo();
        var id = video.getId();
        // Act
        var videoOptional = videoRepository.findById(id);
        // Assert
        assertThat(videoOptional)
                .isPresent()
                .containsSame(video);
        videoOptional.ifPresent(videoArmazenada -> {
            assertThat(videoArmazenada.getId())
                    .isEqualTo(video.getId());
            assertThat(videoArmazenada.getTitulo())
                    .isEqualTo(video.getTitulo());
            assertThat(videoArmazenada.getDescricao())
                    .isEqualTo(video.getDescricao());
            assertThat(videoArmazenada.getUrl())
                    .isEqualTo(video.getUrl());
            assertThat(videoArmazenada.getDataPublicacao())
                    .isEqualTo(video.getDataPublicacao());
            assertThat(videoArmazenada.getDataCriacao())
                    .isEqualTo(video.getDataCriacao());
        });
    }

    @Test
    void devePermitirApagarVideo() {
        // Arrange
        var video = registrarVideo();
        var id = video.getId();
        // Act
        videoRepository.deleteById(id);
        var videoOptional = videoRepository.findById(id);
        // Assert
        assertThat(videoOptional)
                .isEmpty();
    }

    @Test
    void devePermitirListarVideo() {
        // Act
        var resultado = videoRepository.findAll();
        // Assert
        assertThat(resultado)
                .hasSize(5);
    }

    private Videos gerarVideo() {
        return Videos.builder()
                .titulo("joe")
                .descricao("xpto test")
                .build();
    }

    private Videos registrarVideo() {
        var video = gerarVideo();
        video.setId(UUID.randomUUID());
        return videoRepository.save(video);
    }

}
