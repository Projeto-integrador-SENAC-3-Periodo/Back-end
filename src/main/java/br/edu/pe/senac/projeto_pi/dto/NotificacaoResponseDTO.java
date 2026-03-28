package br.edu.pe.senac.projeto_pi.dto;

import br.edu.pe.senac.projeto_pi.entity.Notificacao;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificacaoResponseDTO {
    private Long id;
    private Long idUsuario;
    private String nomeUsuario;
    private String titulo;
    private String mensagem;
    private Notificacao.TipoNotificacao tipo;
    private Notificacao.StatusNotificacao status;
    private LocalDateTime createdAt;
}
