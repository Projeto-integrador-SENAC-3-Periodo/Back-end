package br.edu.pe.senac.projeto_pi.dto;

import br.edu.pe.senac.projeto_pi.entity.UserCurso;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UsuarioCursoResponseDTO {
    private Long id;
    private Long idUser;
    private String nomeUser;
    private String emailUser;
    private Long idCurso;
    private String nomeCurso;
    private UserCurso.Papel papel;
    private LocalDateTime dataMatricula;
}
