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
import br.edu.pe.senac.projeto_pi.entity.UserCurso;
import br.edu.pe.senac.projeto_pi.entity.Users;
import br.edu.pe.senac.projeto_pi.repositories.AtividadeRepository;
import br.edu.pe.senac.projeto_pi.repositories.CursoRepository;
import br.edu.pe.senac.projeto_pi.repositories.NotificacaoRepository;
import br.edu.pe.senac.projeto_pi.repositories.TipoAtividadeRepository;
import br.edu.pe.senac.projeto_pi.repositories.UserCursoRepository;
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
    private UserCursoRepository userCursoRepository;

    @Autowired
    private LogSistemaService logService;

    @Transactional
    public AtividadeResponseDTO create(AtividadeRequestDTO dto) {
        // idAluno é OPCIONAL — null = atividade de catálogo criada por coord/admin
        Users aluno = null;
        if (dto.getIdAluno() != null) {
            aluno = userRepository.findById(dto.getIdAluno())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        }

        Curso curso = cursoRepository.findById(dto.getIdCurso())
            .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
        TipoAtividade tipo = tipoAtividadeRepository.findById(dto.getIdTipoAtividade())
            .orElseThrow(() -> new RuntimeException("Tipo de Atividade não encontrado"));

        Atividade atividade = new Atividade();
        atividade.setAluno(aluno);
        atividade.setCurso(curso);
        atividade.setTipoAtividade(tipo);
        atividade.setTitulo(dto.getTitulo());
        atividade.setDescricao(dto.getDescricao());
        atividade.setHorasSolicitadas(dto.getHorasSolicitadas());
        atividade.setPontos(dto.getPontos() != null ? dto.getPontos() : 0);
        atividade.setStatus(Atividade.StatusAtividade.PENDENTE);
        atividade.setDataSubmissao(LocalDateTime.now());

        atividade = atividadeRepository.save(atividade);
        logService.registrarAcaoAtual("Criou atividade: " + atividade.getTitulo(), "Atividade");

        // Notifica todos os alunos do curso sobre a nova atividade
        notificarAlunosDoCurso(curso,
            "Nova atividade disponível",
            "A atividade \"" + atividade.getTitulo() + "\" foi cadastrada no curso "
                + curso.getNome() + ".");

        return toResponseDTO(atividade);
    }

    @Transactional(readOnly = true)
    public List<AtividadeResponseDTO> listAll() {
        return atividadeRepository.findAll().stream()
            .map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AtividadeResponseDTO> listByCurso(Long cursoId) {
        return atividadeRepository.findByCursoIdC(cursoId).stream()
            .map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AtividadeResponseDTO getById(Long id) {
        return toResponseDTO(atividadeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Atividade não encontrada")));
    }

    @Transactional
    public AtividadeResponseDTO update(Long id, AtividadeRequestDTO dto) {
        Atividade atividade = atividadeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));

        if (dto.getIdAluno() != null) {
            Users aluno = userRepository.findById(dto.getIdAluno())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
            atividade.setAluno(aluno);
        }

        Curso curso = cursoRepository.findById(dto.getIdCurso())
            .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
        TipoAtividade tipo = tipoAtividadeRepository.findById(dto.getIdTipoAtividade())
            .orElseThrow(() -> new RuntimeException("Tipo de Atividade não encontrado"));

        atividade.setCurso(curso);
        atividade.setTipoAtividade(tipo);
        atividade.setTitulo(dto.getTitulo());
        atividade.setDescricao(dto.getDescricao());
        atividade.setHorasSolicitadas(dto.getHorasSolicitadas());
        if (dto.getPontos() != null) atividade.setPontos(dto.getPontos());

        atividade = atividadeRepository.save(atividade);
        logService.registrarAcaoAtual("Atualizou atividade: " + atividade.getTitulo(), "Atividade");
        return toResponseDTO(atividade);
    }

    @Transactional
    public void delete(Long id) {
        Atividade atividade = atividadeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
        logService.registrarAcaoAtual("Deletou atividade: " + atividade.getTitulo(), "Atividade");
        atividadeRepository.deleteById(id);
    }

    @Transactional
    public AtividadeResponseDTO aprovar(Long id) {
        Atividade atividade = atividadeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
        atividade.setStatus(Atividade.StatusAtividade.APROVADO);
        atividade.setDataValidacao(LocalDateTime.now());
        atividade = atividadeRepository.save(atividade);

        if (atividade.getAluno() != null) {
            criarNotificacao(atividade.getAluno(), "Atividade aprovada",
                "Sua atividade \"" + atividade.getTitulo() + "\" foi aprovada.");
        }
        logService.registrarAcaoAtual("Aprovou atividade: " + atividade.getTitulo(), "Atividade");
        return toResponseDTO(atividade);
    }

    @Transactional
    public AtividadeResponseDTO reprovar(Long id) {
        Atividade atividade = atividadeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
        atividade.setStatus(Atividade.StatusAtividade.REPROVADO);
        atividade.setDataValidacao(LocalDateTime.now());
        atividade = atividadeRepository.save(atividade);

        if (atividade.getAluno() != null) {
            criarNotificacao(atividade.getAluno(), "Atividade reprovada",
                "Sua atividade \"" + atividade.getTitulo() + "\" foi reprovada.");
        }
        logService.registrarAcaoAtual("Reprovou atividade: " + atividade.getTitulo(), "Atividade");
        return toResponseDTO(atividade);
    }

    // ─── Helpers ─────────────────────────────────────────────────

    private void notificarAlunosDoCurso(Curso curso, String titulo, String mensagem) {
        List<UserCurso> vinculos = userCursoRepository.findByCursoIdC(curso.getIdC());
        for (UserCurso uc : vinculos) {
            if (uc.getPapel() == UserCurso.Papel.ALUNO) {
                criarNotificacao(uc.getUser(), titulo, mensagem);
            }
        }
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

    private AtividadeResponseDTO toResponseDTO(Atividade a) {
        AtividadeResponseDTO dto = new AtividadeResponseDTO();
        dto.setId(a.getIdAtividade());
        if (a.getAluno() != null) {
            dto.setIdAluno(a.getAluno().getId());
            dto.setNomeAluno(a.getAluno().getNome());
        }
        dto.setIdCurso(a.getCurso().getIdC());
        dto.setNomeCurso(a.getCurso().getNome());
        dto.setIdTipoAtividade(a.getTipoAtividade().getIdTA());
        dto.setNomeTipoAtividade(a.getTipoAtividade().getNome());
        dto.setTitulo(a.getTitulo());
        dto.setDescricao(a.getDescricao());
        dto.setHorasSolicitadas(a.getHorasSolicitadas());
        dto.setPontos(a.getPontos());
        dto.setStatus(a.getStatus());
        dto.setDataSubmissao(a.getDataSubmissao());
        dto.setDataValidacao(a.getDataValidacao());
        dto.setCatalogo(a.getAluno() == null);
        return dto;
    }
}
