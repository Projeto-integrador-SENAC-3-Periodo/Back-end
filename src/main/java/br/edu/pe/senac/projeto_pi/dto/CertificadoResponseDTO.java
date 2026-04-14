package br.edu.pe.senac.projeto_pi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CertificadoResponseDTO {

    private Long id;

    private Long idAluno;
    private String nomeAluno;

    private Long idCurso;
    private String nomeCurso;

    private String descricao;

    private LocalDateTime dataEmissao;
}