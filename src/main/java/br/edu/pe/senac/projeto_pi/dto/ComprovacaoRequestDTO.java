package br.edu.pe.senac.projeto_pi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComprovacaoRequestDTO {
    private Long idAluno;
    private String arquivo;
}