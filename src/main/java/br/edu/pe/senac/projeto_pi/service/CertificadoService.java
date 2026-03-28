package br.edu.pe.senac.projeto_pi.service;

import br.edu.pe.senac.projeto_pi.dto.CertificadoResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.Atividade;
import br.edu.pe.senac.projeto_pi.entity.Certificados;
import br.edu.pe.senac.projeto_pi.repositories.AtividadeRepository;
import br.edu.pe.senac.projeto_pi.repositories.CertificadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CertificadoService {

    @Autowired
    private CertificadoRepository certificadoRepository;

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Transactional
    public Certificados createCertificado(Long atividadeId, String arquivoUrl, String tipoArquivo) {
        Atividade atividade = atividadeRepository.findById(atividadeId)
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));

        Certificados certificado = new Certificados();
        certificado.setAtividade(atividade);
        certificado.setAluno(atividade.getAluno());
        certificado.setArquivoUrl(arquivoUrl);
        certificado.setTipoArquivo(tipoArquivo);

        return certificadoRepository.save(certificado);
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
        dto.setIdAtividade(c.getAtividade().getIdAtividade());
        dto.setTituloAtividade(c.getAtividade().getTitulo());
        dto.setIdAluno(c.getAluno().getId());
        dto.setNomeAluno(c.getAluno().getNome());
        dto.setArquivoUrl(c.getArquivoUrl());
        dto.setTipoArquivo(c.getTipoArquivo());
        dto.setAssinaturaDigital(c.getAssinaturaDigital());
        dto.setDataEmissao(c.getDataEmissao());
        return dto;
    }
}
