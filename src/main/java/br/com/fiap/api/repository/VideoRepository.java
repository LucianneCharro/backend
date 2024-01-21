package br.com.fiap.api.repository;

import br.com.fiap.api.model.Videos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface VideoRepository extends JpaRepository<Videos, UUID> {

    @Query("SELECT m FROM Videos m")
    Page<Videos> listarVideos(Pageable pageable);

    List<Videos> findByTitulo(String titulo);
}
