package br.edu.pe.senac.projeto_pi.repositories;

import br.edu.pe.senac.projeto_pi.entity.Certificados;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificados, Long> {
}
