package br.edu.pe.senac.projeto_pi.service;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.pe.senac.projeto_pi.dto.UsuarioUpdateRequest;
import br.edu.pe.senac.projeto_pi.dto.UsersResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.Perfil;
import br.edu.pe.senac.projeto_pi.entity.Users;
import br.edu.pe.senac.projeto_pi.repositories.UsersRepository;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LogSistemaService logService;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // GERA SENHA PROVISÓRIA
    private String gerarSenhaProvisoria() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder senha = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 8; i++) {
            senha.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }

        return senha.toString();
    }

    // CADASTRO DE USUÁRIO COM SENHA AUTOMÁTICA
    @Transactional
    public Users cadastrarUser(Users usuario) {

        if (usersRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado!");
        }

        if (usuario.getPerfil() == Perfil.ALUNO) {

            if (usuario.getMatricula() == null || usuario.getMatricula().isEmpty()) {
                throw new RuntimeException("Aluno deve possuir matrícula.");
            }

            if (usersRepository.existsByMatricula(usuario.getMatricula())) {
                throw new RuntimeException("Matrícula já cadastrada.");
            }
        }

        // GERA UMA ÚNICA SENHA
        String senhaProvisoria = gerarSenhaProvisoria();

        // CRIPTOGRAFA E SALVA
        usuario.setSenha(passwordEncoder.encode(senhaProvisoria));
        usuario.setSenhaProvisoria(true);

        Users novoUsuario = usersRepository.save(usuario);

        // ENVIA A MESMA SENHA POR EMAIL
        emailService.enviarCredenciais(
            novoUsuario.getNome(),
            novoUsuario.getEmail(),
            senhaProvisoria
        );

        logService.registrar(novoUsuario, "Criou usuário", "Users");

        return novoUsuario;
    }

    // ALTERAR SENHA
    @Transactional
    public void alterarSenha(String email, String senhaAtual, String novaSenha, String confirmacaoNovaSenha) {

        Users usuario = usersRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new RuntimeException("Senha atual incorreta.");
        }

        if (!novaSenha.equals(confirmacaoNovaSenha)) {
            throw new RuntimeException("Nova senha e confirmação não coincidem.");
        }

        if (passwordEncoder.matches(novaSenha, usuario.getSenha())) {
            throw new RuntimeException("A nova senha não pode ser igual à senha atual.");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setSenhaProvisoria(false);

        usersRepository.save(usuario);

        logService.registrar(usuario, "Alterou senha", "Users");
    }

    // LISTAR USUÁRIOS
    @Transactional
    public List<UsersResponseDTO> listarTodos() {
        return usersRepository.findAll()
            .stream()
            .map(u -> new UsersResponseDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getPerfil().name(),
                u.getMatricula()
            ))
            .toList();
    }

    // BUSCAR USUÁRIOS POR NOME, MATRÍCULA OU EMAIL
    @Transactional(readOnly = true)
    public List<UsersResponseDTO> buscarPorTermo(String termo, Perfil perfil) {
        List<Users> results;
        if (termo == null || termo.isBlank()) {
            // No search term: list all (optionally filtered by perfil)
            if (perfil != null) {
                results = usersRepository.findByPerfil(perfil);
            } else {
                results = usersRepository.findAll();
            }
        } else {
            results = usersRepository.buscarPorTermo(termo, perfil);
        }
        return results.stream()
            .map(u -> new UsersResponseDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getPerfil().name(),
                u.getMatricula()
            ))
            .toList();
    }

    // LISTAR TODOS OS ALUNOS
    @Transactional(readOnly = true)
    public List<UsersResponseDTO> listarAlunos() {
        return usersRepository.findByPerfil(Perfil.ALUNO)
            .stream()
            .map(u -> new UsersResponseDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getPerfil().name(),
                u.getMatricula()
            ))
            .toList();
    }

    // ATUALIZAR USUÁRIO
    @Transactional
    public UsersResponseDTO atualizar(Long id, UsuarioUpdateRequest req) {

        Users u = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (req.getNome() != null && !req.getNome().isBlank()) {
            u.setNome(req.getNome().trim());
        }

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            String email = req.getEmail().trim().toLowerCase();

            usersRepository.findByEmail(email).ifPresent(other -> {
                if (!other.getId().equals(id)) {
                    throw new RuntimeException("E-mail já cadastrado para outro usuário.");
                }
            });

            u.setEmail(email);
        }

        if (req.getPerfil() != null) {
            u.setPerfil(req.getPerfil());
        }

        if (u.getPerfil() != Perfil.ALUNO) {
            u.setMatricula(null);
        } else if (req.getMatricula() != null) {
            String mat = req.getMatricula().trim();

            if (mat.isEmpty()) {
                throw new RuntimeException("Aluno deve possuir matrícula.");
            }

            usersRepository.findByMatricula(mat).ifPresent(other -> {
                if (!other.getId().equals(id)) {
                    throw new RuntimeException("Matrícula já cadastrada.");
                }
            });

            u.setMatricula(mat);
        }

        if (req.getSenha() != null && !req.getSenha().isBlank()) {
            u.setSenha(passwordEncoder.encode(req.getSenha()));
            u.setSenhaProvisoria(false);
        }

        usersRepository.save(u);

        logService.registrar(u, "Usuário atualizado pelo administrador", "Users");

        return new UsersResponseDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getPerfil().name(),
                u.getMatricula()
        );
    }

    // REMOVER USUÁRIO
    @Transactional
    public void remover(Long id) {
        Users u = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        usersRepository.delete(u);

        logService.registrarAcaoAtual("Excluiu usuário id " + id, "Users");
    }
}
