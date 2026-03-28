package br.edu.pe.senac.projeto_pi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CertificadoResponseDTO {
    private Long id;
    private Long idAtividade;
    private String tituloAtividade;
    private Long idAluno;
    private String nomeAluno;
    private String arquivoUrl;
    private String tipoArquivo;
    private String assinaturaDigital;
    private LocalDateTime dataEmissao;
}