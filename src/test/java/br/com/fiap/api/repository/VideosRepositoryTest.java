package br.com.fiap.api.repository;

import br.com.fiap.api.model.Videos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VideosRepositoryTest {

    @Mock
    private VideoRepository videoRepository;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void devePermitirRegistrarMensagem() {
        // Arrange
        var mensagem = gerarMensagem();
        when(videoRepository.save(any(Videos.class))).thenReturn(mensagem);
        // Act
        var mensagemArmazenada = videoRepository.save(mensagem);
        // Assert
        verify(videoRepository, times(1)).save(mensagem);
        assertThat(mensagemArmazenada)
                .isInstanceOf(Videos.class)
                .isNotNull()
                .isEqualTo(mensagem);
        assertThat(mensagemArmazenada)
                .extracting(Videos::getId)
                .isEqualTo(mensagem.getId());
        assertThat(mensagemArmazenada)
                .extracting(Videos::getTitulo)
                .isEqualTo(mensagem.getTitulo());
        assertThat(mensagemArmazenada)
                .extracting(Videos::getDescricao)
                .isEqualTo(mensagem.getDescricao());
        assertThat(mensagemArmazenada)
                .extracting(Videos::getUrl)
                .isEqualTo(mensagem.getUrl());
        assertThat(mensagemArmazenada)
                .extracting(Videos::getDataCriacao)
                .isEqualTo(mensagem.getDataCriacao());
    }

    @Test
    void devePermitirConsultarMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = gerarMensagem();
        mensagem.setId(id);

        when(videoRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(mensagem));
        // Act
        var mensagemOptional = videoRepository.findById(id);
        // Assert
        verify(videoRepository, times(1)).findById(id);
        assertThat(mensagemOptional)
                .isPresent()
                .containsSame(mensagem);
        mensagemOptional.ifPresent(mensagemArmazenada -> {
            assertThat(mensagemArmazenada.getId())
                    .isEqualTo(mensagem.getId());
            assertThat(mensagemArmazenada.getTitulo())
                    .isEqualTo(mensagem.getTitulo());
            assertThat(mensagemArmazenada.getDescricao())
                    .isEqualTo(mensagem.getDescricao());
            assertThat(mensagemArmazenada.getUrl())
                    .isEqualTo(mensagem.getUrl());
            assertThat(mensagemArmazenada.getDataCriacao())
                    .isEqualTo(mensagem.getDataCriacao());
        });
    }

    @Test
    void devePermitirApagarMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        doNothing().when(videoRepository).deleteById(id);
        // Act
        videoRepository.deleteById(id);
        // Assert
        verify(videoRepository, times(1)).deleteById(id);
    }

    @Test
    void devePermitirListarMensagens() {
        // Arrange
        var mensagem1 = gerarMensagem();
        var mensagem2 = gerarMensagem();
        var mensagemList = Arrays.asList(mensagem1, mensagem2);

        when(videoRepository.findAll()).thenReturn(mensagemList);

        // Act
        var resultado = videoRepository.findAll();

        // Assert
        verify(videoRepository, times(1)).findAll();
        assertThat(resultado)
                .hasSize(2)
                .containsExactlyInAnyOrder(mensagem1, mensagem2);
    }

    private Videos gerarMensagem() {
        return Videos.builder().titulo("joe").descricao("xpto test").build();
    }

}
