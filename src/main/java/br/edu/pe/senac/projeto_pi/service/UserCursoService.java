package br.edu.pe.senac.projeto_pi.service;

import br.edu.pe.senac.projeto_pi.dto.UsuarioCursoRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.UsuarioCursoResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.Curso;
import br.edu.pe.senac.projeto_pi.entity.Notificacao;
import br.edu.pe.senac.projeto_pi.entity.UserCurso;
import br.edu.pe.senac.projeto_pi.entity.Users;
import br.edu.pe.senac.projeto_pi.repositories.CursoRepository;
import br.edu.pe.senac.projeto_pi.repositories.NotificacaoRepository;
import br.edu.pe.senac.projeto_pi.repositories.UserCursoRepository;
import br.edu.pe.senac.projeto_pi.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCursoService {

    @Autowired
    private UserCursoRepository userCursoRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private LogSistemaService logService;

    @Transactional
    public UsuarioCursoResponseDTO vincularAluno(Long cursoId, UsuarioCursoRequestDTO dto) {
        Users user = usersRepository.findById(dto.getIdUser())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        if (userCursoRepository.existsByUserIdAndCursoIdC(dto.getIdUser(), cursoId)) {
            throw new RuntimeException("Usuário já está vinculado a este curso");
        }

        UserCurso userCurso = new UserCurso();
        userCurso.setUser(user);
        userCurso.setCurso(curso);
        userCurso.setPapel(dto.getPapel() != null ? dto.getPapel() : UserCurso.Papel.ALUNO);

        userCurso = userCursoRepository.save(userCurso);
        logService.registrarAcaoAtual(
            "Vinculou usuário " + user.getNome() + " ao curso " + curso.getNome(), "UserCurso");

        // Create notification for the linked user
        criarNotificacaoVinculo(user, curso, userCurso.getPapel());

        return toResponseDTO(userCurso);
    }

    @Transactional
    public void desvincularAluno(Long cursoId, Long alunoId) {
        UserCurso uc = userCursoRepository.findByUserIdAndCursoIdC(alunoId, cursoId)
                .orElseThrow(() -> new RuntimeException("Vínculo não encontrado"));
        Users user = uc.getUser();
        Curso curso = uc.getCurso();
        userCursoRepository.delete(uc);
        logService.registrarAcaoAtual(
            "Desvinculou usuário " + user.getNome() + " do curso " + curso.getNome(), "UserCurso");

        // Create notification for the unlinked user
        criarNotificacaoDesvinculo(user, curso);
    }

    private void criarNotificacaoVinculo(Users user, Curso curso, UserCurso.Papel papel) {
        String titulo;
        String mensagem;
        if (papel == UserCurso.Papel.COORDENADOR) {
            titulo = "Vinculado como coordenador";
            mensagem = "Você foi vinculado como coordenador ao curso \"" + curso.getNome() + "\".";
        } else {
            titulo = "Matriculado em curso";
            mensagem = "Você foi matriculado no curso \"" + curso.getNome() + "\".";
        }

        Notificacao notificacao = new Notificacao();
        notificacao.setUser(user);
        notificacao.setTitulo(titulo);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(Notificacao.TipoNotificacao.PUSH);
        notificacao.setStatus(Notificacao.StatusNotificacao.ENVIADA);
        notificacao.setCreatedAt(LocalDateTime.now());
        notificacaoRepository.save(notificacao);
    }

    private void criarNotificacaoDesvinculo(Users user, Curso curso) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUser(user);
        notificacao.setTitulo("Desvinculado de curso");
        notificacao.setMensagem("Você foi desvinculado do curso \"" + curso.getNome() + "\".");
        notificacao.setTipo(Notificacao.TipoNotificacao.PUSH);
        notificacao.setStatus(Notificacao.StatusNotificacao.ENVIADA);
        notificacao.setCreatedAt(LocalDateTime.now());
        notificacaoRepository.save(notificacao);
    }

    @Transactional(readOnly = true)
    public List<UsuarioCursoResponseDTO> listarAlunosDoCurso(Long cursoId) {
        return userCursoRepository.findByCursoIdC(cursoId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioCursoResponseDTO> listarCursosDoAluno(Long alunoId) {
        return userCursoRepository.findByUserId(alunoId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private UsuarioCursoResponseDTO toResponseDTO(UserCurso uc) {
        UsuarioCursoResponseDTO dto = new UsuarioCursoResponseDTO();
        dto.setId(uc.getIdUC());
        dto.setIdUser(uc.getUser().getId());
        dto.setNomeUser(uc.getUser().getNome());
        dto.setEmailUser(uc.getUser().getEmail());
        dto.setIdCurso(uc.getCurso().getIdC());
        dto.setNomeCurso(uc.getCurso().getNome());
        dto.setPapel(uc.getPapel());
        dto.setDataMatricula(uc.getDataMatricula());
        return dto;
    }
}
