package br.edu.pe.senac.projeto_pi.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.edu.pe.senac.projeto_pi.entity.Users;
import br.edu.pe.senac.projeto_pi.repositories.UsersRepository;
import br.edu.pe.senac.projeto_pi.security.UserDetailsImpl;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identificador) throws UsernameNotFoundException {
        Users user = userRepository.findByEmailOrMatricula(identificador, identificador)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + identificador));
        return new UserDetailsImpl(user);
    }
}
