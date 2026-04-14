package br.edu.pe.senac.projeto_pi.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.edu.pe.senac.projeto_pi.entity.Perfil;
import br.edu.pe.senac.projeto_pi.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
	boolean existsByEmail(String email);
	
	Optional<Users> findByEmailOrMatricula(String email, String matricula);
	boolean existsByMatricula(String matricula);
	Optional<Users> findByMatricula(String matricula);

	/**
	 * Pesquisar usuario por nome ou matricula.
	 */
	@Query("SELECT u FROM Users u WHERE " +
	       "(LOWER(u.nome) LIKE LOWER(CONCAT('%', :termo, '%')) " +
	       "OR LOWER(u.matricula) LIKE LOWER(CONCAT('%', :termo, '%')) " +
	       "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :termo, '%'))) " +
	       "AND (:perfil IS NULL OR u.perfil = :perfil)")
	List<Users> buscarPorTermo(@Param("termo") String termo, @Param("perfil") Perfil perfil);

	/**
	 * Listar usuario com um perfil especifico
	 */
	List<Users> findByPerfil(Perfil perfil);
}
