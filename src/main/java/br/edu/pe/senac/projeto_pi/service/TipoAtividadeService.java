package br.edu.pe.senac.projeto_pi.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.pe.senac.projeto_pi.dto.TipoAtividadeRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.TipoAtividadeResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.CategoriaAtividade;
import br.edu.pe.senac.projeto_pi.entity.TipoAtividade;
import br.edu.pe.senac.projeto_pi.repositories.CategoriaAtividadeRepository;
import br.edu.pe.senac.projeto_pi.repositories.TipoAtividadeRepository;

@Service
public class TipoAtividadeService {

    @Autowired
    private TipoAtividadeRepository repository;

    @Autowired
    private CategoriaAtividadeRepository categoriaRepository;

    @Transactional
    public TipoAtividadeResponseDTO create(TipoAtividadeRequestDTO requestDTO) {
        CategoriaAtividade categoria = categoriaRepository.findById(requestDTO.getIdCategoria()).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        TipoAtividade tipo = new TipoAtividade();
        tipo.setNome(requestDTO.getNome());
        tipo.setDescricao(requestDTO.getDescricao());
        tipo.setHorasMaximas(requestDTO.getHorasMaximas());
        tipo.setRequisito(requestDTO.getRequisito());
        tipo.setCategoria(categoria);
        tipo = repository.save(tipo);
        return toResponseDTO(tipo);
    }

    @Transactional(readOnly = true)
    public List<TipoAtividadeResponseDTO> listAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TipoAtividadeResponseDTO getById(Long id) {
        TipoAtividade tipo = repository.findById(id).orElseThrow(() -> new RuntimeException("Tipo de Atividade não encontrado"));
        return toResponseDTO(tipo);
    }

    @Transactional
    public TipoAtividadeResponseDTO update(Long id, TipoAtividadeRequestDTO requestDTO) {
        TipoAtividade tipo = repository.findById(id).orElseThrow(() -> new RuntimeException("Tipo de Atividade não encontrado"));
        CategoriaAtividade categoria = categoriaRepository.findById(requestDTO.getIdCategoria()).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        tipo.setNome(requestDTO.getNome());
        tipo.setDescricao(requestDTO.getDescricao());
        tipo.setHorasMaximas(requestDTO.getHorasMaximas());
        tipo.setRequisito(requestDTO.getRequisito());
        tipo.setCategoria(categoria);
        tipo = repository.save(tipo);
        return toResponseDTO(tipo);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private TipoAtividadeResponseDTO toResponseDTO(TipoAtividade tipo) {
        TipoAtividadeResponseDTO dto = new TipoAtividadeResponseDTO();
        dto.setId(tipo.getIdTA());
        dto.setNome(tipo.getNome());
        dto.setDescricao(tipo.getDescricao());
        dto.setHorasMaximas(tipo.getHorasMaximas());
        dto.setRequisito(tipo.getRequisito());
        dto.setIdCategoria(tipo.getCategoria().getIdCatA());
        dto.setNomeCategoria(tipo.getCategoria().getNome());
        return dto;
    }
}
