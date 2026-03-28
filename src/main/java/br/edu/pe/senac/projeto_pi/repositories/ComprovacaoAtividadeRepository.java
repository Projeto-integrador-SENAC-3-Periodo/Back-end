package br.edu.pe.senac.projeto_pi.repositories;

import br.edu.pe.senac.projeto_pi.entity.ComprovacaoAtividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComprovacaoAtividadeRepository extends JpaRepository<ComprovacaoAtividade, Long> {
    List<ComprovacaoAtividade> findByAlunoId(Long alunoId);
    List<ComprovacaoAtividade> findByAtividadeIdAtividade(Long atividadeId);
}