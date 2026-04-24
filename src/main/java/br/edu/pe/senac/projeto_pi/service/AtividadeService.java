package br.edu.pe.senac.projeto_pi.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.edu.pe.senac.projeto_pi.dto.AtividadeRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.AtividadeResponseDTO;
import br.edu.pe.senac.projeto_pi.dto.AvaliacaoRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.HorasAlunoResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.Atividade;
import br.edu.pe.senac.projeto_pi.entity.Certificados;
import br.edu.pe.senac.projeto_pi.entity.Curso;
import br.edu.pe.senac.projeto_pi.entity.Notificacao;
import br.edu.pe.senac.projeto_pi.entity.TipoAtividade;
import br.edu.pe.senac.projeto_pi.entity.UserCurso;
import br.edu.pe.senac.projeto_pi.entity.Users;
import br.edu.pe.senac.projeto_pi.entity.Perfil;
import br.edu.pe.senac.projeto_pi.repositories.AtividadeRepository;
import br.edu.pe.senac.projeto_pi.repositories.CertificadoRepository;
import br.edu.pe.senac.projeto_pi.repositories.CursoRepository;
import br.edu.pe.senac.projeto_pi.repositories.NotificacaoRepository;
import br.edu.pe.senac.projeto_pi.repositories.TipoAtividadeRepository;
import br.edu.pe.senac.projeto_pi.repositories.UserCursoRepository;
import br.edu.pe.senac.projeto_pi.repositories.UsersRepository;

@Service
public class AtividadeService {

    @Autowired private AtividadeRepository atividadeRepository;
    @Autowired private UsersRepository userRepository;
    @Autowired private CursoRepository cursoRepository;
    @Autowired private CertificadoRepository certificadoRepository;
    @Autowired private TipoAtividadeRepository tipoAtividadeRepository;
    @Autowired private NotificacaoRepository notificacaoRepository;
    @Autowired private UserCursoRepository userCursoRepository;
    @Autowired private FileStorageService fileStorageService;
    @Autowired private LogSistemaService logService;
    @Autowired private EmailService emailService;

    // ─── Helper: teto de horas do curso ──────────────────────────

    private int limiteCurso(Curso curso) {
        Integer limite = curso.getHorasComplementares();
        return (limite != null && limite > 0) ? limite : 1;
    }

    // ─── Helper: valida se o coordenador pertence ao curso ────────

    /**
     * Verifica se o usuário autenticado (pelo email) é COORDENADOR do curso informado.
     * ADMINISTRADOR passa livre — não precisa ser vinculado ao curso.
     * Lança exceção 403 se a verificação falhar.
     */
    private void validarCoordenadorDoCurso(String emailAutenticado, Long cursoId) {
        Users usuario = userRepository.findByEmail(emailAutenticado)
            .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado"));

        // Admin pode tudo
        if (usuario.getPerfil() == Perfil.ADMINISTRADOR) return;

        // Coordenador: precisa estar vinculado ao curso com papel COORDENADOR
        userCursoRepository.findByUserIdAndCursoIdC(usuario.getId(), cursoId)
            .filter(uc -> uc.getPapel() == UserCurso.Papel.COORDENADOR)
            .orElseThrow(() -> new RuntimeException(
                "Acesso negado: você não é coordenador deste curso"));
    }

    /**
     * Valida que o coordenador autenticado é coordenador do curso da atividade.
     */
    private void validarCoordenadorDaAtividade(String emailAutenticado, Atividade atividade) {
        validarCoordenadorDoCurso(emailAutenticado, atividade.getCurso().getIdC());
    }

    // ─── ALUNO: Submeter nova atividade ───────────────────────────

    @Transactional
    public AtividadeResponseDTO submeterAtividade(Long alunoId,
                                                   AtividadeRequestDTO dto,
                                                   MultipartFile comprovante) {
        Users aluno = userRepository.findById(alunoId)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        Curso curso = cursoRepository.findById(dto.getIdCurso())
            .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        userCursoRepository.findByUserIdAndCursoIdC(alunoId, curso.getIdC())
            .orElseThrow(() -> new RuntimeException("Aluno não está matriculado neste curso"));
        
        if (dto.getTipoAtividade() == null || dto.getTipoAtividade().isBlank())
            throw new RuntimeException("Tipo de atividade é obrigatório");

        if (dto.getCategoriaFixa() == null)
            throw new RuntimeException("Categoria obrigatória");

        if (dto.getDescricao() == null || dto.getDescricao().isBlank())
            throw new RuntimeException("Descrição é obrigatória");
        
        if (dto.getHorasSolicitadas() == null || dto.getHorasSolicitadas() <= 0)
            throw new RuntimeException("Horas inválidas");

        String contentType = comprovante.getContentType();
        if (contentType == null ||
            !(contentType.equals("application/pdf") ||
              contentType.equals("image/png") ||
              contentType.equals("image/jpeg") ||
              contentType.equals("image/jpg")))
            throw new RuntimeException("Apenas PDF ou imagem (PNG/JPG)");

        if (comprovante.getSize() > 10 * 1024 * 1024)
            throw new RuntimeException("Arquivo máximo 10MB");

        int limiteHoras = limiteCurso(curso);
        int horasJaAprovadas = atividadeRepository
            .somarHorasAprovadasPorAlunoECurso(alunoId, curso.getIdC());
        if (horasJaAprovadas >= limiteHoras)
            throw new RuntimeException("Teto de " + limiteHoras + "h complementares já atingido neste curso");

        Atividade atividade = new Atividade();
        atividade.setAluno(aluno);
        atividade.setCurso(curso);
        atividade.setCategoriaFixa(dto.getCategoriaFixa());
        atividade.setTipoAtividade(dto.getTipoAtividade());
        atividade.setDescricao(dto.getDescricao());
        atividade.setDescricao(dto.getDescricao());
        atividade.setHorasSolicitadas(dto.getHorasSolicitadas());
        atividade.setStatus(Atividade.StatusAtividade.PENDENTE);
        atividade.setDataSubmissao(LocalDateTime.now());
        atividade.setTentativas(0);
        atividade = atividadeRepository.save(atividade);

        String comprovanteUrl = fileStorageService.storeFile(comprovante, atividade.getIdAtividade());
        atividade.setComprovanteUrl(comprovanteUrl);
        atividade = atividadeRepository.save(atividade);

        notificarCoordenadoresDoCurso(curso,
            "Nova atividade aguardando avaliação",
            "O aluno " + aluno.getNome() + " enviou a atividade \"" +
                atividade.getTitulo() + "\" aguardando sua avaliação.");

        logService.registrar(aluno, "Submeteu atividade: " + atividade.getTitulo(), "Atividade");
        return toResponseDTO(atividade);
    }

    // ─── ALUNO: Reenviar atividade reprovada ──────────────────────

    @Transactional
    public AtividadeResponseDTO reenviarAtividade(Long atividadeId,
                                                   Long alunoId,
                                                   AtividadeRequestDTO dto,
                                                   MultipartFile novoComprovante) {
        Atividade atividade = atividadeRepository.findById(atividadeId)
            .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));

        if (!atividade.getAluno().getId().equals(alunoId))
            throw new RuntimeException("Você não tem permissão para reenviar esta atividade");

        if (atividade.getStatus() != Atividade.StatusAtividade.REPROVADO)
            throw new RuntimeException(
                "Apenas atividades REPROVADAS podem ser reenviadas. Status atual: " + atividade.getStatus());

        if (dto.getCategoriaFixa() != null)  atividade.setCategoriaFixa(dto.getCategoriaFixa());
        if (dto.getTipoAtividade() != null)  atividade.setTipoAtividade(dto.getTipoAtividade());
        if (dto.getDescricao() != null)   atividade.setDescricao(dto.getDescricao());
        if (dto.getHorasSolicitadas() != null && dto.getHorasSolicitadas() > 0)
            atividade.setHorasSolicitadas(dto.getHorasSolicitadas());

        if (novoComprovante != null && !novoComprovante.isEmpty()) {
            String contentType = novoComprovante.getContentType();
            if (contentType == null ||
                !(contentType.equals("application/pdf") ||
                  contentType.equals("image/png") ||
                  contentType.equals("image/jpeg") ||
                  contentType.equals("image/jpg")))
                throw new RuntimeException("Formato inválido. Use PDF, PNG ou JPG.");
            String url = fileStorageService.storeFile(novoComprovante, atividade.getIdAtividade());
            atividade.setComprovanteUrl(url);
        }

        atividade.setStatus(Atividade.StatusAtividade.PENDENTE);
        atividade.setMotivoReprovacao(null);
        atividade.setHorasAprovadas(null);
        atividade.setDataValidacao(null);
        atividade.setDataSubmissao(LocalDateTime.now());
        atividade.setTentativas(atividade.getTentativas() + 1);
        atividade = atividadeRepository.save(atividade);

        notificarCoordenadoresDoCurso(atividade.getCurso(),
            "Atividade reenviada para avaliação",
            "O aluno " + atividade.getAluno().getNome() +
                " corrigiu e reenviou a atividade \"" + atividade.getTitulo() + "\".");

        logService.registrar(atividade.getAluno(),
            "Reenviou atividade id=" + atividadeId + ": " + atividade.getTitulo(), "Atividade");
        return toResponseDTO(atividade);
    }

    // ─── COORDENADOR: Avaliar atividade ───────────────────────────

    @Transactional
    public AtividadeResponseDTO avaliarAtividade(Long atividadeId,
                                                  AvaliacaoRequestDTO dto,
                                                  String emailCoordenador) {
        Atividade atividade = atividadeRepository.findById(atividadeId)
            .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));

        // Garante que o coordenador só avalia atividades do seu próprio curso
        validarCoordenadorDaAtividade(emailCoordenador, atividade);

        if (atividade.getStatus() != Atividade.StatusAtividade.PENDENTE)
            throw new RuntimeException(
                "Apenas atividades PENDENTES podem ser avaliadas. Status atual: " + atividade.getStatus());

        if (dto.getStatus() == null)
            throw new RuntimeException("O status da avaliação é obrigatório (APROVADO ou REPROVADO)");

        if (dto.getCategoriaFixa() != null)
            atividade.setCategoriaFixa(dto.getCategoriaFixa());

        if (dto.getTipoAtividade() != null) {
        	    atividade.setTipoAtividade(dto.getTipoAtividade());
        }

        if (dto.getStatus() == Atividade.StatusAtividade.APROVADO) {
            processarAprovacao(atividade, dto);
        } else if (dto.getStatus() == Atividade.StatusAtividade.REPROVADO) {
            processarReprovacao(atividade, dto);
        } else {
            throw new RuntimeException("Status inválido: " + dto.getStatus());
        }

        atividade.setDataValidacao(LocalDateTime.now());
        atividade = atividadeRepository.save(atividade);

        logService.registrarAcaoAtual(
            "Avaliou atividade id=" + atividadeId + " como " + dto.getStatus(), "Atividade");
        return toResponseDTO(atividade);
    }

    // ─── COORDENADOR: Consultas filtradas pelo seu curso ─────────

    /**
     * Lista atividades PENDENTES apenas dos cursos que o coordenador coordena.
     * ADMINISTRADOR vê todos os cursos.
     */
    @Transactional(readOnly = true)
    public List<AtividadeResponseDTO> listarPendentesPorCurso(Long cursoId,
                                                               String emailAutenticado) {
        validarCoordenadorDoCurso(emailAutenticado, cursoId);
        return atividadeRepository.findPendentesByCurso(cursoId).stream()
            .map(this::toResponseDTO).collect(Collectors.toList());
    }

    /**
     * Lista todas as atividades (qualquer status) de um curso.
     * Coordenador só acessa cursos que coordena; Admin acessa todos.
     */
    @Transactional(readOnly = true)
    public List<AtividadeResponseDTO> listarPorCurso(Long cursoId, String emailAutenticado) {
        validarCoordenadorDoCurso(emailAutenticado, cursoId);
        return atividadeRepository.findByCursoIdC(cursoId).stream()
            .map(this::toResponseDTO).collect(Collectors.toList());
    }

    // ─── Consultas sem restrição de curso ─────────────────────────

    @Transactional(readOnly = true)
    public List<AtividadeResponseDTO> listarTodasAtividades() {
        return atividadeRepository.findAll().stream()
            .map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AtividadeResponseDTO> listarPorAluno(Long alunoId) {
        userRepository.findById(alunoId)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        return atividadeRepository.findByAlunoId(alunoId).stream()
            .map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AtividadeResponseDTO buscarPorId(Long id) {
        return toResponseDTO(atividadeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Atividade não encontrada")));
    }

    @Transactional(readOnly = true)
    public HorasAlunoResponseDTO consultarHorasAluno(Long alunoId, Long cursoId) {
        Users aluno = userRepository.findById(alunoId)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        Curso curso = cursoRepository.findById(cursoId)
            .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        int limiteHoras    = limiteCurso(curso);
        int horasAprovadas = atividadeRepository.somarHorasAprovadasPorAlunoECurso(alunoId, cursoId);
        long pendentes     = atividadeRepository.countByAlunoIdAndStatus(alunoId, Atividade.StatusAtividade.PENDENTE);
        long aprovadas     = atividadeRepository.countByAlunoIdAndStatus(alunoId, Atividade.StatusAtividade.APROVADO);
        long reprovadas    = atividadeRepository.countByAlunoIdAndStatus(alunoId, Atividade.StatusAtividade.REPROVADO);

        return new HorasAlunoResponseDTO(
            alunoId, aluno.getNome(), horasAprovadas, limiteHoras,
            Math.max(0, limiteHoras - horasAprovadas),
            (int) pendentes, (int) aprovadas, (int) reprovadas);
    }

    // ─── Aprovação ────────────────────────────────────────────────

    private void processarAprovacao(Atividade atividade, AvaliacaoRequestDTO dto) {
        int horasParaAprovar = (dto.getHorasAprovadas() != null && dto.getHorasAprovadas() > 0)
            ? dto.getHorasAprovadas()
            : atividade.getHorasSolicitadas();

        int limiteHoras  = limiteCurso(atividade.getCurso());
        int horasJaAprov = atividadeRepository.somarHorasAprovadasPorAlunoECurso(
                               atividade.getAluno().getId(), atividade.getCurso().getIdC());
        int horasDisp    = limiteHoras - horasJaAprov;

        if (horasDisp <= 0)
            throw new RuntimeException(
                "Aluno já atingiu o teto de " + limiteHoras + "h complementares neste curso");

        int horasEfetivas = Math.min(horasParaAprovar, horasDisp);
        String msgExtra   = (horasEfetivas < horasParaAprovar) ? " (limitado ao teto do curso)" : "";

        atividade.setHorasAprovadas(horasEfetivas);
        atividade.setStatus(Atividade.StatusAtividade.APROVADO);
        atividade.setMotivoReprovacao(null);

        Users aluno = atividade.getAluno();

        criarNotificacao(aluno,
            "Atividade aprovada ✓",
            "Sua atividade \"" + atividade.getTitulo() + "\" foi APROVADA! " +
                "Horas creditadas: " + horasEfetivas + "h" + msgExtra + ".");

        emailService.enviarAprovacaoAtividade(
            aluno.getNome(), aluno.getEmail(),
            atividade.getTitulo(), horasEfetivas, limiteHoras);
    }

    // ─── Reprovação ───────────────────────────────────────────────

    private void processarReprovacao(Atividade atividade, AvaliacaoRequestDTO dto) {
        if (dto.getMotivoReprovacao() == null || dto.getMotivoReprovacao().isBlank())
            throw new RuntimeException("O motivo da reprovação é obrigatório");

        atividade.setStatus(Atividade.StatusAtividade.REPROVADO);
        atividade.setMotivoReprovacao(dto.getMotivoReprovacao());
        atividade.setHorasAprovadas(null);

        Users aluno = atividade.getAluno();

        criarNotificacao(aluno,
            "Atividade reprovada ✗",
            "Sua atividade \"" + atividade.getTitulo() + "\" foi REPROVADA. " +
                "Motivo: " + dto.getMotivoReprovacao() + " Você pode corrigir e reenviar.");

        emailService.enviarReprovacaoAtividade(
            aluno.getNome(), aluno.getEmail(),
            atividade.getTitulo(), dto.getMotivoReprovacao());
    }

    // ─── Helpers internos ─────────────────────────────────────────

    private void notificarCoordenadoresDoCurso(Curso curso, String titulo, String mensagem) {
        List<UserCurso> vinculos = userCursoRepository.findByCursoIdC(curso.getIdC());
        for (UserCurso uc : vinculos) {
            if (uc.getPapel() == UserCurso.Papel.COORDENADOR) {
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
        dto.setIdAluno(a.getAluno().getId());
        dto.setNomeAluno(a.getAluno().getNome());
        dto.setIdCurso(a.getCurso().getIdC());
        dto.setNomeCurso(a.getCurso().getNome());
        dto.setTipoAtividade(a.getTipoAtividade());
        dto.setCategoriaFixa(a.getCategoriaFixa());
        dto.setDescricao(a.getDescricao());
        dto.setHorasSolicitadas(a.getHorasSolicitadas());
        dto.setHorasAprovadas(a.getHorasAprovadas());
        dto.setComprovanteUrl(a.getComprovanteUrl());
        dto.setStatus(a.getStatus());
        dto.setMotivoReprovacao(a.getMotivoReprovacao());
        dto.setTentativas(a.getTentativas());
        dto.setDataSubmissao(a.getDataSubmissao());
        dto.setDataValidacao(a.getDataValidacao());
        return dto;
    }

    @SuppressWarnings("unused")
    private void gerarCertificadoConclusao(Users aluno, Curso curso) {
        boolean jaExiste = certificadoRepository.existsByAlunoIdAndCursoIdC(aluno.getId(), curso.getIdC());
        if (!jaExiste) {
            Certificados cert = new Certificados();
            cert.setAluno(aluno);
            cert.setCurso(curso);
            cert.setDescricao("Certificado de conclusão de horas complementares");
            certificadoRepository.save(cert);
        }
    }
}
