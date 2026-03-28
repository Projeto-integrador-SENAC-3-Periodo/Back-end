package br.edu.pe.senac.projeto_pi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.pe.senac.projeto_pi.dto.AlterarSenhaRequest;
import br.edu.pe.senac.projeto_pi.dto.LoginRequest;
import br.edu.pe.senac.projeto_pi.dto.LoginResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.Users;
import br.edu.pe.senac.projeto_pi.repositories.UsersRepository;
import br.edu.pe.senac.projeto_pi.security.JwtUtil;
import br.edu.pe.senac.projeto_pi.service.UsersService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UsersService usersService;

    @PostMapping("/login")
    public ResponseEntity<?> autenticar(@Valid @RequestBody LoginRequest request) {
        try {
            Users usuario = usersRepository.findByEmailOrMatricula(
                    request.getIdentificador(), request.getIdentificador())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuario.getEmail(), request.getSenha())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
            String token = jwtUtil.generateToken(userDetails, "ROLE_" + usuario.getPerfil().name());

            LoginResponseDTO response = new LoginResponseDTO(
                token, "Bearer", usuario.getPerfil().name(),
                usuario.getNome(), usuario.getEmail(),
                System.currentTimeMillis() + jwtUtil.getExpirationTime(),
                usuario.isSenhaProvisoria()
            );

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }

    @PutMapping("/alterar-senha")
    public ResponseEntity<?> alterarSenha(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AlterarSenhaRequest request) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);

            if (!jwtUtil.validateToken(token, email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
            }

            usersService.alterarSenha(
                email,
                request.getSenhaAtual(),
                request.getNovaSenha(),
                request.getConfirmacaoNovaSenha()
            );

            return ResponseEntity.ok("Senha alterada com sucesso.");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao alterar senha.");
        }
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            
            if (jwtUtil.validateToken(token, username)) {
                return ResponseEntity.ok("Token válido");
            } else if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token ausente ou mal formatado");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
    }
}
