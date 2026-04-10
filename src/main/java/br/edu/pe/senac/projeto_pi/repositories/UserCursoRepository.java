package br.edu.pe.senac.projeto_pi.repositories;

import br.edu.pe.senac.projeto_pi.entity.UserCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCursoRepository extends JpaRepository<UserCurso, Long> {
    List<UserCurso> findByCursoIdC(Long cursoId);
    boolean existsByUserIdAndCursoIdC(Long userId, Long cursoId);
    List<UserCurso> findByUserId(Long userId);
}
