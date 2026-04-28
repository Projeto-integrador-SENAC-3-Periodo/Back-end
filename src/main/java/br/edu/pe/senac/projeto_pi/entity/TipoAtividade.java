package br.edu.pe.senac.projeto_pi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import br.edu.pe.senac.projeto_pi.entity.Atividade.CategoriaFixa;

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

    @Column(name = "horas_maximas")
    private Integer horasMaximas;

    @Column(columnDefinition = "TEXT")
    private String requisito;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_f", nullable = false, length = 20)
    private CategoriaFixa categoriaF;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        if (this.criadoEm == null) this.criadoEm = LocalDateTime.now();
        this.ativo = true;
    }
}
