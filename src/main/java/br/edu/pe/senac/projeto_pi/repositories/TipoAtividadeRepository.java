package br.edu.pe.senac.projeto_pi.repositories;

import br.edu.pe.senac.projeto_pi.entity.Atividade.CategoriaFixa;
import br.edu.pe.senac.projeto_pi.entity.TipoAtividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoAtividadeRepository extends JpaRepository<TipoAtividade, Long> {

    List<TipoAtividade> findByAtivoTrue();

    List<TipoAtividade> findByCategoriaFAndAtivoTrue(CategoriaFixa categoriaF);

    /** Verifica se alguma atividade referencia este tipo — bloqueia exclusão física */
    @Query("SELECT COUNT(a) > 0 FROM Atividade a WHERE a.tipoAtividade.idTA = :id")
    boolean existeAtividadeUsandoTipo(@Param("id") Long id);

    /** Soma horas aprovadas+pendentes do aluno neste tipo (para checar limite) */
    @Query("""
        SELECT COALESCE(SUM(a.horasSolicitadas), 0)
        FROM Atividade a
        WHERE a.aluno.id = :alunoId
          AND a.tipoAtividade.idTA = :tipoId
          AND a.status IN ('PENDENTE', 'APROVADO')
    """)
    int somarHorasAtivasPorAlunoETipo(@Param("alunoId") Long alunoId,
                                       @Param("tipoId") Long tipoId);
}
