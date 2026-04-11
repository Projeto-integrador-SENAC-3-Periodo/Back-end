package br.edu.pe.senac.projeto_pi.controllers;

import br.edu.pe.senac.projeto_pi.dto.CertificadoResponseDTO;
import br.edu.pe.senac.projeto_pi.service.CertificadoService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/certificados")
public class CertificadoController {

    @Autowired
    private CertificadoService certificadoService;

    @GetMapping("/{id}")
    public ResponseEntity<CertificadoResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(certificadoService.getById(id));
    }
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<CertificadoResponseDTO>> listByAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(certificadoService.listByAluno(alunoId));
    }
}
