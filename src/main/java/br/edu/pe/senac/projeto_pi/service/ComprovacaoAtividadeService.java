package br.edu.pe.senac.projeto_pi.service;

import br.edu.pe.senac.projeto_pi.dto.AvaliacaoRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.ComprovacaoResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.Atividade;
import br.edu.pe.senac.projeto_pi.entity.ComprovacaoAtividade;
import br.edu.pe.senac.projeto_pi.entity.Notificacao;
import br.edu.pe.senac.projeto_pi.entity.Users;
import br.edu.pe.senac.projeto_pi.repositories.AtividadeRepository;
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
    private FileStorageService fileStorageService;

    @Autowired
    private LogSistemaService logService;

    @Transactional
    public ComprovacaoResponseDTO enviarComprovante(Long atividadeId, Long alunoId, MultipartFile file) {
        Atividade atividade = atividadeRepository.findById(atividadeId)
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
        logService.registrar(aluno, "Enviou comprovante para atividade: " + atividade.getTitulo(), "ComprovacaoAtividade");
        return toResponseDTO(comprovacao);
    }

    @Transactional
    public ComprovacaoResponseDTO avaliar(Long comprovacaoId, AvaliacaoRequestDTO dto) {
        ComprovacaoAtividade comprovacao = comprovacaoRepository.findById(comprovacaoId)
                .orElseThrow(() -> new RuntimeException("Comprovação não encontrada"));

        comprovacao.setStatus(dto.getStatus());
        comprovacao.setObservacao(dto.getObservacao());
        comprovacao = comprovacaoRepository.save(comprovacao);

        String titulo = dto.getStatus() == ComprovacaoAtividade.StatusComprovacao.APROVADO
                ? "Comprovante aprovado" : "Comprovante rejeitado";
        String mensagem = dto.getStatus() == ComprovacaoAtividade.StatusComprovacao.APROVADO
                ? "Seu comprovante para a atividade \"" + comprovacao.getAtividade().getTitulo() + "\" foi aprovado."
                : "Seu comprovante para a atividade \"" + comprovacao.getAtividade().getTitulo() + "\" foi rejeitado. " + (dto.getObservacao() != null ? dto.getObservacao() : "");

        Notificacao notificacao = new Notificacao();
        notificacao.setUser(comprovacao.getAluno());
        notificacao.setTitulo(titulo);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(Notificacao.TipoNotificacao.PUSH);
        notificacao.setStatus(Notificacao.StatusNotificacao.ENVIADA);
        notificacao.setCreatedAt(LocalDateTime.now());
        notificacaoRepository.save(notificacao);
        logService.registrarAcaoAtual("Avaliou comprovante id " + comprovacaoId + " como " + dto.getStatus(), "ComprovacaoAtividade");
        return toResponseDTO(comprovacao);
    }

    @Transactional(readOnly = true)
    public List<ComprovacaoResponseDTO> listarPorAtividade(Long atividadeId) {
        return comprovacaoRepository.findByAtividadeIdAtividade(atividadeId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
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
