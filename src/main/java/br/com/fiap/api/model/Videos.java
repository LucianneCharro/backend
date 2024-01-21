package br.com.fiap.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class Videos {

    @Id
    @GenericGenerator(name = "uuid")
    private UUID id;

    @NotEmpty(message = "título não pode estar vazio")
    private String titulo;

    @NotEmpty(message = "descrição não pode estar vazio")
    private String descricao;

    @NotEmpty(message = "url não pode estar vazio")
    private String url;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSS")
    private LocalDateTime dataPublicacao;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSS")
    private LocalDateTime dataCriacao;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSS")
    private LocalDateTime dataAlteracao;

    @Builder.Default
    private int gostei = 0;

    @PrePersist
    public void prePersist() {
        var timestamp = LocalDateTime.now();
        dataPublicacao = timestamp;
        dataCriacao = timestamp;
        dataAlteracao = timestamp;
    }
}