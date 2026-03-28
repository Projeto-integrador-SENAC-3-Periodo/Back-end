package br.edu.pe.senac.projeto_pi.dto;

import br.edu.pe.senac.projeto_pi.entity.ComprovacaoAtividade;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ComprovacaoResponseDTO {
    private Long id;
    private Long idAluno;
    private String nomeAluno;
    private Long idAtividade;
    private String tituloAtividade;
    private String arquivo;
    private ComprovacaoAtividade.StatusComprovacao status;
    private LocalDateTime dataEnvio;
    private String observacao;
}
