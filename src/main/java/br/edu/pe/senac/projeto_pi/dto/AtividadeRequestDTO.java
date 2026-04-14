package br.edu.pe.senac.projeto_pi.dto;

import br.edu.pe.senac.projeto_pi.entity.Atividade;
import lombok.Getter;
import lombok.Setter;

/**
 * Payload enviado pelo ALUNO ao submeter uma atividade com comprovante.
 * O idAluno é resolvido a partir do JWT no serviço — não exposto como campo externo.
 */
@Getter
@Setter
public class AtividadeRequestDTO {


    /** Categoria fixa: ENSINO, PESQUISA ou EXTENSAO. */
    private Atividade.CategoriaFixa categoriaFixa;

    /** ID do tipo de atividade escolhido pelo aluno. */
    private Long idTipoAtividade;

    /** Título/nome da atividade (ex.: "Monitoria de Algoritmos"). */
    private String titulo;

    /** Descrição livre da atividade. */
    private String descricao;

    /** Quantidade de horas solicitadas pelo aluno. */
    private Integer horasSolicitadas;

    /**
     * ID do curso ao qual esta atividade pertence.
     * Se o aluno estiver em apenas um curso, pode ser inferido automaticamente.
     */
    private Long idCurso;
}
