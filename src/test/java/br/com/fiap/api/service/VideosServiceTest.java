package br.com.fiap.api.service;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Videos;
import br.com.fiap.api.repository.VideoRepository;
import br.com.fiap.api.utils.VideoHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class VideosServiceTest {

    private VideosService videoService;
    @Mock
    private VideoRepository videoRepository;
    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        videoService = new VideoServiceImpl(videoRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class RegistrarVideos {

        @Test
        void devePermitirRegistrarVideo() {
            var video = VideoHelper.gerarVideo();
            when(videoRepository.save(any(Videos.class)))
                    .thenAnswer(i -> i.getArgument(0));

            var mensagemArmazenada = videoService.criarVideo(video);

            assertThat(mensagemArmazenada)
                    .isInstanceOf(Videos.class)
                    .isNotNull();
            assertThat(mensagemArmazenada.getTitulo())
                    .isEqualTo(video.getTitulo());
            assertThat(mensagemArmazenada.getId())
                    .isNotNull();
            assertThat(mensagemArmazenada.getDescricao())
                    .isEqualTo(video.getDescricao());
            assertThat(mensagemArmazenada.getUrl())
                    .isEqualTo(video.getUrl());
            verify(videoRepository, times(1)).save(video);
        }
    }

    @Nested
    class BuscarVideos {

        @Test
        void devePermitirBuscarVideo() {
            var id = UUID.randomUUID();
            var video = VideoHelper.gerarVideo();
            when(videoRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(video));

            var mensagemObtida = videoService.buscarVideo(id);

            verify(videoRepository, times(1))
                    .findById(id);
            assertThat(mensagemObtida)
                    .isEqualTo(video);
            assertThat(mensagemObtida.getId())
                    .isEqualTo(video.getId());
            assertThat(mensagemObtida.getTitulo())
                    .isEqualTo(video.getTitulo());
            assertThat(mensagemObtida.getDescricao())
                    .isEqualTo(video.getDescricao());
            assertThat(mensagemObtida.getDataCriacao())
                    .isEqualTo(video.getDataCriacao());
        }

        @Test
        void deveGerarExcecao_QuandoBuscarVideo_IdNaoExistente() {
            var id = UUID.randomUUID();

            when(videoRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> videoService.buscarVideo(id))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("video não encontrado");
            verify(videoRepository, times(1)).findById(id);
        }
    }

    @Nested
    class AlterarVideos {

        @Test
        void devePermirirAlterarVideo() {
            var id = UUID.randomUUID();
            var mensagemAntiga = VideoHelper.gerarVideo();
            mensagemAntiga.setId(id);
            var mensagemNova = mensagemAntiga;
            mensagemNova.setDescricao("abcd");

            when(videoRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(mensagemAntiga));

            when(videoRepository.save(any(Videos.class)))
                    .thenAnswer(i -> i.getArgument(0));

            var mensagemObtida = videoService
                    .alterarVideo(id, mensagemNova);

            assertThat(mensagemObtida)
                    .isInstanceOf(Videos.class)
                    .isNotNull();
            assertThat(mensagemObtida.getId())
                    .isEqualTo(mensagemNova.getId());
            assertThat(mensagemObtida.getTitulo())
                    .isEqualTo(mensagemNova.getTitulo());
            assertThat(mensagemObtida.getDescricao())
                    .isEqualTo(mensagemNova.getDescricao());
            verify(videoRepository, times(1)).save(any(Videos.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarVideo_IdNaoCoincide() {
            var id = UUID.randomUUID();
            var mensagemAntiga = VideoHelper.gerarVideo();
            mensagemAntiga.setId(id);
            var mensagemNova = mensagemAntiga.toBuilder().build();
            mensagemNova.setId(UUID.randomUUID());

            assertThatThrownBy(
                    () -> videoService.alterarVideo(id, mensagemNova))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("video não encontrado");
            verify(videoRepository, never()).save(any(Videos.class));
        }

    }

    @Nested
    class RemoverVideos {

        @Test
        void devePermitirApagarVideo() {
            var id = UUID.fromString("51fa607a-1e61-11ee-be56-0242ac120002");
            var video = VideoHelper.gerarVideo();
            video.setId(id);
            when(videoRepository.findById(id))
                    .thenReturn(Optional.of(video));
            doNothing()
                    .when(videoRepository).deleteById(id);

            var resultado = videoService.apagarVideo(id);

            assertThat(resultado).isTrue();
            verify(videoRepository, times(1)).findById(any(UUID.class));
            verify(videoRepository, times(1)).delete(any(Videos.class));
        }

    }

    @Nested
    class IncrementarGostei {

        @Test
        void devePermitirIncrementarGostei() {
            var video = VideoHelper.gerarVideo();
            video.setId(UUID.randomUUID());

            when(videoRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(video));

            when(videoRepository.save(any(Videos.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            var videoRecebida = videoService.incrementarGostei(video.getId());

            verify(videoRepository, times(1)).save(videoRecebida);
            assertThat(videoRecebida.getGostei()).isEqualTo(1);
        }

    }

    @Nested
    class ListarVideos {

        @Test
        void devePermitirListarVideos() {
            Page<Videos> page = new PageImpl<>(Arrays.asList(
                    VideoHelper.gerarVideo(),
                    VideoHelper.gerarVideo()
            ));

            when(videoRepository.listarVideos(any(Pageable.class)))
                    .thenReturn(page);

            Page<Videos> videos = videoService.listarVideo(Pageable.unpaged());

            assertThat(videos).hasSize(2);
            assertThat(videos.getContent())
                    .asList()
                    .allSatisfy(video -> {
                        assertThat(video).isNotNull();
                        assertThat(video).isInstanceOf(Videos.class);
                    });
            verify(videoRepository, times(1)).listarVideos(any(Pageable.class));
        }

        @Test
        void devePermitirListarVideos_QuandoNaoExisteRegistro() {
            Page<Videos> page = new PageImpl<>(Collections.emptyList());

            when(videoRepository.listarVideos(any(Pageable.class)))
                    .thenReturn(page);

            Page<Videos> videos = videoService.listarVideo(Pageable.unpaged());

            assertThat(videos).isEmpty();
            verify(videoRepository, times(1)).listarVideos(any(Pageable.class));
        }
    }
}
