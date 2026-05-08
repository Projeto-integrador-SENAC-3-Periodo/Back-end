package br.edu.pe.senac.projeto_pi.repositories;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import br.edu.pe.senac.projeto_pi.entity.LogSistema;
import java.util.List;
 
@Repository
public interface LogSistemaRepository extends JpaRepository<LogSistema, Long> {
    List<LogSistema> findByUserId(Long userId);
}