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
import br.edu.pe.senac.projeto_pi.repositories.AtividadeRepository;
import br.edu.pe.senac.projeto_pi.repositories.CertificadoRepository;
import br.edu.pe.senac.projeto_pi.repositories.CursoRepository;
import br.edu.pe.senac.projeto_pi.repositories.NotificacaoRepository;
import br.edu.pe.senac.projeto_pi.repositories.TipoAtividadeRepository;
import br.edu.pe.senac.projeto_pi.repositories.UserCursoRepository;
import br.edu.pe.senac.projeto_pi.repositories.UsersRepository;

@Service
public class AtividadeService {

    /**
     * Retorna o teto de horas complementares do curso.
     * Definido pelo administrador/coordenador ao cadastrar o curso (campo horasComplementares).
     * Nunca retorna zero — usa 1 como mínimo defensivo.
     */
    private int limiteCurso(Curso curso) {
        Integer limite = curso.getHorasComplementares();
        return (limite != null && limite > 0) ? limite : 1;
    }

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private CursoRepository cursoRepository;
    
    @Autowired
    private CertificadoRepository certificadoRepository;

    @Autowired
    private TipoAtividadeRepository tipoAtividadeRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private UserCursoRepository userCursoRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private LogSistemaService logService;

    // ALUNO: Submeter nova atividade com comprovante

    /**
     * Aluno envia uma nova atividade com todos os dados e o arquivo comprovante.
     * Status inicial: PENDENTE.
     */
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

        TipoAtividade tipo = tipoAtividadeRepository.findById(dto.getIdTipoAtividade())
            .orElseThrow(() -> new RuntimeException("Tipo de atividade não encontrado"));

        if (dto.getCategoriaFixa() == null) {
            throw new RuntimeException("Categoria obrigatória");
        }

        if (dto.getHorasSolicitadas() == null || dto.getHorasSolicitadas() <= 0) {
            throw new RuntimeException("Horas inválidas");
        }

        // VALIDAÇÃO DO ARQUIVO
        String contentType = comprovante.getContentType();

        if (contentType == null ||
            !(contentType.equals("application/pdf") ||
              contentType.equals("image/png") ||
              contentType.equals("image/jpeg") ||
              contentType.equals("image/jpg"))) {

            throw new RuntimeException("Apenas PDF ou imagem (PNG/JPG)");
        }

        if (comprovante.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("Arquivo máximo 5MB");
        }

        int limiteHoras = limiteCurso(curso);

        int horasJaAprovadas = atividadeRepository
            .somarHorasAprovadasPorAlunoECurso(alunoId, curso.getIdC());

        if (horasJaAprovadas >= limiteHoras) {
            throw new RuntimeException("Teto de horas já atingido");
        }

        // Salva o comprovante primeiro para obter um ID temporário (usa timestamp)
        Atividade atividade = new Atividade();
        atividade.setAluno(aluno);
        atividade.setCurso(curso);
        atividade.setTipoAtividade(tipo);
        atividade.setCategoriaFixa(dto.getCategoriaFixa());
        atividade.setTitulo(dto.getTitulo() != null ? dto.getTitulo() : tipo.getNome());
        atividade.setDescricao(dto.getDescricao());
        atividade.setHorasSolicitadas(dto.getHorasSolicitadas());
        atividade.setStatus(Atividade.StatusAtividade.PENDENTE);
        atividade.setDataSubmissao(LocalDateTime.now());
        atividade.setTentativas(0);

        atividade = atividadeRepository.save(atividade);

        // Armazena o arquivo usando o ID da atividade como diretório
        String comprovanteUrl = fileStorageService.storeFile(comprovante, atividade.getIdAtividade());
        atividade.setComprovanteUrl(comprovanteUrl);

        atividade = atividadeRepository.save(atividade);

        notificarCoordenadoresDoCurso(curso,
            "Nova atividade aguardando avaliação",
            "O aluno " + aluno.getNome() + " enviou a atividade \"" +
                atividade.getTitulo() + "\" aguardando sua avaliação.");

        logService.registrar(aluno,
            "Submeteu atividade: " + atividade.getTitulo(), "Atividade");

        return toResponseDTO(atividade);
    }


    // ALUNO: Reenviar atividade reprovada (com novo comprovante)

    /**
     * Aluno corrige e reenvia uma atividade previamente reprovada.
     * Apenas atividades com status REPROVADO podem ser reenviadas.
     * O status volta para PENDENTE e os dados podem ser atualizados.
     */
    @Transactional
    public AtividadeResponseDTO reenviarAtividade(Long atividadeId,
                                                   Long alunoId,
                                                   AtividadeRequestDTO dto,
                                                   MultipartFile novoComprovante) {
        Atividade atividade = atividadeRepository.findById(atividadeId)
            .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));

        // Valida dono
        if (!atividade.getAluno().getId().equals(alunoId)) {
            throw new RuntimeException("Você não tem permissão para reenviar esta atividade");
        }

        // Só permite reenvio se estiver REPROVADO
        if (atividade.getStatus() != Atividade.StatusAtividade.REPROVADO) {
            throw new RuntimeException(
                "Apenas atividades REPROVADAS podem ser reenviadas. Status atual: " + atividade.getStatus());
        }

        // Atualiza campos informados pelo aluno
        if (dto.getCategoriaFixa() != null) {
            atividade.setCategoriaFixa(dto.getCategoriaFixa());
        }
        if (dto.getIdTipoAtividade() != null) {
            TipoAtividade tipo = tipoAtividadeRepository.findById(dto.getIdTipoAtividade())
                .orElseThrow(() -> new RuntimeException("Tipo de atividade não encontrado"));
            atividade.setTipoAtividade(tipo);
        }
        if (dto.getDescricao() != null) {
            atividade.setDescricao(dto.getDescricao());
        }
        if (dto.getHorasSolicitadas() != null && dto.getHorasSolicitadas() > 0) {
            atividade.setHorasSolicitadas(dto.getHorasSolicitadas());
        }
        if (dto.getTitulo() != null) {
            atividade.setTitulo(dto.getTitulo());
        }

        // Novo comprovante, se enviado
        if (novoComprovante != null && !novoComprovante.isEmpty()) {
        	if (novoComprovante != null && !novoComprovante.isEmpty()) {

        	    String contentType = novoComprovante.getContentType();

        	    if (contentType == null ||
        	        !(contentType.equals("application/pdf") ||
        	          contentType.equals("image/png") ||
        	          contentType.equals("image/jpeg"))) {

        	        throw new RuntimeException("Formato inválido");
        	    }

        	    String url = fileStorageService.storeFile(novoComprovante, atividade.getIdAtividade());

        	    atividade.setComprovanteUrl(url);
        	    atividade.setNomeArquivo(novoComprovante.getOriginalFilename());
        	    atividade.setTipoArquivo(contentType);
        	    atividade.setTamanhoArquivo(novoComprovante.getSize());
        	}
        }
        

        // Volta ao estado pendente, limpa reprovação anterior
        atividade.setStatus(Atividade.StatusAtividade.PENDENTE);
        atividade.setMotivoReprovacao(null);
        atividade.setHorasAprovadas(null);
        atividade.setDataValidacao(null);
        atividade.setDataSubmissao(LocalDateTime.now());
        atividade.setTentativas(atividade.getTentativas() + 1);

        atividade = atividadeRepository.save(atividade);

        // Notifica coordenadores
        notificarCoordenadoresDoCurso(atividade.getCurso(),
            "Atividade reenviada para avaliação",
            "O aluno " + atividade.getAluno().getNome() +
                " corrigiu e reenviou a atividade \"" + atividade.getTitulo() + "\".");

        logService.registrar(atividade.getAluno(),
            "Reenviou atividade id=" + atividadeId + ": " + atividade.getTitulo(), "Atividade");

        return toResponseDTO(atividade);
    }

    // COORDENADOR: Avaliar atividade (aprovar ou reprovar)

    /**
     * Coordenador avalia uma atividade pendente.
     *
     * Ao APROVAR:
     *  - horasAprovadas (informadas ou iguais às solicitadas) são somadas ao total do aluno.
     *  - Coordenador pode corrigir categoria e tipo de atividade.
     *  - Horas não ultrapassam o limite de 100h do curso.
     *
     * Ao REPROVAR:
     *  - motivoReprovacao é obrigatório.
     *  - Aluno pode reenviar posteriormente.
     */
    @Transactional
    public AtividadeResponseDTO avaliarAtividade(Long atividadeId, AvaliacaoRequestDTO dto) {
        Atividade atividade = atividadeRepository.findById(atividadeId)
            .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));

        if (atividade.getStatus() != Atividade.StatusAtividade.PENDENTE) {
            throw new RuntimeException(
                "Apenas atividades PENDENTES podem ser avaliadas. Status atual: " + atividade.getStatus());
        }

        if (dto.getStatus() == null) {
            throw new RuntimeException("O status da avaliação é obrigatório (APROVADO ou REPROVADO)");
        }

        // Permite que o coordenador corrija a categoria
        if (dto.getCategoriaFixa() != null) {
            atividade.setCategoriaFixa(dto.getCategoriaFixa());
        }

        // Permite que o coordenador corrija o tipo de atividade
        if (dto.getIdTipoAtividade() != null) {
            TipoAtividade tipo = tipoAtividadeRepository.findById(dto.getIdTipoAtividade())
                .orElseThrow(() -> new RuntimeException("Tipo de atividade não encontrado"));
            atividade.setTipoAtividade(tipo);
        }

        if (dto.getStatus() == Atividade.StatusAtividade.APROVADO) {
            processarAprovacao(atividade, dto);
        } else if (dto.getStatus() == Atividade.StatusAtividade.REPROVADO) {
            processarReprovacao(atividade, dto);
        } else {
            throw new RuntimeException("Status inválido para avaliação: " + dto.getStatus());
        }

        atividade.setDataValidacao(LocalDateTime.now());
        atividade = atividadeRepository.save(atividade);

        logService.registrarAcaoAtual(
            "Avaliou atividade id=" + atividadeId + " como " + dto.getStatus(), "Atividade");

        return toResponseDTO(atividade);
    }

    // Consultas

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
    public List<AtividadeResponseDTO> listarPendentesPorCurso(Long cursoId) {
        return atividadeRepository.findPendentesByCurso(cursoId).stream()
            .map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AtividadeResponseDTO> listarPorCurso(Long cursoId) {
        return atividadeRepository.findByCursoIdC(cursoId).stream()
            .map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AtividadeResponseDTO buscarPorId(Long id) {
        return toResponseDTO(atividadeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Atividade não encontrada")));
    }

    /**
     * Retorna o resumo de horas complementares de um aluno em um curso.
     * Considera APENAS atividades com status APROVADO no cálculo.
     */
    @Transactional(readOnly = true)
    public HorasAlunoResponseDTO consultarHorasAluno(Long alunoId, Long cursoId) {
        Users aluno = userRepository.findById(alunoId)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        Curso curso = cursoRepository.findById(cursoId)
            .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        int limiteHoras = limiteCurso(curso);

        int horasAprovadas = atividadeRepository
            .somarHorasAprovadasPorAlunoECurso(alunoId, cursoId);

        long pendentes = atividadeRepository
            .countByAlunoIdAndStatus(alunoId, Atividade.StatusAtividade.PENDENTE);
        long aprovadas = atividadeRepository
            .countByAlunoIdAndStatus(alunoId, Atividade.StatusAtividade.APROVADO);
        long reprovadas = atividadeRepository
            .countByAlunoIdAndStatus(alunoId, Atividade.StatusAtividade.REPROVADO);

        return new HorasAlunoResponseDTO(
            alunoId,
            aluno.getNome(),
            horasAprovadas,
            limiteHoras,
            Math.max(0, limiteHoras - horasAprovadas),
            (int) pendentes,
            (int) aprovadas,
            (int) reprovadas
        );
    }

    // Helpers privados

    private void processarAprovacao(Atividade atividade, AvaliacaoRequestDTO dto) {
        // Coordenador define as horas aprovadas; se não informou, usa as solicitadas
        int horasParaAprovar = (dto.getHorasAprovadas() != null && dto.getHorasAprovadas() > 0)
            ? dto.getHorasAprovadas()
            : atividade.getHorasSolicitadas();

        // Busca o teto de horas do curso (definido no cadastro do curso)
        int limiteHoras = limiteCurso(atividade.getCurso());

        // Valida teto do curso
        int horasJaAprovadas = atividadeRepository
            .somarHorasAprovadasPorAlunoECurso(
                atividade.getAluno().getId(),
                atividade.getCurso().getIdC());

        int horasDisponiveis = limiteHoras - horasJaAprovadas;
        if (horasDisponiveis <= 0) {
            throw new RuntimeException(
                "Aluno já atingiu o teto de " + limiteHoras + "h complementares neste curso");
        }

        // Limita ao disponível (não ultrapassa o teto do curso)
        int horasEfetivas = Math.min(horasParaAprovar, horasDisponiveis);

        atividade.setHorasAprovadas(horasEfetivas);
        atividade.setStatus(Atividade.StatusAtividade.APROVADO);
        atividade.setMotivoReprovacao(null);

        // Notifica o aluno
        String msgExtra = (horasEfetivas < horasParaAprovar)
            ? " (limitado ao teto do curso)" : "";
        criarNotificacao(atividade.getAluno(),
            "Atividade aprovada ✓",
            "Sua atividade \"" + atividade.getTitulo() + "\" foi APROVADA! " +
                "Horas creditadas: " + horasEfetivas + "h" + msgExtra + ".");
    }

    private void processarReprovacao(Atividade atividade, AvaliacaoRequestDTO dto) {
        // Feedback obrigatório em caso de reprovação
        if (dto.getMotivoReprovacao() == null || dto.getMotivoReprovacao().isBlank()) {
            throw new RuntimeException(
                "O motivo da reprovação é obrigatório ao reprovar uma atividade");
        }

        atividade.setStatus(Atividade.StatusAtividade.REPROVADO);
        atividade.setMotivoReprovacao(dto.getMotivoReprovacao());
        atividade.setHorasAprovadas(null);

        // Notifica o aluno com o motivo
        criarNotificacao(atividade.getAluno(),
            "Atividade reprovada ✗",
            "Sua atividade \"" + atividade.getTitulo() + "\" foi REPROVADA. " +
                "Motivo: " + dto.getMotivoReprovacao() +
                " Você pode corrigir e reenviar.");
    }

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
        dto.setIdTipoAtividade(a.getTipoAtividade().getIdTA());
        dto.setNomeTipoAtividade(a.getTipoAtividade().getNome());
        dto.setCategoriaFixa(a.getCategoriaFixa());
        dto.setTitulo(a.getTitulo());
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
    private String resolverTipoArquivo(String url) {
        if (url == null) return "desconhecido";
        String lower = url.toLowerCase();
        if (lower.endsWith(".pdf")) return "PDF";
        if (lower.endsWith(".png")) return "PNG";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "JPEG";
        return "arquivo";
    }
    private void gerarCertificadoConclusao(Users aluno, Curso curso) {

        boolean jaExiste = certificadoRepository
            .existsByAlunoIdAndCursoIdC(aluno.getId(), curso.getIdC());

        if (!jaExiste) {
            Certificados cert = new Certificados();
            cert.setAluno(aluno);
            cert.setCurso(curso);
            cert.setDescricao("Certificado de conclusão de horas complementares");

            certificadoRepository.save(cert);
        }
    }
}
