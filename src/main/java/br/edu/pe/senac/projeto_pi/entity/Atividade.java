package br.edu.pe.senac.projeto_pi.entity;

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

    /** Null = atividade de catálogo (criada por admin/coord.); o aluno envia comprovação depois. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_aluno", nullable = true)
    private Users aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_curso", nullable = false)
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_tipo_atividade", nullable = false)
    private TipoAtividade tipoAtividade;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "horas_solicitadas")
    private Integer horasSolicitadas;

    @Column(nullable = false)
    private Integer pontos = 0;

    /** Ex.: "Palestra", "Workshop" — livre para exibição. */
    @Column(length = 120)
    private String categoria;

    /** certificate | photo | document — alinhado ao front. */
    @Column(name = "tipo_comprovação", length = 20)
    private String tipoComprovação;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAtividade status;

    @Column(name = "data_submissao", nullable = false)
    private LocalDateTime dataSubmissao;

    @Column(name = "data_validacao")
    private LocalDateTime dataValidacao;

    @OneToMany(mappedBy = "atividade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certificados> certificados;

    public enum StatusAtividade {
        PENDENTE,
        APROVADO,
        REPROVADO
    }
}
