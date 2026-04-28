package br.edu.pe.senac.projeto_pi.service;

import br.edu.pe.senac.projeto_pi.dto.TipoAtividadeRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.TipoAtividadeResponseDTO;
import br.edu.pe.senac.projeto_pi.entity.Atividade.CategoriaFixa;
import br.edu.pe.senac.projeto_pi.entity.TipoAtividade;
import br.edu.pe.senac.projeto_pi.repositories.TipoAtividadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoAtividadeService {

    @Autowired
    private TipoAtividadeRepository repository;

    // ─── CRIAR ───────────────────────────────────────────────────

    @Transactional
    public TipoAtividadeResponseDTO create(TipoAtividadeRequestDTO dto) {
        TipoAtividade tipo = new TipoAtividade();
        tipo.setNome(dto.getNome());
        tipo.setCategoriaF(dto.getCategoriaFixa());
        tipo.setHorasMaximas(dto.getHorasMaximas());
        tipo.setRequisito(dto.getRequisito());
        return TipoAtividadeResponseDTO.from(repository.save(tipo));
    }

    // ─── LISTAR ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<TipoAtividadeResponseDTO> listAll() {
        return repository.findByAtivoTrue().stream()
                .map(TipoAtividadeResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TipoAtividadeResponseDTO> listAllIncludingInactive() {
        return repository.findAll().stream()
                .map(TipoAtividadeResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TipoAtividadeResponseDTO> listByCategoria(CategoriaFixa categoria) {
        return repository.findByCategoriaFAndAtivoTrue(categoria).stream()
                .map(TipoAtividadeResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TipoAtividadeResponseDTO getById(Long id) {
        return TipoAtividadeResponseDTO.from(
            repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Tipo de Atividade não encontrado")));
    }

    // ─── EDITAR ──────────────────────────────────────────────────

    @Transactional
    public TipoAtividadeResponseDTO update(Long id, TipoAtividadeRequestDTO dto) {
        TipoAtividade tipo = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Tipo de Atividade não encontrado"));
        tipo.setNome(dto.getNome());
        tipo.setCategoriaF(dto.getCategoriaFixa());
        tipo.setHorasMaximas(dto.getHorasMaximas());
        tipo.setRequisito(dto.getRequisito());
        return TipoAtividadeResponseDTO.from(repository.save(tipo));
    }

    // ─── REMOVER (soft delete se em uso, físico se livre) ────────

    @Transactional
    public void delete(Long id) {
        TipoAtividade tipo = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Tipo de Atividade não encontrado"));

        if (repository.existeAtividadeUsandoTipo(id)) {
            // Tipo em uso: apenas desativa (soft delete)
            tipo.setAtivo(false);
            repository.save(tipo);
        } else {
            // Sem vínculo: pode deletar fisicamente
            repository.delete(tipo);
        }
    }

    // ─── REATIVAR ────────────────────────────────────────────────

    @Transactional
    public TipoAtividadeResponseDTO reativar(Long id) {
        TipoAtividade tipo = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Tipo de Atividade não encontrado"));
        tipo.setAtivo(true);
        return TipoAtividadeResponseDTO.from(repository.save(tipo));
    }
}
