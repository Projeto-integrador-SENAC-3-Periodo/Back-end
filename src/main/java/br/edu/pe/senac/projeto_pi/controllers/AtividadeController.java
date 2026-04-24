package br.edu.pe.senac.projeto_pi.controllers;

import br.edu.pe.senac.projeto_pi.dto.AtividadeRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.AtividadeResponseDTO;
import br.edu.pe.senac.projeto_pi.dto.AvaliacaoRequestDTO;
import br.edu.pe.senac.projeto_pi.dto.HorasAlunoResponseDTO;
import br.edu.pe.senac.projeto_pi.service.AtividadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/atividades")
public class AtividadeController {

    @Autowired
    private AtividadeService atividadeService;

    // ─── ALUNO ───────────────────────────────────────────────────

    /**
     * Aluno submete atividade.
     * - categoriaFixa, tipoAtividadeTexto (campo livre), descricao, horasSolicitadas, idCurso e comprovante
     * - idTipoAtividade é OPCIONAL — se não enviado, fica null (aluno usa texto livre)
     */
    @PostMapping(value = "/submeter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<AtividadeResponseDTO> submeterAtividade(
            @RequestParam("idAluno") Long idAluno,
            @RequestParam("categoriaFixa") String categoriaFixa,
            @RequestParam(value = "tipoAtividade", required = false) String tipoAtividade,
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam("horasSolicitadas") Integer horasSolicitadas,
            @RequestParam("idCurso") Long idCurso,
            @RequestParam("comprovante") MultipartFile comprovante) {

        AtividadeRequestDTO dto = new AtividadeRequestDTO();
        dto.setCategoriaFixa(
            br.edu.pe.senac.projeto_pi.entity.Atividade.CategoriaFixa.valueOf(categoriaFixa.toUpperCase()));
        dto.setTipoAtividade(tipoAtividade); 
        dto.setDescricao(descricao);
        dto.setHorasSolicitadas(horasSolicitadas);
        dto.setIdCurso(idCurso);

        return ResponseEntity.ok(atividadeService.submeterAtividade(idAluno, dto, comprovante));
    }

    @PutMapping(value = "/{id}/reenviar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<AtividadeResponseDTO> reenviarAtividade(
            @PathVariable Long id,
            @RequestParam("idAluno") Long idAluno,
            @RequestParam(value = "categoriaFixa", required = false) String categoriaFixa,
            @RequestParam(value = "tipoAtividade", required = false) String tipoAtividade,
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "horasSolicitadas", required = false) Integer horasSolicitadas,
            @RequestParam(value = "comprovante", required = false) MultipartFile comprovante) {

        AtividadeRequestDTO dto = new AtividadeRequestDTO();
        if (categoriaFixa != null)
            dto.setCategoriaFixa(
                br.edu.pe.senac.projeto_pi.entity.Atividade.CategoriaFixa.valueOf(categoriaFixa.toUpperCase()));
        dto.setTipoAtividade(tipoAtividade);
        dto.setDescricao(descricao);
        dto.setHorasSolicitadas(horasSolicitadas);

        return ResponseEntity.ok(atividadeService.reenviarAtividade(id, idAluno, dto, comprovante));
    }

    @GetMapping("/aluno/{alunoId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<AtividadeResponseDTO>> listarPorAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(atividadeService.listarPorAluno(alunoId));
    }

    @GetMapping("/horas/aluno/{alunoId}/curso/{cursoId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<HorasAlunoResponseDTO> consultarHoras(
            @PathVariable Long alunoId, @PathVariable Long cursoId) {
        return ResponseEntity.ok(atividadeService.consultarHorasAluno(alunoId, cursoId));
    }

    // ─── COORDENADOR / ADMINISTRADOR ────────────────────────────

    @GetMapping("/pendentes/curso/{cursoId}")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<AtividadeResponseDTO>> listarPendentesPorCurso(
            @PathVariable Long cursoId, Authentication auth) {
        return ResponseEntity.ok(
            atividadeService.listarPendentesPorCurso(cursoId, auth.getName()));
    }

    @PutMapping("/{id}/avaliar")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<AtividadeResponseDTO> avaliarAtividade(
            @PathVariable Long id,
            @RequestBody AvaliacaoRequestDTO dto,
            Authentication auth) {
        return ResponseEntity.ok(
            atividadeService.avaliarAtividade(id, dto, auth.getName()));
    }

    @GetMapping("/curso/{cursoId}")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<AtividadeResponseDTO>> listarPorCurso(
            @PathVariable Long cursoId, Authentication auth) {
        return ResponseEntity.ok(
            atividadeService.listarPorCurso(cursoId, auth.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ALUNO', 'COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<AtividadeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(atividadeService.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<AtividadeResponseDTO>> listarTodas() {
        return ResponseEntity.ok(atividadeService.listarTodasAtividades());
    }
}
