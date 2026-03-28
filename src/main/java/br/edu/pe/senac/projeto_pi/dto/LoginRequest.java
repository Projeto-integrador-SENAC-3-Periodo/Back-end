package br.edu.pe.senac.projeto_pi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
	@NotBlank(message = "Identificador é obrigatório (email ou matrícula)")
    private String identificador;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}