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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller de atividades complementares.
 *
 * Fluxo principal:
 *  1. Aluno submete atividade + comprovante  → POST /atividades/submeter
 *  2. Coordenador lista pendentes            → GET  /atividades/pendentes/curso/{cursoId}
 *  3. Coordenador avalia                     → PUT  /atividades/{id}/avaliar
 *  4. Aluno reenvia após reprovação          → PUT  /atividades/{id}/reenviar
 *  5. Aluno consulta suas horas             → GET  /atividades/horas/aluno/{alunoId}/curso/{cursoId}
 */
@RestController
@RequestMapping("/atividades")
public class AtividadeController {

    @Autowired
    private AtividadeService atividadeService;

    // ─── ALUNO ──────────────────────────────────────────────────────────────

    /**
     * Aluno submete uma nova atividade com comprovante/certificado.
     * Aceita multipart/form-data com os campos da atividade + arquivo.
     */
    @PostMapping(value = "/submeter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<AtividadeResponseDTO> submeterAtividade(
            @RequestParam("idAluno") Long idAluno,
            @RequestParam("categoriaFixa") String categoriaFixa,
            @RequestParam("idTipoAtividade") Long idTipoAtividade,
            @RequestParam("titulo") String titulo,
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam("horasSolicitadas") Integer horasSolicitadas,
            @RequestParam("idCurso") Long idCurso,
            @RequestParam("comprovante") MultipartFile comprovante) {

        AtividadeRequestDTO dto = new AtividadeRequestDTO();
        dto.setCategoriaFixa(
            br.edu.pe.senac.projeto_pi.entity.Atividade.CategoriaFixa.valueOf(categoriaFixa.toUpperCase()));
        dto.setIdTipoAtividade(idTipoAtividade);
        dto.setTitulo(titulo);
        dto.setDescricao(descricao);
        dto.setHorasSolicitadas(horasSolicitadas);
        dto.setIdCurso(idCurso);

        return ResponseEntity.ok(atividadeService.submeterAtividade(idAluno, dto, comprovante));
    }

    /**
     * Aluno reenvia uma atividade reprovada com as correções e novo comprovante (opcional).
     */
    @PutMapping(value = "/{id}/reenviar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<AtividadeResponseDTO> reenviarAtividade(
            @PathVariable Long id,
            @RequestParam("idAluno") Long idAluno,
            @RequestParam(value = "categoriaFixa", required = false) String categoriaFixa,
            @RequestParam(value = "idTipoAtividade", required = false) Long idTipoAtividade,
            @RequestParam(value = "titulo", required = false) String titulo,
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "horasSolicitadas", required = false) Integer horasSolicitadas,
            @RequestParam(value = "comprovante", required = false) MultipartFile comprovante) {

        AtividadeRequestDTO dto = new AtividadeRequestDTO();
        if (categoriaFixa != null) {
            dto.setCategoriaFixa(
                br.edu.pe.senac.projeto_pi.entity.Atividade.CategoriaFixa.valueOf(categoriaFixa.toUpperCase()));
        }
        dto.setIdTipoAtividade(idTipoAtividade);
        dto.setTitulo(titulo);
        dto.setDescricao(descricao);
        dto.setHorasSolicitadas(horasSolicitadas);

        return ResponseEntity.ok(atividadeService.reenviarAtividade(id, idAluno, dto, comprovante));
    }

    /**
     * Lista todas as atividades de um aluno (todos os status).
     */
    @GetMapping("/aluno/{alunoId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<AtividadeResponseDTO>> listarPorAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(atividadeService.listarPorAluno(alunoId));
    }

    /**
     * Resumo de horas complementares do aluno em um curso.
     * Retorna horas aprovadas, limite, restantes e contagem por status.
     */
    @GetMapping("/horas/aluno/{alunoId}/curso/{cursoId}")
    @PreAuthorize("hasAnyRole('ALUNO', 'COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<HorasAlunoResponseDTO> consultarHoras(
            @PathVariable Long alunoId,
            @PathVariable Long cursoId) {
        return ResponseEntity.ok(atividadeService.consultarHorasAluno(alunoId, cursoId));
    }

    // ─── COORDENADOR / ADMINISTRADOR ────────────────────────────────────────

    /**
     * Lista atividades PENDENTES de um curso, aguardando avaliação do coordenador.
     */
    @GetMapping("/pendentes/curso/{cursoId}")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<AtividadeResponseDTO>> listarPendentesPorCurso(
            @PathVariable Long cursoId) {
        return ResponseEntity.ok(atividadeService.listarPendentesPorCurso(cursoId));
    }

    /**
     * Coordenador avalia uma atividade (aprova ou reprova).
     *
     * Body JSON:
     * {
     *   "status": "APROVADO" | "REPROVADO",
     *   "horasAprovadas": 20,           // opcional; usa horasSolicitadas se omitido
     *   "categoriaFixa": "PESQUISA",    // opcional; corrige a categoria do aluno
     *   "idTipoAtividade": 3,           // opcional; corrige o tipo do aluno
     *   "motivoReprovacao": "..."        // obrigatório se status = REPROVADO
     * }
     */
    @PutMapping("/{id}/avaliar")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<AtividadeResponseDTO> avaliarAtividade(
            @PathVariable Long id,
            @RequestBody AvaliacaoRequestDTO dto) {
        return ResponseEntity.ok(atividadeService.avaliarAtividade(id, dto));
    }

    /**
     * Lista todas as atividades de um curso (qualquer status).
     */
    @GetMapping("/curso/{cursoId}")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<AtividadeResponseDTO>> listarPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(atividadeService.listarPorCurso(cursoId));
    }

    /**
     * Busca uma atividade pelo ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ALUNO', 'COORDENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<AtividadeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(atividadeService.buscarPorId(id));
    }

    /**
     * Lista todas as atividades (admin).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<AtividadeResponseDTO>> listarTodas() {
        return ResponseEntity.ok(atividadeService.listarTodasAtividades());
    }
}
