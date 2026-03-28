package br.edu.pe.senac.projeto_pi.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.pe.senac.projeto_pi.dto.AtividadeRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.AtividadeResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.Atividade;
import br.edu.pe.senac.projeto_pi.entity.Curso;
import br.edu.pe.senac.projeto_pi.entity.Notificacao;
import br.edu.pe.senac.projeto_pi.entity.TipoAtividade;
import br.edu.pe.senac.projeto_pi.entity.Users;
import br.edu.pe.senac.projeto_pi.repositories.AtividadeRepository;
import br.edu.pe.senac.projeto_pi.repositories.CursoRepository;
import br.edu.pe.senac.projeto_pi.repositories.NotificacaoRepository;
import br.edu.pe.senac.projeto_pi.repositories.TipoAtividadeRepository;
import br.edu.pe.senac.projeto_pi.repositories.UsersRepository;

@Service
public class AtividadeService {

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private TipoAtividadeRepository tipoAtividadeRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private LogSistemaService logService;

    @Transactional
    public AtividadeResponseDTO create(AtividadeRequestDTO atividadeRequestDTO) {
        Users aluno = userRepository.findById(atividadeRequestDTO.getIdAluno()).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        Curso curso = cursoRepository.findById(atividadeRequestDTO.getIdCurso()).orElseThrow(() -> new RuntimeException("Curso não encontrado"));
        TipoAtividade tipoAtividade = tipoAtividadeRepository.findById(atividadeRequestDTO.getIdTipoAtividade()).orElseThrow(() -> new RuntimeException("Tipo de Atividade não encontrado"));

        Atividade atividade = new Atividade();
        atividade.setAluno(aluno);
        atividade.setCurso(curso);
        atividade.setTipoAtividade(tipoAtividade);
        atividade.setTitulo(atividadeRequestDTO.getTitulo());
        atividade.setDescricao(atividadeRequestDTO.getDescricao());
        atividade.setHorasSolicitadas(atividadeRequestDTO.getHorasSolicitadas());
        atividade.setPontos(atividadeRequestDTO.getPontos() != null ? atividadeRequestDTO.getPontos() : 0);
        atividade.setStatus(Atividade.StatusAtividade.PENDENTE);
        atividade.setDataSubmissao(LocalDateTime.now());

        atividade = atividadeRepository.save(atividade);
        logService.registrarAcaoAtual("Criou atividade: " + atividade.getTitulo(), "Atividade");
        return toResponseDTO(atividade);
    }

    @Transactional(readOnly = true)
    public List<AtividadeResponseDTO> listAll() {
        return atividadeRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AtividadeResponseDTO> listByCurso(Long cursoId) {
        return atividadeRepository.findByCursoIdC(cursoId).stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AtividadeResponseDTO getById(Long id) {
        Atividade atividade = atividadeRepository.findById(id).orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
        return toResponseDTO(atividade);
    }

    @Transactional
    public AtividadeResponseDTO update(Long id, AtividadeRequestDTO atividadeRequestDTO) {
        Atividade atividade = atividadeRepository.findById(id).orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
        Users aluno = userRepository.findById(atividadeRequestDTO.getIdAluno()).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        Curso curso = cursoRepository.findById(atividadeRequestDTO.getIdCurso()).orElseThrow(() -> new RuntimeException("Curso não encontrado"));
        TipoAtividade tipoAtividade = tipoAtividadeRepository.findById(atividadeRequestDTO.getIdTipoAtividade()).orElseThrow(() -> new RuntimeException("Tipo de Atividade não encontrado"));

        atividade.setAluno(aluno);
        atividade.setCurso(curso);
        atividade.setTipoAtividade(tipoAtividade);
        atividade.setTitulo(atividadeRequestDTO.getTitulo());
        atividade.setDescricao(atividadeRequestDTO.getDescricao());
        atividade.setHorasSolicitadas(atividadeRequestDTO.getHorasSolicitadas());
        if (atividadeRequestDTO.getPontos() != null) {
            atividade.setPontos(atividadeRequestDTO.getPontos());
        }

        atividade = atividadeRepository.save(atividade);
        logService.registrarAcaoAtual("Atualizou atividade: " + atividade.getTitulo(), "Atividade");
        return toResponseDTO(atividade);
    }

    @Transactional
    public void delete(Long id) {
        Atividade atividade = atividadeRepository.findById(id).orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
        logService.registrarAcaoAtual("Deletou atividade: " + atividade.getTitulo(), "Atividade");
        atividadeRepository.deleteById(id);
    }

    @Transactional
    public AtividadeResponseDTO aprovar(Long id) {
        Atividade atividade = atividadeRepository.findById(id).orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
        atividade.setStatus(Atividade.StatusAtividade.APROVADO);
        atividade.setDataValidacao(LocalDateTime.now());
        atividade = atividadeRepository.save(atividade);

        criarNotificacao(atividade.getAluno(), "Atividade aprovada",
                "Sua atividade \"" + atividade.getTitulo() + "\" foi aprovada.");
        logService.registrarAcaoAtual("Aprovou atividade: " + atividade.getTitulo(), "Atividade");
        return toResponseDTO(atividade);
    }

    @Transactional
    public AtividadeResponseDTO reprovar(Long id) {
        Atividade atividade = atividadeRepository.findById(id).orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
        atividade.setStatus(Atividade.StatusAtividade.REPROVADO);
        atividade.setDataValidacao(LocalDateTime.now());
        atividade = atividadeRepository.save(atividade);

        criarNotificacao(atividade.getAluno(), "Atividade reprovada",
                "Sua atividade \"" + atividade.getTitulo() + "\" foi reprovada.");
        logService.registrarAcaoAtual("Reprovou atividade: " + atividade.getTitulo(), "Atividade");
        return toResponseDTO(atividade);
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

    private AtividadeResponseDTO toResponseDTO(Atividade atividade) {
        AtividadeResponseDTO dto = new AtividadeResponseDTO();
        dto.setId(atividade.getIdAtividade());
        dto.setIdAluno(atividade.getAluno().getId());
        dto.setNomeAluno(atividade.getAluno().getNome());
        dto.setIdCurso(atividade.getCurso().getIdC());
        dto.setNomeCurso(atividade.getCurso().getNome());
        dto.setIdTipoAtividade(atividade.getTipoAtividade().getIdTA());
        dto.setNomeTipoAtividade(atividade.getTipoAtividade().getNome());
        dto.setTitulo(atividade.getTitulo());
        dto.setDescricao(atividade.getDescricao());
        dto.setHorasSolicitadas(atividade.getHorasSolicitadas());
        dto.setPontos(atividade.getPontos());
        dto.setStatus(atividade.getStatus());
        dto.setDataSubmissao(atividade.getDataSubmissao());
        dto.setDataValidacao(atividade.getDataValidacao());
        return dto;
    }
}
