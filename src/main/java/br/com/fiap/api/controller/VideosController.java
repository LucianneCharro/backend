
package br.com.fiap.api.controller;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Videos;
import br.com.fiap.api.repository.VideoRepository;
import br.com.fiap.api.service.VideosService;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideosController {

    private final VideosService videosService;
    @Autowired
    private VideoRepository repository;
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Videos> criarVideo(@Valid @RequestBody Videos videos) {
        log.info("requisição para criar videos foi efetuada");
        var videoCriado = videosService.criarVideo(videos);
        return new ResponseEntity<>(videoCriado, HttpStatus.CREATED);
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buscarVideo(@PathVariable String id) {
        log.info("requisição para buscar video foi efetuada");
        try {
            var uuid = UUID.fromString(id);
            var videoEncontrada = videosService.buscarVideo(uuid);
            return new ResponseEntity<>(videoEncontrada, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("ID inválido");
        } catch (MensagemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/titulo/{titulo}")
        public ResponseEntity<?> buscarVideoTitulo(@PathVariable String titulo) {
        var videoEncontrada = videosService.buscarVideoTitulo(titulo);
        return new ResponseEntity<>(videoEncontrada, HttpStatus.OK);
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Videos>> listarVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataPublicacao,desc") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataPublicacao").descending());
        log.info("requisição para listar videos foi efetuada: Página={}, Tamanho={}, Sort={}", page, size, sort);
        Page<Videos> videos = videosService.listarVideo(pageable);
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> atualizarVideo(
            @PathVariable String id,
            @RequestBody @Valid Videos videos) {
        log.info("requisição para atualizar videos foi efetuada");
        try {
            var uuid = UUID.fromString(id);
            var videoAtualizado = videosService.alterarVideo(uuid, videos);
            return new ResponseEntity<>(videoAtualizado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("ID inválido");
        } catch (MensagemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/gostei")
    public ResponseEntity<?> incrementarGostei(@PathVariable String id) {
        log.info("requisição para incrementar gostei foi efetuada");
        try {
            var uuid = UUID.fromString(id);
            var videoAtualizado = videosService.incrementarGostei(uuid);
            return new ResponseEntity<>(videoAtualizado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("ID inválido");
        } catch (MensagemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> apagarVideo(@PathVariable String id) {
        log.info("requisição para apagar video foi efetuada");
        try {
            var uuid = UUID.fromString(id);
            videosService.apagarVideo(uuid);
            return new ResponseEntity<>("video removido", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("ID inválido");
        } catch (MensagemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
