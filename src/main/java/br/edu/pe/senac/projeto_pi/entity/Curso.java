package br.edu.pe.senac.projeto_pi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "curso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curso")
    private Long idC;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    /**
     * Teto de horas complementares do curso.
     * Quando o aluno atingir este valor (somando apenas atividades APROVADAS),
     * nenhuma nova aprovação adiciona horas a ele neste curso.
     */
    @Column(name = "horas_complementares", nullable = false)
    private Integer horasComplementares;

    @Column(nullable = false)
    private boolean ativo = true;

    @OneToMany(mappedBy = "curso")
    private List<UserCurso> usuariosCursos;

    @OneToMany(mappedBy = "curso")
    private List<Atividade> atividades;

    @OneToMany(mappedBy = "curso")
    private List<RegraCursoAtividade> regrasCursoAtividade;
}
