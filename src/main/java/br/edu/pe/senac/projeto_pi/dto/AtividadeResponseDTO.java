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

    // Categoria fixa (ENSINO / PESQUISA / EXTENSAO)
    private Atividade.CategoriaFixa categoriaFixa;

    private String descricao;

    /** Horas informadas pelo aluno. */
    private Integer horasSolicitadas;

    /** Horas efetivamente aprovadas pelo coordenador. Null enquanto pendente. */
    private Integer horasAprovadas;

    /** URL do comprovante/certificado. */
    private String comprovanteUrl;

    private Atividade.StatusAtividade status;

    /** Feedback de reprovação (preenchido somente quando REPROVADO). */
    private String motivoReprovacao;

    /** Quantidade de reenvios após reprovação. */
    private Integer tentativas;

    private LocalDateTime dataSubmissao;
    private LocalDateTime dataValidacao;
}
