package br.edu.pe.senac.projeto_pi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import br.edu.pe.senac.projeto_pi.dto.CertificadoResponseDTO;

@Entity
@Table(name = "certificado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Certificados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCertificado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_aluno", nullable = false)
    private Users aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_curso", nullable = false)
    private Curso curso;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "data_emissao", nullable = false)
    private LocalDateTime dataEmissao;

    @PrePersist
    protected void onCreate() {
        this.dataEmissao = LocalDateTime.now();
    }

}