package br.edu.pe.senac.projeto_pi.dto;

import br.edu.pe.senac.projeto_pi.entity.Atividade;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvaliacaoRequestDTO {
    private Atividade.StatusAtividade status;
    private Integer horasAprovadas;
    private Atividade.CategoriaFixa categoriaFixa;
    private String TipoAtividade;
    private String motivoReprovacao;
}
