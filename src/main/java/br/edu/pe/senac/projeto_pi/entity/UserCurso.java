package br.edu.pe.senac.projeto_pi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuariocurso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_user", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_curso", nullable = false)
    private Curso curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Papel papel;
    

    @Column(name = "data_matricula", nullable = false)
    private LocalDateTime dataMatricula;

    @PrePersist
    protected void onCreate() {
        this.dataMatricula = LocalDateTime.now();
    }

    public enum Papel {
        ALUNO,
        COORDENADOR
    }
}
