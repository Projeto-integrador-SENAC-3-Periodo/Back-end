package br.edu.pe.senac.projeto_pi.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "tipo_atividade", nullable = false)
    private String tipoAtividade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaFixa categoriaFixa;

    private String titulo;
    
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


    @PrePersist
    protected void onCreate() {
        if (this.status == null)     this.status = StatusAtividade.PENDENTE;
        if (this.dataSubmissao == null) this.dataSubmissao = LocalDateTime.now();
        if (this.tentativas == null) this.tentativas = 0;
    }

    // ─── Enums ────────────────────────────────────────────────────

    public enum StatusAtividade {
        PENDENTE,
        APROVADO,
        REPROVADO;

        /** Garante que o Jackson serialize como string maiúscula. */
        @JsonValue
        public String toValue() { return this.name(); }

        /** Desserializa de forma tolerante (aceita maiúsculas e minúsculas). */
        @JsonCreator
        public static StatusAtividade fromString(String value) {
            if (value == null) return null;
            try {
                return StatusAtividade.valueOf(value.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                    "Status inválido: '" + value + "'. Valores aceitos: PENDENTE, APROVADO, REPROVADO");
            }
        }
    }

    public enum CategoriaFixa {
        ENSINO,
        PESQUISA,
        EXTENSAO;

        /** Garante que o Jackson serialize como string maiúscula. */
        @JsonValue
        public String toValue() { return this.name(); }

        /** Desserializa de forma tolerante (aceita maiúsculas e minúsculas). */
        @JsonCreator
        public static CategoriaFixa fromString(String value) {
            if (value == null) return null;
            try {
                return CategoriaFixa.valueOf(value.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                    "Categoria inválida: '" + value + "'. Valores aceitos: ENSINO, PESQUISA, EXTENSAO");
            }
        }
    }



}
