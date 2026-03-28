package br.edu.pe.senac.projeto_pi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comprovacao_atividade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComprovacaoAtividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_aluno", nullable = false)
    private Users aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_atividade", nullable = false)
    private Atividade atividade;

    @Column(name = "arquivo_url", length = 500)
    private String arquivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusComprovacao status;

    @Column(name = "data_envio", nullable = false)
    private LocalDateTime dataEnvio;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @PrePersist
    protected void onCreate() {
        this.dataEnvio = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusComprovacao.PENDENTE;
        }
    }

    public enum StatusComprovacao {
        PENDENTE,
        APROVADO,
        REJEITADO
    }
}
