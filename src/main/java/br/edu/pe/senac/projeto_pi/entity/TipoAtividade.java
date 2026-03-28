package br.edu.pe.senac.projeto_pi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tipoatividade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoAtividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTA;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "horas_maximas")
    private Integer horasMaximas;

    @Column(columnDefinition = "TEXT")
    private String requisito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_categoria", nullable = false)
    private CategoriaAtividade categoria;

    @OneToMany(mappedBy = "tipoAtividade")
    private List<Atividade> atividades;

    @OneToMany(mappedBy = "tipoAtividade")
    private List<RegraCursoAtividade> regrasCursoAtividade;
}
