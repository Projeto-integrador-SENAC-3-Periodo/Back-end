package br.edu.pe.senac.projeto_pi.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "atividade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAtividade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_aluno", nullable = false)
    private Users aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_curso", nullable = false)
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "id_tipo_atividade")
    private TipoAtividade tipoAtividade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaFixa categoriaFixa;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "horas_solicitadas", nullable = false)
    private Integer horasSolicitadas;

    @Column(name = "horas_aprovadas")
    private Integer horasAprovadas;

    @Column(name = "comprovante_url")
    private String comprovanteUrl;

    @Column(name = "nome_arquivo")
    private String nomeArquivo;

    @Column(name = "tipo_arquivo")
    private String tipoArquivo;

    @Column(name = "tamanho_arquivo")
    private Long tamanhoArquivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAtividade status;

    @Column(nullable = false)
    private Integer pontos = 0;

    @Column(name = "motivo_reprovacao", columnDefinition = "TEXT")
    private String motivoReprovacao;

    @Column(name = "tentativas", nullable = false)
    private Integer tentativas = 0;

    @Column(name = "data_submissao", nullable = false)
    private LocalDateTime dataSubmissao;

    @Column(name = "data_validacao")
    private LocalDateTime dataValidacao;

    /**
     * Texto bruto extraído pelo Tesseract OCR do comprovante enviado pelo aluno.
     * Preenchido automaticamente no momento do upload — null se OCR estiver
     * desativado ou se a extração falhar.
     */
    @Column(name = "texto_ocr", columnDefinition = "TEXT")
    private String textoOcr;
    
    @Column(name = "horas_ocr")
    private Integer horasOcr;

    /**
     * Data detectada automaticamente pelo OCR (armazenada como String
     * para não perder informação de formatos variados).
     */
    @Column(name = "data_ocr", length = 50)
    private String dataOcr;

    @PrePersist
    protected void onCreate() {
        if (this.status == null)        this.status       = StatusAtividade.PENDENTE;
        if (this.dataSubmissao == null) this.dataSubmissao = LocalDateTime.now();
        if (this.tentativas == null)    this.tentativas   = 0;
        if (this.pontos == null)        this.pontos       = 0;
    }

    public enum StatusAtividade {
        PENDENTE, APROVADO, REPROVADO;

        @JsonValue
        public String toValue() { return this.name(); }

        @JsonCreator
        public static StatusAtividade fromString(String value) {
            if (value == null) return null;
            try { return StatusAtividade.valueOf(value.toUpperCase().trim()); }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                    "Status inválido: '" + value + "'. Aceitos: PENDENTE, APROVADO, REPROVADO");
            }
        }
    }

    public enum CategoriaFixa {
        ENSINO, PESQUISA, EXTENSAO;

        @JsonValue
        public String toValue() { return this.name(); }

        @JsonCreator
        public static CategoriaFixa fromString(String value) {
            if (value == null) return null;
            try { return CategoriaFixa.valueOf(value.toUpperCase().trim()); }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                    "Categoria inválida: '" + value + "'. Aceitos: ENSINO, PESQUISA, EXTENSAO");
            }
        }
    }
}
