package br.edu.pe.senac.projeto_pi.dto;

import br.edu.pe.senac.projeto_pi.entity.Atividade.CategoriaFixa;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoAtividadeRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Categoria é obrigatória")
    private CategoriaFixa categoriaFixa;

    @NotNull(message = "Carga horária máxima é obrigatória")
    @Min(value = 1, message = "Mínimo 1 hora")
    private Integer horasMaximas;

    private String requisito;
}
