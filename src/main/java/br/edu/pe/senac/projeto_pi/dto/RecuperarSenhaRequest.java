package br.edu.pe.senac.projeto_pi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecuperarSenhaRequest {

    @NotBlank(message = "Informe o email ou matrícula.")
    private String identificador;
}
