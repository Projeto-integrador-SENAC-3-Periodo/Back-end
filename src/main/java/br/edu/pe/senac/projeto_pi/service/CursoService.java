package br.edu.pe.senac.projeto_pi.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.pe.senac.projeto_pi.dto.CursoRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.CursoResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.Curso;
import br.edu.pe.senac.projeto_pi.repositories.CursoRepository;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private LogSistemaService logService;

    @Transactional
    public CursoResponseDTO create(CursoRequestDTO cursoRequestDTO) {
        Curso curso = new Curso();
        curso.setNome(cursoRequestDTO.getNome());
        curso.setDescricao(cursoRequestDTO.getDescricao());
        curso.setCargaHorariaMinima(cursoRequestDTO.getCargaHorariaMinima());
        curso = cursoRepository.save(curso);
        logService.registrarAcaoAtual("Criou curso: " + curso.getNome(), "Curso");
        return toResponseDTO(curso);
    }

    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listAll() {
        return cursoRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CursoResponseDTO getById(Long id) {
        Curso curso = cursoRepository.findById(id).orElseThrow(() -> new RuntimeException("Curso não encontrado"));
        return toResponseDTO(curso);
    }

    @Transactional
    public CursoResponseDTO update(Long id, CursoRequestDTO cursoRequestDTO) {
        Curso curso = cursoRepository.findById(id).orElseThrow(() -> new RuntimeException("Curso não encontrado"));
        curso.setNome(cursoRequestDTO.getNome());
        curso.setDescricao(cursoRequestDTO.getDescricao());
        curso.setCargaHorariaMinima(cursoRequestDTO.getCargaHorariaMinima());
        curso = cursoRepository.save(curso);
        logService.registrarAcaoAtual("Atualizou curso: " + curso.getNome(), "Curso");
        return toResponseDTO(curso);
    }

    @Transactional
    public void delete(Long id) {
        Curso curso = cursoRepository.findById(id).orElseThrow(() -> new RuntimeException("Curso não encontrado"));
        logService.registrarAcaoAtual("Deletou curso: " + curso.getNome(), "Curso");
        cursoRepository.deleteById(id);
    }

    private CursoResponseDTO toResponseDTO(Curso curso) {
        CursoResponseDTO dto = new CursoResponseDTO();
        dto.setId(curso.getIdC());
        dto.setNome(curso.getNome());
        dto.setDescricao(curso.getDescricao());
        dto.setCargaHorariaMinima(curso.getCargaHorariaMinima());
        return dto;
    }
}
