package br.edu.pe.senac.projeto_pi.service;

import br.edu.pe.senac.projeto_pi.dto.AvaliacaoRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.ComprovacaoResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.Certificados;
import br.edu.pe.senac.projeto_pi.entity.ComprovacaoAtividade;
import br.edu.pe.senac.projeto_pi.entity.Notificacao;
import br.edu.pe.senac.projeto_pi.entity.Users;
import br.edu.pe.senac.projeto_pi.repositories.AtividadeRepository;
import br.edu.pe.senac.projeto_pi.repositories.CertificadoRepository;
import br.edu.pe.senac.projeto_pi.repositories.ComprovacaoAtividadeRepository;
import br.edu.pe.senac.projeto_pi.repositories.NotificacaoRepository;
import br.edu.pe.senac.projeto_pi.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComprovacaoAtividadeService {

    @Autowired
    private ComprovacaoAtividadeRepository comprovacaoRepository;

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private CertificadoRepository certificadoRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private LogSistemaService logService;

    @Transactional
    public ComprovacaoResponseDTO enviarComprovante(Long atividadeId, Long alunoId, MultipartFile file) {
        var atividade = atividadeRepository.findById(atividadeId)
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
        Users aluno = usersRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        String arquivoUrl = fileStorageService.storeFile(file, atividadeId);

        ComprovacaoAtividade comprovacao = new ComprovacaoAtividade();
        comprovacao.setAluno(aluno);
        comprovacao.setAtividade(atividade);
        comprovacao.setArquivo(arquivoUrl);
        comprovacao.setStatus(ComprovacaoAtividade.StatusComprovacao.PENDENTE);

        comprovacao = comprovacaoRepository.save(comprovacao);
        logService.registrar(aluno,
            "Enviou comprovante para atividade: " + atividade.getTitulo(), "ComprovacaoAtividade");
        return toResponseDTO(comprovacao);
    }

    @Transactional
    public ComprovacaoResponseDTO avaliar(Long comprovacaoId, AvaliacaoRequestDTO dto) {
        ComprovacaoAtividade comprovacao = comprovacaoRepository.findById(comprovacaoId)
                .orElseThrow(() -> new RuntimeException("Comprovação não encontrada"));

        comprovacao.setStatus(dto.getStatus());
        comprovacao.setObservacao(dto.getObservacao());
        comprovacao = comprovacaoRepository.save(comprovacao);

        String titulo;
        String mensagem;

        if (dto.getStatus() == ComprovacaoAtividade.StatusComprovacao.APROVADO) {
            titulo = "Comprovante aprovado";
            mensagem = "Seu comprovante para a atividade \""
                + comprovacao.getAtividade().getTitulo() + "\" foi aprovado.";

            // Gera certificado automaticamente ao aprovar
            gerarCertificado(comprovacao);

        } else {
            titulo = "Comprovante rejeitado";
            mensagem = "Seu comprovante para a atividade \""
                + comprovacao.getAtividade().getTitulo() + "\" foi rejeitado."
                + (dto.getObservacao() != null && !dto.getObservacao().isBlank()
                    ? " Motivo: " + dto.getObservacao() : "");
        }

        criarNotificacao(comprovacao.getAluno(), titulo, mensagem);
        logService.registrarAcaoAtual(
            "Avaliou comprovante id " + comprovacaoId + " como " + dto.getStatus(),
            "ComprovacaoAtividade");
        return toResponseDTO(comprovacao);
    }

    @Transactional(readOnly = true)
    public List<ComprovacaoResponseDTO> listarPorAtividade(Long atividadeId) {
        return comprovacaoRepository.findByAtividadeIdAtividade(atividadeId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─── Helpers ─────────────────────────────────────────────────

    private void gerarCertificado(ComprovacaoAtividade comprovacao) {
        // Evita duplicata: só cria se ainda não existir certificado para este comprovante
        boolean jaExiste = certificadoRepository
            .findByAlunoId(comprovacao.getAluno().getId())
            .stream()
            .anyMatch(c -> c.getAtividade().getIdAtividade()
                .equals(comprovacao.getAtividade().getIdAtividade()));

        if (!jaExiste) {
            Certificados cert = new Certificados();
            cert.setAtividade(comprovacao.getAtividade());
            cert.setAluno(comprovacao.getAluno());
            cert.setArquivoUrl(comprovacao.getArquivo());
            cert.setTipoArquivo(resolverTipoArquivo(comprovacao.getArquivo()));
            certificadoRepository.save(cert);
        }
    }

    private String resolverTipoArquivo(String url) {
        if (url == null) return "desconhecido";
        String lower = url.toLowerCase();
        if (lower.endsWith(".pdf")) return "PDF";
        if (lower.endsWith(".png")) return "PNG";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "JPEG";
        return "arquivo";
    }

    private void criarNotificacao(Users usuario, String titulo, String mensagem) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUser(usuario);
        notificacao.setTitulo(titulo);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(Notificacao.TipoNotificacao.PUSH);
        notificacao.setStatus(Notificacao.StatusNotificacao.ENVIADA);
        notificacao.setCreatedAt(LocalDateTime.now());
        notificacaoRepository.save(notificacao);
    }

    private ComprovacaoResponseDTO toResponseDTO(ComprovacaoAtividade c) {
        ComprovacaoResponseDTO dto = new ComprovacaoResponseDTO();
        dto.setId(c.getId());
        dto.setIdAluno(c.getAluno().getId());
        dto.setNomeAluno(c.getAluno().getNome());
        dto.setIdAtividade(c.getAtividade().getIdAtividade());
        dto.setTituloAtividade(c.getAtividade().getTitulo());
        dto.setArquivo(c.getArquivo());
        dto.setStatus(c.getStatus());
        dto.setDataEnvio(c.getDataEnvio());
        dto.setObservacao(c.getObservacao());
        return dto;
    }
}
