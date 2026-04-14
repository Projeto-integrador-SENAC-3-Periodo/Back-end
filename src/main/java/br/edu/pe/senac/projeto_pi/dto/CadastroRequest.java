package br.edu.pe.senac.projeto_pi.dto;

import jakarta.validation.constraints.NotBlank;

import br.edu.pe.senac.projeto_pi.entity.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CadastroRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    
    private String matricula;
    
    @NotNull(message = "Perfil é obrigatório")
    private Perfil perfil;
    
}
