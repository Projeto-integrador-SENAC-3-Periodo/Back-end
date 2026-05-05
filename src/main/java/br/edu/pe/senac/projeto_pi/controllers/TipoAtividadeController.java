package br.edu.pe.senac.projeto_pi.controllers;

import br.edu.pe.senac.projeto_pi.dto.TipoAtividadeRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.TipoAtividadeResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.Atividade.CategoriaFixa;
import br.edu.pe.senac.projeto_pi.service.TipoAtividadeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-atividade")
public class TipoAtividadeController {

    @Autowired
    private TipoAtividadeService tipoAtividadeService;

    @GetMapping
    public ResponseEntity<List<TipoAtividadeResponseDTO>> listAll() {
        return ResponseEntity.ok(tipoAtividadeService.listAll());
    }

    /** Coordenador/Admin vê todos (incluindo inativos) */
    @GetMapping("/todos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR')")
    public ResponseEntity<List<TipoAtividadeResponseDTO>> listAllAdmin() {
        return ResponseEntity.ok(tipoAtividadeService.listAllIncludingInactive());
    }

    /** Filtrar por categoria — para o formulário do aluno */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<TipoAtividadeResponseDTO>> listByCategoria(
            @PathVariable CategoriaFixa categoria) {
        return ResponseEntity.ok(tipoAtividadeService.listByCategoria(categoria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoAtividadeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tipoAtividadeService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TipoAtividadeResponseDTO> create(
            @RequestBody @Valid TipoAtividadeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tipoAtividadeService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR')")
    public ResponseEntity<TipoAtividadeResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid TipoAtividadeRequestDTO dto) {
        return ResponseEntity.ok(tipoAtividadeService.update(id, dto));
    }

    /**
     * DELETE: soft delete se em uso, físico se livre.
     * Coordenador/Admin pode remover.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tipoAtividadeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** Reativa um tipo desativado */
    @PatchMapping("/{id}/reativar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR')")
    public ResponseEntity<TipoAtividadeResponseDTO> reativar(@PathVariable Long id) {
        return ResponseEntity.ok(tipoAtividadeService.reativar(id));
    }
}
