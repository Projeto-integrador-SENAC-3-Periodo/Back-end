package br.edu.pe.senac.projeto_pi.controllers;

import br.edu.pe.senac.projeto_pi.dto.AvaliacaoRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.ComprovacaoResponseDTO;
import br.edu.pe.senac.projeto_pi.service.ComprovacaoAtividadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ComprovacaoAtividadeController {

    @Autowired
    private ComprovacaoAtividadeService comprovacaoService;

    @PostMapping("/atividades/{atividadeId}/comprovacao")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<ComprovacaoResponseDTO> enviarComprovante(
            @PathVariable Long atividadeId,
            @RequestParam Long idAluno,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(comprovacaoService.enviarComprovante(atividadeId, idAluno, file));
    }

    @PutMapping("/comprovacoes/{id}/avaliar")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<ComprovacaoResponseDTO> avaliar(
            @PathVariable Long id,
            @RequestBody AvaliacaoRequestDTO dto) {
        return ResponseEntity.ok(comprovacaoService.avaliar(id, dto));
    }

    @GetMapping("/atividades/{atividadeId}/comprovacoes")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<ComprovacaoResponseDTO>> listarPorAtividade(@PathVariable Long atividadeId) {
        return ResponseEntity.ok(comprovacaoService.listarPorAtividade(atividadeId));
    }
}

