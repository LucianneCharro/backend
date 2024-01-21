package br.com.fiap.api.service;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Videos;
import br.com.fiap.api.repository.VideoRepository;
import com.redis.S;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideosService {

    private final VideoRepository videoRepository;

    @Override
    public Videos criarVideo(Videos video) {
        video.setId(UUID.randomUUID());
        return videoRepository.save(video);
    }

    @Override
    public Videos buscarVideo(UUID id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new MensagemNotFoundException("video não encontrado"));
    }
    @Override
    public List<Videos> buscarVideoTitulo(String titulo) {
        return videoRepository.findByTitulo(titulo);
    }
    @Override
    public Videos alterarVideo(UUID id, Videos videosAtualizada) {
        var video = buscarVideo(id);
        if (!video.getId().equals(videosAtualizada.getId())) {
            throw new MensagemNotFoundException("video não apresenta o ID correto");
        }
        video.setDataAlteracao(LocalDateTime.now());
        video.setDescricao(videosAtualizada.getDescricao());
        video.setUrl(videosAtualizada.getUrl());
        return videoRepository.save(video);
    }

    @Override
    public boolean apagarVideo(UUID id) {
        var video = buscarVideo(id);
        videoRepository.delete(video);
        return true;
    }

    @Override
    public Videos incrementarGostei(UUID id) {
        var video = buscarVideo(id);
        video.setGostei(video.getGostei() + 1);
        return videoRepository.save(video);
    }

    @Override
    public Page<Videos> listarVideo(Pageable pageable) {

        return videoRepository.listarVideos(pageable);
    }

}
