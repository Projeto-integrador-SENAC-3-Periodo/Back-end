package br.edu.pe.senac.projeto_pi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.pe.senac.projeto_pi.dto.CursoRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.CursoResponseDTO;
import br.edu.pe.senac.projeto_pi.service.CursoService;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR')")
    public ResponseEntity<CursoResponseDTO> create(@RequestBody CursoRequestDTO cursoRequestDTO) {
        return ResponseEntity.ok(cursoService.create(cursoRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>> listAll() {
        return ResponseEntity.ok(cursoService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR')")
    public ResponseEntity<CursoResponseDTO> update(@PathVariable Long id, @RequestBody CursoRequestDTO cursoRequestDTO) {
        return ResponseEntity.ok(cursoService.update(id, cursoRequestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cursoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
