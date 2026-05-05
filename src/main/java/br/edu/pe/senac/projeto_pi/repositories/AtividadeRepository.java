package br.edu.pe.senac.projeto_pi.repositories;

import br.edu.pe.senac.projeto_pi.entity.Atividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtividadeRepository extends JpaRepository<Atividade, Long> {

    List<Atividade> findByCursoIdC(Long cursoId);

    List<Atividade> findByAlunoId(Long alunoId);

    List<Atividade> findByAlunoIdAndStatus(Long alunoId, Atividade.StatusAtividade status);

    List<Atividade> findByStatus(Atividade.StatusAtividade status);

    /**
     * Soma das horas aprovadas de um aluno em um curso específico.
     * Considera apenas atividades com status APROVADO.
     */
    @Query("SELECT COALESCE(SUM(a.horasAprovadas), 0) FROM Atividade a " +
           "WHERE a.aluno.id = :alunoId AND a.curso.idC = :cursoId " +
           "AND a.status = 'APROVADO'")
    Integer somarHorasAprovadasPorAlunoECurso(@Param("alunoId") Long alunoId,
                                               @Param("cursoId") Long cursoId);

    /**
     * Total global de horas aprovadas de um aluno .
     */
    @Query("SELECT COALESCE(SUM(a.horasAprovadas), 0) FROM Atividade a " +
           "WHERE a.aluno.id = :alunoId AND a.status = 'APROVADO'")
    Integer somarHorasAprovadasPorAluno(@Param("alunoId") Long alunoId);

    /**
     * Todas as atividades pendentes de um curso.
     */
    @Query("SELECT a FROM Atividade a WHERE a.curso.idC = :cursoId AND a.status = 'PENDENTE'")
    List<Atividade> findPendentesByCurso(@Param("cursoId") Long cursoId);

    /**
     * Contagem de atividades por status de um aluno.
     */
    long countByAlunoIdAndStatus(Long alunoId, Atividade.StatusAtividade status);
}
