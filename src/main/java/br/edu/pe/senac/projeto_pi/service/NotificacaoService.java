package br.edu.pe.senac.projeto_pi.service;

import br.edu.pe.senac.projeto_pi.dto.NotificacaoResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.Notificacao;
import br.edu.pe.senac.projeto_pi.repositories.NotificacaoRepository;
import br.edu.pe.senac.projeto_pi.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacaoService {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public List<NotificacaoResponseDTO> listarPorUsuario(Long userId) {
        return notificacaoRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificacaoResponseDTO marcarComoLida(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));
        notificacao.setStatus(Notificacao.StatusNotificacao.LIDA);
        notificacao = notificacaoRepository.save(notificacao);
        return toResponseDTO(notificacao);
    }

    private NotificacaoResponseDTO toResponseDTO(Notificacao n) {
        NotificacaoResponseDTO dto = new NotificacaoResponseDTO();
        dto.setId(n.getIdN());
        dto.setIdUsuario(n.getUser().getId());
        dto.setNomeUsuario(n.getUser().getNome());
        dto.setTitulo(n.getTitulo());
        dto.setMensagem(n.getMensagem());
        dto.setTipo(n.getTipo());
        dto.setStatus(n.getStatus());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}
