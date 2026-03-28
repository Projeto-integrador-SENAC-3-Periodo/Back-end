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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.edu.pe.senac.projeto_pi.dto.AtividadeRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.AtividadeResponseDTO;
import br.edu.pe.senac.projeto_pi.service.AtividadeService;
import br.edu.pe.senac.projeto_pi.service.FileStorageService;



@RestController
@RequestMapping("/atividades")
public class AtividadeController {

    @Autowired
    private AtividadeService atividadeService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<AtividadeResponseDTO> create(@RequestBody AtividadeRequestDTO atividadeRequestDTO) {
        return ResponseEntity.ok(atividadeService.create(atividadeRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<AtividadeResponseDTO>> listAll() {
        return ResponseEntity.ok(atividadeService.listAll());
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<AtividadeResponseDTO>> listByCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(atividadeService.listByCurso(cursoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtividadeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(atividadeService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<AtividadeResponseDTO> update(@PathVariable Long id, @RequestBody AtividadeRequestDTO atividadeRequestDTO) {
        return ResponseEntity.ok(atividadeService.update(id, atividadeRequestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        atividadeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/aprovar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR')")
    public ResponseEntity<AtividadeResponseDTO> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(atividadeService.aprovar(id));
    }

    @PostMapping("/{id}/reprovar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDENADOR')")
    public ResponseEntity<AtividadeResponseDTO> reprovar(@PathVariable Long id) {
        return ResponseEntity.ok(atividadeService.reprovar(id));
    }

    @PostMapping("/{id}/upload")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<String> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String fileUrl = fileStorageService.storeFile(file, id);
        return ResponseEntity.ok(fileUrl);
    }
}
