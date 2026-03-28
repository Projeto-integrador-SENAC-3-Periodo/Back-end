package br.edu.pe.senac.projeto_pi.dto;

import br.edu.pe.senac.projeto_pi.entity.ComprovacaoAtividade;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvaliacaoRequestDTO {
    private ComprovacaoAtividade.StatusComprovacao status;
    private String observacao;
}
