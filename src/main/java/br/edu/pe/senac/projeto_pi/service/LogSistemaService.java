package br.edu.pe.senac.projeto_pi.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.edu.pe.senac.projeto_pi.entity.LogSistema;
import br.edu.pe.senac.projeto_pi.entity.Users;
import br.edu.pe.senac.projeto_pi.repositories.LogSistemaRepository;
import br.edu.pe.senac.projeto_pi.repositories.UsersRepository;

@Service
public class LogSistemaService {

    @Autowired
    private LogSistemaRepository logRepository;

    @Autowired
    private UsersRepository usersRepository;

    public void registrar(Users usuario, String acao, String entidade) {
        LogSistema log = new LogSistema();
        log.setUser(usuario);
        log.setAcao(acao);
        log.setEntidade(entidade);
        logRepository.save(log);
    }

    public void registrar(String email, String acao, String entidade) {
        LogSistema log = new LogSistema();
        Optional<Users> user = usersRepository.findByEmail(email);
        user.ifPresent(log::setUser);
        log.setAcao(acao);
        log.setEntidade(entidade);
        logRepository.save(log);
    }

    public void registrarAcaoAtual(String acao, String entidade) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            registrar(email, acao, entidade);
        } catch (Exception ignored) {
        }
    }
}
