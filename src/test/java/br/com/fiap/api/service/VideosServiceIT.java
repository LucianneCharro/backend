package br.com.fiap.api.service;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Videos;
import br.com.fiap.api.repository.VideoRepository;
import br.com.fiap.api.utils.VideoHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class VideosServiceIT {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideosService videoService;

    @Test
    void devePermitirRegistrarVideo() {
        var video = VideoHelper.gerarVideo();

        var videoArmazenada = videoService.criarVideo(video);

        assertThat(videoArmazenada)
                .isNotNull()
                .isInstanceOf(Videos.class);
        assertThat(videoArmazenada.getId())
                .isNotNull();
        assertThat(videoArmazenada.getTitulo())
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(video.getTitulo());
        assertThat(videoArmazenada.getDescricao())
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(video.getDescricao());
    }

    @Test
    void devePermitirBuscarVideo() {
        var video = VideoHelper.registrarVideo(videoRepository);

        var mensagemObtidaOptional = videoRepository.findById(video.getId());

        assertThat(mensagemObtidaOptional)
                .isPresent()
                .containsSame(video);
        mensagemObtidaOptional.ifPresent(mensagemObtida -> {
            assertThat(mensagemObtida.getId())
                    .isEqualTo(video.getId());
            assertThat(mensagemObtida.getTitulo())
                    .isEqualTo(video.getTitulo());
            assertThat(mensagemObtida.getDescricao())
                    .isEqualTo(video.getDescricao());
            assertThat(mensagemObtida.getUrl())
                    .isEqualTo(video.getUrl());
            assertThat(mensagemObtida.getDataCriacao())
                    .isEqualTo(video.getDataCriacao());
        });
    }

    @Test
    void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExistente() {
        var id = UUID.fromString("50537a52-1ab2-11ee-be56-0242ac120002");

        assertThatThrownBy(() -> videoService.buscarVideo(id))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("video não encontrado");
    }

    @Test
    void devePermitirAlterarMensagem() {
        var mensagemOriginal = VideoHelper.registrarVideo(videoRepository);
        var mensagemModificada = mensagemOriginal.toBuilder().build();
        mensagemModificada.setDescricao("abcd");

        var mensagemObtida = videoService.alterarVideo(mensagemOriginal.getId(),
                mensagemModificada);

        assertThat(mensagemObtida)
                .isInstanceOf(Videos.class)
                .isNotNull();
        assertThat(mensagemObtida.getId())
                .isEqualTo(mensagemModificada.getId());
        assertThat(mensagemObtida.getTitulo())
                .isEqualTo(mensagemModificada.getTitulo());
        assertThat(mensagemObtida.getDescricao())
                .isEqualTo(mensagemModificada.getDescricao());
        assertThat(mensagemObtida.getUrl())
                .isEqualTo(mensagemModificada.getUrl());
    }

    @Test
    void deveGerarExcecao_QuandoAlterarVideo_IdNaoCoincide() {
        var id = UUID.fromString("5f789b39-4295-42c1-a65b-cfca5b987db2");
        var mensagemNova = VideoHelper.gerarVideoCompleto();

        assertThatThrownBy(
                () -> videoService.alterarVideo(id, mensagemNova))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("video não apresenta o ID correto");
    }

    @Test
    void devePermitirApagarMensagem() {
        var mensagemRegistrada = VideoHelper.registrarVideo(videoRepository);
        var resultado = videoService.apagarVideo(mensagemRegistrada.getId());
        assertThat(resultado).isTrue();
    }

    @Test
    void devePermitirIncrementarGostei() {
        var mensagemRegistrada = VideoHelper.registrarVideo(videoRepository);

        var mensagemRecebida = videoService.incrementarGostei(mensagemRegistrada.getId());

        assertThat(mensagemRecebida.getGostei()).isEqualTo(1);
    }

    @Test
    void devePermitirListarMensagens() {
        Page<Videos> mensagens = videoService.listarVideo(Pageable.unpaged());

        assertThat(mensagens).hasSize(5);
        assertThat(mensagens.getContent())
                .asList()
                .allSatisfy(mensagem -> {
                    assertThat(mensagem).isNotNull();
                    assertThat(mensagem).isInstanceOf(Videos.class);
                });
    }

    @Test
    @Sql(scripts = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void devePermitirListarTodasAsMensagens_QuandoNaoExisteRegistro() {
        Page<Videos> mensagens = videoService.listarVideo(Pageable.unpaged());
        assertThat(mensagens).isEmpty();
    }

}