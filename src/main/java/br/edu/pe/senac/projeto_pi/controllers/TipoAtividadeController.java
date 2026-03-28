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

import br.edu.pe.senac.projeto_pi.dto.TipoAtividadeRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.TipoAtividadeResponseDTO;
import br.edu.pe.senac.projeto_pi.service.TipoAtividadeService;

@RestController
@RequestMapping("/tipos-atividade")
public class TipoAtividadeController {

    @Autowired
    private TipoAtividadeService tipoAtividadeService;

    @PostMapping
    @PreAuthorize("hasAnyRole(\'ADMINISTRADOR\', \'COORDENADOR\')")
    public ResponseEntity<TipoAtividadeResponseDTO> create(@RequestBody TipoAtividadeRequestDTO requestDTO) {
        return ResponseEntity.ok(tipoAtividadeService.create(requestDTO));
    }

    @GetMapping
    public ResponseEntity<List<TipoAtividadeResponseDTO>> listAll() {
        return ResponseEntity.ok(tipoAtividadeService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoAtividadeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tipoAtividadeService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole(\'ADMINISTRADOR\', \'COORDENADOR\')")
    public ResponseEntity<TipoAtividadeResponseDTO> update(@PathVariable Long id, @RequestBody TipoAtividadeRequestDTO requestDTO) {
        return ResponseEntity.ok(tipoAtividadeService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole(\'ADMINISTRADOR\', \'COORDENADOR\')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tipoAtividadeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
