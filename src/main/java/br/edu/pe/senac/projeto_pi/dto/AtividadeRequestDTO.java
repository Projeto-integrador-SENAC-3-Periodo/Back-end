package br.edu.pe.senac.projeto_pi.dto;

import br.edu.pe.senac.projeto_pi.entity.Atividade;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtividadeRequestDTO {

    /** Categoria fixa: ENSINO, PESQUISA ou EXTENSAO. */
    private Atividade.CategoriaFixa categoriaFixa;

    /** ID do TipoAtividade selecionado pelo aluno. */
    private Long idTipoAtividade;

    /** Descrição da atividade. */
    private String descricao;

    /** Quantidade de horas solicitadas pelo aluno. */
    private Integer horasSolicitadas;

    /**
     * ID do curso ao qual esta atividade pertence.
     */
    private Long idCurso;
}
