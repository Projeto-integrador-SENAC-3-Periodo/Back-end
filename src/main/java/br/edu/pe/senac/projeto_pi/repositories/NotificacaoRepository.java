package br.edu.pe.senac.projeto_pi.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.pe.senac.projeto_pi.entity.Notificacao;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUserIdOrderByCreatedAtDesc(Long userId);
}
