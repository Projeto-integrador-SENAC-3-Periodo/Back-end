package br.edu.pe.senac.projeto_pi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_user", nullable = false)
    private Users user;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    @Enumerated(EnumType.STRING)
    private TipoNotificacao tipo;

    @Enumerated(EnumType.STRING)
    private StatusNotificacao status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public enum TipoNotificacao {
        EMAIL,
        PUSH
    }

    public enum StatusNotificacao {
        ENVIADA,
        LIDA
    }
}