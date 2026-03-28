package br.edu.pe.senac.projeto_pi.dto; 

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtividadeRequestDTO {
    private Long idAluno;
    private Long idCurso;
    private Long idTipoAtividade;
    private String titulo;
    private String descricao;
    private Integer horasSolicitadas;
    private Integer pontos;
}
