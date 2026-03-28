package br.edu.pe.senac.projeto_pi.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersDTO {
    private Long id;
    private String nome;
    private String email;
    private String perfil;
    private String matricula;
    private LocalDateTime createdAt;

}
