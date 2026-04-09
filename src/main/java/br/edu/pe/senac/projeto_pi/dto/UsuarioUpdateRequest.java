package br.edu.pe.senac.projeto_pi.dto;

import br.edu.pe.senac.projeto_pi.entity.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioUpdateRequest {

    @Size(min = 3, max = 100)
    private String nome;

    @Email
    private String email;

    private Perfil perfil;

    private String matricula;

    /** Nova senha (opcional). Se enviada, substitui a atual. */
    @Size(min = 8)
    private String senha;
}
