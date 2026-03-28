package br.edu.pe.senac.projeto_pi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String tipo;
    private String perfil;
    private String nome;
    private String email;
    private Long expiracaoEm;
    private boolean senhaProvisoria;
    
    // Construtor sem senhaProvisoria para compatibilidade
    public LoginResponseDTO(String token, String tipo, String perfil, String nome, String email, Long expiracaoEm) {
        this.token = token;
        this.tipo = tipo;
        this.perfil = perfil;
        this.nome = nome;
        this.email = email;
        this.expiracaoEm = expiracaoEm;
        this.senhaProvisoria = false;
    }
    
    // Construtor sem expiracaoEm para compatibilidade
    public LoginResponseDTO(String token, String tipo, String perfil, String nome, String email) {
        this.token = token;
        this.tipo = tipo;
        this.perfil = perfil;
        this.nome = nome;
        this.email = email;
        this.senhaProvisoria = false;
    }
}
