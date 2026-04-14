package br.edu.pe.senac.projeto_pi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursoRequestDTO {

    @NotBlank(message = "O nome do curso é obrigatório")
    private String nome;

    private String descricao;

    @NotNull(message = "A quantidade de horas complementares é obrigatória")
    @Min(value = 1, message = "As horas complementares devem ser maiores que zero")
    private Integer horasComplementares;
}
