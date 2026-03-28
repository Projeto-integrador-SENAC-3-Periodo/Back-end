package br.edu.pe.senac.projeto_pi.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.pe.senac.projeto_pi.entity.CategoriaAtividade;

@Repository
public interface CategoriaAtividadeRepository extends JpaRepository<CategoriaAtividade, Long> {
}
