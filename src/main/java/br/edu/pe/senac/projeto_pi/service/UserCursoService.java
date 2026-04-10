package br.edu.pe.senac.projeto_pi.service;

import br.edu.pe.senac.projeto_pi.dto.UsuarioCursoRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.UsuarioCursoResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.Curso;
import br.edu.pe.senac.projeto_pi.entity.UserCurso;
import br.edu.pe.senac.projeto_pi.entity.Users;
import br.edu.pe.senac.projeto_pi.repositories.CursoRepository;
import br.edu.pe.senac.projeto_pi.repositories.UserCursoRepository;
import br.edu.pe.senac.projeto_pi.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        logService.registrarAcaoAtual("Vinculou usuário " + user.getNome() + " ao curso " + curso.getNome(), "UserCurso");
        return toResponseDTO(userCurso);
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
