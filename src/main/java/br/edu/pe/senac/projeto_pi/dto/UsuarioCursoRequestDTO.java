package br.edu.pe.senac.projeto_pi.dto;


import br.edu.pe.senac.projeto_pi.entity.UserCurso;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioCursoRequestDTO {
    private Long idUser;
    private Long idCurso;
    private UserCurso.Papel papel;
}
