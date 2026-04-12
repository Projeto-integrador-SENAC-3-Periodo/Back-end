package br.edu.pe.senac.projeto_pi.controllers;

import br.edu.pe.senac.projeto_pi.dto.UsuarioCursoRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.UsuarioCursoResponseDTO;
import br.edu.pe.senac.projeto_pi.service.UserCursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cursos")
public class UserCursoController {

    @Autowired
    private UserCursoService userCursoService;

    @PostMapping("/{cursoId}/alunos")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<UsuarioCursoResponseDTO> vincularAluno(
            @PathVariable Long cursoId,
            @RequestBody UsuarioCursoRequestDTO dto) {
        return ResponseEntity.ok(userCursoService.vincularAluno(cursoId, dto));
    }

    @DeleteMapping("/{cursoId}/alunos/{alunoId}")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<?> desvincularAluno(
            @PathVariable Long cursoId,
            @PathVariable Long alunoId) {
        try {
            userCursoService.desvincularAluno(cursoId, alunoId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/aluno/{alunoId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioCursoResponseDTO>> listarCursosDoAluno(
            @PathVariable Long alunoId) {
        return ResponseEntity.ok(userCursoService.listarCursosDoAluno(alunoId));
    }

    @GetMapping("/{cursoId}/alunos")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioCursoResponseDTO>> listarAlunos(
            @PathVariable Long cursoId) {
        return ResponseEntity.ok(userCursoService.listarAlunosDoCurso(cursoId));
    }
}
