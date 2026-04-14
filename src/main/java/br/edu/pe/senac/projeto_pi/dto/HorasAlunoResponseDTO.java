package br.edu.pe.senac.projeto_pi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resumo de horas complementares de um aluno.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorasAlunoResponseDTO {
    private Long idAluno;
    private String nomeAluno;
    private Integer horasAprovadas;
    private Integer horasLimite;
    private Integer horasRestantes;
    private Integer atividadesPendentes;
    private Integer atividadesAprovadas;
    private Integer atividadesReprovadas;
}
