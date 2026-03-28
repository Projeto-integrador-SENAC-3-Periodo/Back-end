package br.edu.pe.senac.projeto_pi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.pe.senac.projeto_pi.dto.CategoriaAtividadeRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.CategoriaAtividadeResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.CategoriaAtividade;
import br.edu.pe.senac.projeto_pi.repositories.CategoriaAtividadeRepository;

@Service
public class CategoriaAtividadeService {

    @Autowired
    private CategoriaAtividadeRepository repository;

    @Transactional
    public CategoriaAtividadeResponseDTO create(CategoriaAtividadeRequestDTO requestDTO) {
        CategoriaAtividade categoria = new CategoriaAtividade();
        categoria.setNome(requestDTO.getNome());
        categoria.setDescricao(requestDTO.getDescricao());
        categoria = repository.save(categoria);
        return toResponseDTO(categoria);
    }

    @Transactional(readOnly = true)
    public List<CategoriaAtividadeResponseDTO> listAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaAtividadeResponseDTO getById(Long id) {
        CategoriaAtividade categoria = repository.findById(id).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        return toResponseDTO(categoria);
    }

    @Transactional
    public CategoriaAtividadeResponseDTO update(Long id, CategoriaAtividadeRequestDTO requestDTO) {
        CategoriaAtividade categoria = repository.findById(id).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        categoria.setNome(requestDTO.getNome());
        categoria.setDescricao(requestDTO.getDescricao());
        categoria = repository.save(categoria);
        return toResponseDTO(categoria);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private CategoriaAtividadeResponseDTO toResponseDTO(CategoriaAtividade categoria) {
        CategoriaAtividadeResponseDTO dto = new CategoriaAtividadeResponseDTO();
        dto.setId(categoria.getIdCatA());
        dto.setNome(categoria.getNome());
        dto.setDescricao(categoria.getDescricao());
        return dto;
    }
}
