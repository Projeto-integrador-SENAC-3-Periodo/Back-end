package br.edu.pe.senac.projeto_pi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    @JoinColumn(name = "fk_id_atividade", nullable = false)
    private Atividade atividade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_aluno", nullable = false)
    private Users aluno;

    @Column(name = "arquivo_url", nullable = false, length = 500)
    private String arquivoUrl;

    @Column(name = "tipo_arquivo", length = 50)
    private String tipoArquivo;

    @Column(name = "texto_ocr", columnDefinition = "TEXT")
    private String textoOcr;

    @Column(name = "assinatura_digital", length = 500)
    private String assinaturaDigital;

    @Column(name = "data_emissao", nullable = false)
    private LocalDateTime dataEmissao;

    @PrePersist
    protected void onCreate() {
        this.dataEmissao = LocalDateTime.now();
    }
}
