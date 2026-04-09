package br.edu.pe.senac.projeto_pi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String perfil;
    /** Pode ser null para não-alunos */
    private String matricula;
}
