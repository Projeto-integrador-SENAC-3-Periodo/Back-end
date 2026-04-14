package br.edu.pe.senac.projeto_pi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import br.edu.pe.senac.projeto_pi.security.JwtRequestFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita @PreAuthorize nos métodos
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    
    @Autowired
    private CorsConfigurationSource corsConfigurationSource;
 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource)) // Habilita CORS
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth

    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 👈 ESSENCIAL

    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
    .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
    .requestMatchers("/error").permitAll()

    .anyRequest().authenticated()
)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
