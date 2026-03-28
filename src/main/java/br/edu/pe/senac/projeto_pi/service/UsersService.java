package br.edu.pe.senac.projeto_pi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Users cadastrarUser(Users usuario) {

        if (usersRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado!");
        }

        // Se for aluno, matrícula é obrigatória
        if (usuario.getPerfil() == Perfil.ALUNO) {

            if (usuario.getMatricula() == null || usuario.getMatricula().isEmpty()) {
                throw new RuntimeException("Aluno deve possuir matrícula.");
            }

            if (usersRepository.existsByMatricula(usuario.getMatricula())) {
                throw new RuntimeException("Matrícula já cadastrada.");
            }
        }
        
        String senhaProvisoria = usuario.getSenha();

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setSenhaProvisoria(true);

        Users novoUsuario = usersRepository.save(usuario);

        emailService.enviarCredenciais(
            novoUsuario.getNome(),
            novoUsuario.getEmail(),
            senhaProvisoria
        );

        logService.registrar(novoUsuario, "Criou usuário", "Users");

        return novoUsuario;
    }

    @Transactional
    public void alterarSenha(String email, String senhaAtual, String novaSenha, String confirmacaoNovaSenha) {

        Users usuario = usersRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Verifica se a senha atual está correta
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new RuntimeException("Senha atual incorreta.");
        }

        // Verifica se a nova senha e a confirmação são iguais
        if (!novaSenha.equals(confirmacaoNovaSenha)) {
            throw new RuntimeException("Nova senha e confirmação não coincidem.");
        }

        // Verifica se a nova senha é diferente da atual
        if (passwordEncoder.matches(novaSenha, usuario.getSenha())) {
            throw new RuntimeException("A nova senha não pode ser igual à senha atual.");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setSenhaProvisoria(false);

        usersRepository.save(usuario);

        logService.registrar(usuario, "Alterou senha", "Users");
    }
}
