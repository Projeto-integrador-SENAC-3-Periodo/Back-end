package br.edu.pe.senac.projeto_pi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class Users{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = true, unique = true)
    private String matricula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private boolean senhaProvisoria = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    public Users() {
    }

    public Users(String nome, String email, Perfil perfil, String senha, String matricula) {
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
        this.senha = senha;
        this.matricula = matricula;
        this.senhaProvisoria = true;
    }
}
