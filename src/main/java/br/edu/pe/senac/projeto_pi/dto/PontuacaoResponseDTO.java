package br.edu.pe.senac.projeto_pi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PontuacaoResponseDTO {
    private Long idAluno;
    private String nomeAluno;
    private int totalPontos;
    private long atividadesConcluidas;
}
