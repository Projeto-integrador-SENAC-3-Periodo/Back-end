package br.edu.pe.senac.projeto_pi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import br.edu.pe.senac.projeto_pi.dto.CadastroRequest;
import br.edu.pe.senac.projeto_pi.dto.PontuacaoResponseDTO;
import br.edu.pe.senac.projeto_pi.dto.UsersResponseDTO;
import br.edu.pe.senac.projeto_pi.dto.UsuarioUpdateRequest;
import br.edu.pe.senac.projeto_pi.entity.Atividade;
import br.edu.pe.senac.projeto_pi.entity.Perfil;
import br.edu.pe.senac.projeto_pi.entity.Users;
import br.edu.pe.senac.projeto_pi.repositories.AtividadeRepository;
import br.edu.pe.senac.projeto_pi.repositories.UsersRepository;
import br.edu.pe.senac.projeto_pi.service.UsersService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AtividadeRepository atividadeRepository;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@Valid @RequestBody CadastroRequest request) {
        try {
            Users usuario = new Users(
                request.getNome(),
                request.getEmail(),
                request.getPerfil(),
                null,
                null
            );
            if (request.getPerfil() == Perfil.ALUNO) {
                usuario.setMatricula(request.getMatricula());
            }
            Users novoUsuario = usersService.cadastrarUser(usuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário cadastrado com sucesso. ID: " + novoUsuario.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<List<UsersResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usersService.listarTodos());
    }

    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    @GetMapping("/busca")
    public ResponseEntity<List<UsersResponseDTO>> buscar(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String perfil) {
        Perfil perfilEnum = null;
        if (perfil != null && !perfil.isBlank()) {
            try {
                perfilEnum = Perfil.valueOf(perfil.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // Invalid perfil, ignore filter
            }
        }
        if (q.isBlank() && perfilEnum != null) {
            // If no search term but perfil filter, list all of that perfil
            return ResponseEntity.ok(usersService.buscarPorTermo("", perfilEnum));
        }
        return ResponseEntity.ok(usersService.buscarPorTermo(q, perfilEnum));
    }

    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    @GetMapping("/alunos")
    public ResponseEntity<List<UsersResponseDTO>> listarAlunos() {
        return ResponseEntity.ok(usersService.listarAlunos());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request) {
        try {
            return ResponseEntity.ok(usersService.atualizar(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Long id) {
        try {
            usersService.remover(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/pontuacao")
    public ResponseEntity<PontuacaoResponseDTO> getPontuacao(@PathVariable Long id) {
        Users aluno = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        List<Atividade> aprovadas = atividadeRepository.findByAlunoIdAndStatus(
            id, Atividade.StatusAtividade.APROVADO);
        int totalPontos = aprovadas.stream()
            .mapToInt(a -> a.getPontos() != null ? a.getPontos() : 0).sum();
        return ResponseEntity.ok(
            new PontuacaoResponseDTO(id, aluno.getNome(), totalPontos, aprovadas.size()));
    }
}
