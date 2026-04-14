package br.edu.pe.senac.projeto_pi.service;

import br.edu.pe.senac.projeto_pi.dto.CertificadoResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.Atividade;
import br.edu.pe.senac.projeto_pi.entity.Certificados;
import br.edu.pe.senac.projeto_pi.repositories.AtividadeRepository;
import br.edu.pe.senac.projeto_pi.repositories.CertificadoRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CertificadoService {

    @Autowired
    private CertificadoRepository certificadoRepository;

    @Transactional(readOnly = true)
    public List<CertificadoResponseDTO> listByAluno(Long alunoId) {
        return certificadoRepository.findByAlunoId(alunoId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CertificadoResponseDTO getById(Long id) {
        Certificados cert = certificadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificado não encontrado"));
        return toResponseDTO(cert);
    }

    private CertificadoResponseDTO toResponseDTO(Certificados c) {
        CertificadoResponseDTO dto = new CertificadoResponseDTO();
        dto.setId(c.getIdCertificado());
        dto.setIdAluno(c.getAluno().getId());
        dto.setNomeAluno(c.getAluno().getNome());
        dto.setIdCurso(c.getCurso().getIdC());
        dto.setNomeCurso(c.getCurso().getNome());
        dto.setDescricao(c.getDescricao());
        dto.setDataEmissao(c.getDataEmissao());
        return dto;
    }
}