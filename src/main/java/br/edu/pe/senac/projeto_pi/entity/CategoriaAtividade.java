package br.edu.pe.senac.projeto_pi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "categoriaatividade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaAtividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCatA;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @OneToMany(mappedBy = "categoria")
    private List<TipoAtividade> tiposAtividade;
}
