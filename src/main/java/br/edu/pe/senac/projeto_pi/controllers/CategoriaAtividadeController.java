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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.pe.senac.projeto_pi.dto.CategoriaAtividadeRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.CategoriaAtividadeResponseDTO;
import br.edu.pe.senac.projeto_pi.service.CategoriaAtividadeService;


@RestController
@RequestMapping("/categoriaAtiv")
public class CategoriaAtividadeController{

    @Autowired
    private CategoriaAtividadeService CatAtividadeService;

    @PostMapping
    @PreAuthorize("hasAnyRole(\'ADMINISTRADOR\', \'COORDENADOR\')")
    public ResponseEntity<CategoriaAtividadeResponseDTO> create(@RequestBody CategoriaAtividadeRequestDTO requestDTO) {
        return ResponseEntity.ok(CatAtividadeService.create(requestDTO));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaAtividadeResponseDTO>> listAll() {
        return ResponseEntity.ok(CatAtividadeService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaAtividadeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(CatAtividadeService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole(\'ADMINISTRADOR\', \'COORDENADOR\')")
    public ResponseEntity<CategoriaAtividadeResponseDTO> update(@PathVariable Long id, @RequestBody CategoriaAtividadeRequestDTO requestDTO) {
        return ResponseEntity.ok(CatAtividadeService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole(\'ADMINISTRADOR\', \'COORDENADOR\')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
    	CatAtividadeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
