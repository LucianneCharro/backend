package br.com.fiap.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import br.com.fiap.api.dto.VideoRequest;
import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.handler.GlobalExceptionHandler;
import br.com.fiap.api.model.Videos;
import br.com.fiap.api.service.VideosService;
import br.com.fiap.api.utils.VideoHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.fasterxml.jackson.databind.ObjectMapper;

class VideosControllerTest {

    private MockMvc mockMvc;

//  @RegisterExtension
//  static MockMvcListener mockMvcListener = new MockMvcListener();

    @RegisterExtension
    LogTrackerStub logTracker = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.INFO)
            .recordForType(VideosController.class);

    @Mock
    private VideosService videoService;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        VideosController videoController = new VideosController(videoService);
        mockMvc = MockMvcBuilders.standaloneSetup(videoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*")
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class CriarVideos {

        @Test
        void devePermitirCriarVideo() throws Exception {
            var videoRequest = VideoHelper.gerarVideoRequest();
            when(videoService.criarVideo(any(Videos.class)))
                    .thenAnswer(i -> i.getArgument(0));

            mockMvc.perform(post("/videos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(videoRequest)))
//                    .andDo(print())
                    .andExpect(status().isCreated());
            verify(videoService, times(1))
                    .criarVideo(any(Videos.class));
        }

        @Test
        void deveGerarExcecao_QuandoCriarVideo_TituloEmBraco() throws Exception {
            var videoRequest = VideoRequest.builder()
                    .titulo("")
                    .descricao("xpto")
                    .build();

            mockMvc.perform(post("/videos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(videoRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors.[0]").value("título não pode estar vazio"));
            verify(videoService, never())
                    .criarVideo(any(Videos.class));
        }

        @Test
        void deveGerarExcecao_QuandoCriarVideo_DescricaoEmBranco() throws Exception {
            var videoRequest = VideoRequest.builder()
                    .titulo("John")
                    .descricao("")
                    .build();

            mockMvc.perform(post("/videos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(videoRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors.[0]").value("descrição não pode estar vazio"));
            verify(videoService, never()).criarVideo(any(Videos.class));
        }

        @Test
        void deveGerarExcecao_QuandoCriarVideo_CamposInvalidos() throws Exception {
            var videoRequest = new ObjectMapper().readTree(
                    "{\"ping\": \"ping\", \"quack\": \"adalberto\"}");

            mockMvc.perform(post("/videos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(videoRequest)))
//      .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> {
                        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(json).contains("Validation error");
                        assertThat(json).contains("título não pode estar vazio");
                        assertThat(json).contains("descrição não pode estar vazio");
                    });
            verify(videoService, never())
                    .criarVideo(any(Videos.class));
        }

        @Test
        void deveGerarExcecao_QuandoCriarVideo_PayloadComXml() throws Exception {
            String xmlPayload = "<video><titulo>John</titulo><descricao>Descrição da mensagem</descricao></video>";

            mockMvc.perform(post("/videos")
                            .contentType(MediaType.APPLICATION_XML)
                            .content(xmlPayload))
//      .andDo(print())
                    .andExpect(status().isUnsupportedMediaType());
            verify(videoService, never()).criarVideo(any(Videos.class));
        }

        @Test
        void deveGerarMensagemDeLog_QuandoCriarVideo() throws Exception {
            var videoRequest = VideoHelper.gerarVideoRequest();
            when(videoService.criarVideo(any(Videos.class))).thenAnswer(i -> i.getArgument(0));

            mockMvc.perform(post("/videos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(videoRequest)))
                    .andExpect(status().isCreated());
            verify(videoService, times(1))
                    .criarVideo(any(Videos.class));
            assertThat(logTracker.size()).isEqualTo(1);
        }
    }

    @Nested
    class BuscarVideos {

        @Test
        void devePermitirBuscarVideo() throws Exception {
            var id = UUID.fromString("259bdc02-1ab5-11ee-be56-0242ac120002");
            var video = VideoHelper.gerarVideo();
            video.setId(id);
            video.setDataCriacao(LocalDateTime.now());

            when(videoService.buscarVideo(any(UUID.class))).thenReturn(video);

            mockMvc.perform(get("/videos/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(video.getId().toString()))
                    .andExpect(jsonPath("$.descricao").value(video.getDescricao()))
                    .andExpect(jsonPath("$.titulo").value(video.getTitulo()))
                    .andExpect(jsonPath("$.url").value(video.getUrl()))
                    .andExpect(jsonPath("$.dataPublicacao").value(video.getDataPublicacao()))
                    .andExpect(jsonPath("$.dataCriacao").exists())
                    .andExpect(jsonPath("$.gostei").exists());
            verify(videoService, times(1)).buscarVideo(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarVideo_IdNaoExistente()
                throws Exception {
            var id = UUID.fromString("259bdc02-1ab5-11ee-be56-0242ac120002");

            when(videoService.buscarVideo(any(UUID.class)))
                    .thenThrow(new MensagemNotFoundException("video não encontrado"));

            mockMvc.perform(get("/videos/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
//          .andDo(print())
                    .andExpect(status().isNotFound());
            verify(videoService, times(1))
                    .buscarVideo(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarVideo_IdInvalido()
                throws Exception {
            var id = "2";

            mockMvc.perform(get("/videos/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("ID inválido"));
            verify(videoService, never())
                    .buscarVideo(any(UUID.class));
        }

        @Test
        void deveGerarMensagemDeLog_QuandoBuscarVideo() throws Exception {
            var id = UUID.fromString("259bdc02-1ab5-11ee-be56-0242ac120002");
            var video = VideoHelper.gerarVideo();
            video.setId(id);
            video.setDataCriacao(LocalDateTime.now());

            when(videoService.buscarVideo(any(UUID.class))).thenReturn(video);

            mockMvc.perform(get("/videos/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            assertThat(logTracker.size()).isEqualTo(1);
            assertThat(logTracker.contains("requisição para buscar video foi efetuada"))
                    .isTrue();
        }
    }

    @Nested
    class AlterarVideos {

        @Test
        void devePermitirAlterarVideo() throws Exception {
            var id = UUID.fromString("259bdc02-1ab5-11ee-be56-0242ac120002");
            var video = VideoHelper.gerarVideo();
            video.setId(id);

            when(videoService.alterarVideo(any(UUID.class), any(Videos.class)))
                    .thenAnswer(i -> i.getArgument(1));

            mockMvc.perform(put("/videos/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(video)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(video.getId().toString()))
                    .andExpect(jsonPath("$.descricao").value(video.getDescricao()))
                    .andExpect(jsonPath("$.titulo").value(video.getTitulo()))
                    .andExpect(jsonPath("$.url").value(video.getUrl()))
                    .andExpect(jsonPath("$.dataCriacao").value(video.getDataCriacao()))
                    .andExpect(jsonPath("$.dataPublicacao").value(video.getDataPublicacao()))
                    .andExpect(jsonPath("$.gostei").value(video.getGostei()));
            verify(videoService, times(1))
                    .alterarVideo(any(UUID.class), any(Videos.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarVideo_IdNaoCoincide() throws Exception {
            var id = "259bdc02-1ab5-11ee-be56-0242ac120002";
            var videoRequest = VideoHelper.gerarVideo();

            when(videoService.alterarVideo(any(UUID.class), any(Videos.class)))
                    .thenThrow(new MensagemNotFoundException("video não apresenta o ID correto"));

            mockMvc.perform(put("/videos/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(videoRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("video não apresenta o ID correto"));
            verify(videoService, never()).apagarVideo(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarVideo_IdInvalido() throws Exception {
            var id = "2";
            var videoRequest = VideoHelper.gerarVideo();

            mockMvc.perform(put("/videos/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(videoRequest)))
//          .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("ID inválido"));
            verify(videoService, never())
                    .apagarVideo(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarVideo_PayloadComXml() throws Exception {
            var id = UUID.fromString("259bdc02-1ab5-11ee-be56-0242ac120002");
            String xmlPayload = "<video><titulo>John</titulo><descricao>Descrição da mensagem</descricao></video>";

            mockMvc.perform(put("/videos/{id}", id)
                            .contentType(MediaType.APPLICATION_XML)
                            .content(xmlPayload))
//          .andDo(print())
                    .andExpect(status().isUnsupportedMediaType());
            verify(videoService, never()).alterarVideo(any(UUID.class), any(Videos.class));
        }

    }

    @Nested
    class ApagarVideos {

        @Test
        void devePermitirApagarVideo() throws Exception {
            var id = UUID.fromString("259bdc02-1ab5-11ee-be56-0242ac120002");
            when(videoService.apagarVideo(any(UUID.class)))
                    .thenReturn(true);

            mockMvc.perform(delete("/videos/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(content().string("video removido"));
            verify(videoService, times(1))
                    .apagarVideo(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoIncrementarGostei_IdInvalido()
                throws Exception {
            var id = "2";

            mockMvc.perform(delete("/videos/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("ID inválido"));
            verify(videoService, never())
                    .apagarVideo(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoApagarVideo_IdNaoExistente()
                throws Exception {
            var id = UUID.randomUUID();

            when(videoService.apagarVideo(any(UUID.class)))
                    .thenThrow(new MensagemNotFoundException("video não encontrado"));

            mockMvc.perform(delete("/videos/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("video não encontrado"));
            verify(videoService, times(1))
                    .apagarVideo(any(UUID.class));
        }

        @Test
        void deveGerarMensagemDeLog_QuandoApagarVideo() throws Exception {
            var id = UUID.fromString("259bdc02-1ab5-11ee-be56-0242ac120002");
            when(videoService.apagarVideo(any(UUID.class))).thenReturn(true);

            mockMvc.perform(delete("/videos/{id}", id))
                    .andExpect(status().isOk());
            assertThat(logTracker.size()).isEqualTo(1);
            assertThat(logTracker.contains("requisição para apagar video foi efetuada"))
                    .isTrue();
        }

    }

    @Nested
    class IncrementarGostei {

        @Test
        void devePermitirIncrementarGostei() throws Exception {
            var video = VideoHelper.gerarVideoCompleto();
            video.setGostei(video.getGostei() + 1);
            var id = video.getId().toString();

            when(videoService.incrementarGostei(any(UUID.class))).thenReturn(video);

            mockMvc.perform(put("/videos/{id}/gostei", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(video.getId().toString()))
                    .andExpect(jsonPath("$.descricao").value(video.getDescricao()))
                    .andExpect(jsonPath("$.titulo").value(video.getTitulo()))
                    .andExpect(jsonPath("$.url").value(video.getUrl()))
                    .andExpect(jsonPath("$.dataCriacao").exists())
                    .andExpect(jsonPath("$.dataPublicacao").exists())
                    .andExpect(jsonPath("$.gostei").exists())
                    .andExpect(jsonPath("$.gostei").value(1));
            verify(videoService, times(1))
                    .incrementarGostei(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoIncrementarGostei_IdInvalido()
                throws Exception {
            var id = "2";

            mockMvc.perform(put("/videos/{id}/gostei", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("ID inválido"));
            verify(videoService, never()).incrementarGostei(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoIncrementarGostei_IdNaoExistente()
                throws Exception {
            var id = "9b0d8b5b-99a8-4635-b92f-d234bb4c2c5a";
            when(videoService.incrementarGostei(any(UUID.class)))
                    .thenThrow(new MensagemNotFoundException("video não encontrado"));

            mockMvc.perform(put("/videos/{id}/gostei", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("video não encontrado"));
            verify(videoService, times(1))
                    .incrementarGostei(any(UUID.class));
        }

        @Test
        void deveGerarMensagemDeLog_QuandoIncrementarGostei() throws Exception {
            var video = VideoHelper.gerarVideoCompleto();
            video.setGostei(video.getGostei() + 1);
            var id = video.getId().toString();

            when(videoService.incrementarGostei(any(UUID.class))).thenReturn(video);

            mockMvc.perform(put("/videos/{id}/gostei", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            assertThat(logTracker.size()).isEqualTo(1);
            assertThat(logTracker.contains("requisição para incrementar gostei foi efetuada"))
                    .isTrue();
        }
    }

    @Nested
    class ListarVideos {

        @Test
        void devePermitirListarVideos() throws Exception {
            var video = VideoHelper.gerarVideoCompleto();
            Page<Videos> page = new PageImpl<>(Collections.singletonList(
                    video
            ));
            when(videoService.listarVideo(any(Pageable.class)))
                    .thenReturn(page);
            mockMvc.perform(get("/videos")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(video.getId().toString()))
                    .andExpect(jsonPath("$.content[0].descricao").value(video.getDescricao()))
                    .andExpect(jsonPath("$.content[0].titulo").value(video.getTitulo()))
                    .andExpect(jsonPath("$.content[0].url").value(video.getUrl()))
                    .andExpect(jsonPath("$.content[0].dataPublicacao").exists())
                    .andExpect(jsonPath("$.content[0].dataCriacao").exists())
                    .andExpect(jsonPath("$.content[0].gostei").exists());
            verify(videoService, times(1))
                    .listarVideo(any(Pageable.class));
        }

        @Test
        void devePermitirListarVideos_QuandoNaoExisteRegistro()
                throws Exception {
            Page<Videos> page = new PageImpl<>(Collections.emptyList());
            when(videoService.listarVideo(any(Pageable.class)))
                    .thenReturn(page);
            mockMvc.perform(get("/videos")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", empty()))
                    .andExpect(jsonPath("$.content", hasSize(0)));
            verify(videoService, times(1))
                    .listarVideo(any(Pageable.class));
        }

        @Test
        void devePermitirListarVideos_QuandoReceberParametrosInvalidos()
                throws Exception {
            Page<Videos> page = new PageImpl<>(Collections.emptyList());
            when(videoService.listarVideo(any(Pageable.class)))
                    .thenReturn(page);
            mockMvc.perform(get("/videos?page=2&ping=pong")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", empty()))
                    .andExpect(jsonPath("$.content", hasSize(0)));
            verify(videoService, times(1)).listarVideo(any(Pageable.class));
        }

        @Test
        void deveGerarMensagemDeLog_QuandoListarVideos() throws Exception {
            var video = VideoHelper.gerarVideoCompleto();
            Page<Videos> page = new PageImpl<>(Collections.singletonList(
                    video
            ));
            when(videoService.listarVideo(any(Pageable.class)))
                    .thenReturn(page);
            mockMvc.perform(get("/videos")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            assertThat(logTracker.size()).isEqualTo(1);
            assertThat(logTracker.contains(
                    "requisição para listar videos foi efetuada: Página=0, Tamanho=10")).isTrue();

        }
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
