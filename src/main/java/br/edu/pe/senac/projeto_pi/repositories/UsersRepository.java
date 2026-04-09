package br.edu.pe.senac.projeto_pi.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.pe.senac.projeto_pi.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
	boolean existsByEmail(String email);
	
	Optional<Users> findByEmailOrMatricula(String email, String matricula);
	boolean existsByMatricula(String matricula);
	Optional<Users> findByMatricula(String matricula);
   
	
}
