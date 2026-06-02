package br.edu.pe.senac.projeto_pi.dto;

import br.edu.pe.senac.projeto_pi.entity.Atividade;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AtividadeResponseDTO {

    private Long id;

    // Aluno
    private Long idAluno;
    private String nomeAluno;

    // Curso
    private Long idCurso;
    private String nomeCurso;

    // Tipo de atividade
    private Long idTipoAtividade;
    private String nomeTipoAtividade;

    // Categoria
    private Atividade.CategoriaFixa categoriaFixa;

    private String descricao;
    private Integer horasSolicitadas;
    private Integer horasAprovadas;
    private String comprovanteUrl;
    private Atividade.StatusAtividade status;
    private String motivoReprovacao;
    private Integer tentativas;
    private LocalDateTime dataSubmissao;
    private LocalDateTime dataValidacao;
    
    // Horas detectadas automaticamente no comprovante.
    private Integer horasOcr;

    // Data detectada no comprovante (formato original, ex: "15/03/2025").
    private String dataOcr;

    // true se o OCR processou o arquivo com sucesso.

    private boolean ocrProcessado;
   
}
