package br.edu.pe.senac.projeto_pi.controllers;

import br.edu.pe.senac.projeto_pi.dto.NotificacaoResponseDTO;
import br.edu.pe.senac.projeto_pi.service.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<List<NotificacaoResponseDTO>> listar(@RequestParam Long userId) {
        return ResponseEntity.ok(notificacaoService.listarPorUsuario(userId));
    }

    @PutMapping("/{id}/lida")
    public ResponseEntity<NotificacaoResponseDTO> marcarComoLida(@PathVariable Long id) {
        return ResponseEntity.ok(notificacaoService.marcarComoLida(id));
    }
}
