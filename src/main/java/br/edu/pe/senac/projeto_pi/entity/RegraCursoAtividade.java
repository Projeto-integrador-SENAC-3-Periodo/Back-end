package br.edu.pe.senac.projeto_pi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "regracursoatividade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegraCursoAtividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRCA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_curso", nullable = false)
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_atividade_id", nullable = false)
    private TipoAtividade tipoAtividade;

    @Column(name = "limite_horas")
    private Integer limiteHoras;
}
