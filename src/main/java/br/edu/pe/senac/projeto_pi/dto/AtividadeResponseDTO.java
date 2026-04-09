package br.edu.pe.senac.projeto_pi.dto;


import java.time.LocalDateTime;

import br.edu.pe.senac.projeto_pi.entity.Atividade;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtividadeResponseDTO {
    private Long id;
    private Long idAluno;
    private String nomeAluno;
    private Long idCurso;
    private String nomeCurso;
    private Long idTipoAtividade;
    private String nomeTipoAtividade;
    private String titulo;
    private String descricao;
    private Integer horasSolicitadas;
    private Integer pontos;
    private String categoria;
    private String tipoComprovação;
    /** true quando é modelo de catálogo (sem aluno na entidade). */
    private Boolean catalogo;
    private Atividade.StatusAtividade status;
    private LocalDateTime dataSubmissao;
    private LocalDateTime dataValidacao;
}
