package br.edu.pe.senac.projeto_pi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private Long id;
    private String matricula;
    private String token;
    private String tipo;
    private String perfil;
    private String nome;
    private String email;
    private Long expiracaoEm;
    private boolean senhaProvisoria;
}
