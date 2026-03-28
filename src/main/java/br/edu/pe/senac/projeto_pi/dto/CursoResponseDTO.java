package br.edu.pe.senac.projeto_pi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private Integer cargaHorariaMinima;
}
