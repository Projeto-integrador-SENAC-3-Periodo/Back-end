package br.edu.pe.senac.projeto_pi.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.pe.senac.projeto_pi.entity.Atividade;

import java.util.List;

@Repository
public interface AtividadeRepository extends JpaRepository<Atividade, Long> {
    List<Atividade> findByCursoIdC(Long cursoId);
    List<Atividade> findByAlunoIdAndStatus(Long alunoId, Atividade.StatusAtividade status);
}
