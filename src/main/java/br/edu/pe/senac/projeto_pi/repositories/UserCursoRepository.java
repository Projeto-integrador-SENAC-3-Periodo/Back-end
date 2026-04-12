package br.edu.pe.senac.projeto_pi.repositories;

import br.edu.pe.senac.projeto_pi.entity.UserCurso;
import br.edu.pe.senac.projeto_pi.entity.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCursoRepository extends JpaRepository<UserCurso, Long> {
    List<UserCurso> findByCursoIdC(Long cursoId);
    boolean existsByUserIdAndCursoIdC(Long userId, Long cursoId);
    List<UserCurso> findByUserId(Long userId);
    Optional<UserCurso> findByUserIdAndCursoIdC(Long userId, Long cursoId);
}
