package br.edu.pe.senac.projeto_pi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.edu.pe.senac.projeto_pi.entity.Perfil;
import br.edu.pe.senac.projeto_pi.entity.Users;
import br.edu.pe.senac.projeto_pi.repositories.UsersRepository;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner initAdmin(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            boolean adminExiste = usersRepository
                    .findAll()
                    .stream()
                    .anyMatch(u -> u.getPerfil() == Perfil.ADMINISTRADOR);

            if (!adminExiste) {

                Users admin = new Users();
                admin.setNome("Administrador");
                admin.setEmail("admin@sprig.com");
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setPerfil(Perfil.ADMINISTRADOR);
                admin.setSenhaProvisoria(false);

                usersRepository.save(admin);

                System.out.println("Administrador padrão criado!");
                System.out.println("Email: admin@sprig.com");
                System.out.println("Senha: admin123");
            }
        };
    }
}
