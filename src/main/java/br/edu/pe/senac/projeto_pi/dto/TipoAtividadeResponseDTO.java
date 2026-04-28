package br.edu.pe.senac.projeto_pi.dto;

import br.edu.pe.senac.projeto_pi.entity.Atividade.CategoriaFixa;
import br.edu.pe.senac.projeto_pi.entity.TipoAtividade;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TipoAtividadeResponseDTO {
    private Long id;
    private String nome;
    private CategoriaFixa categoriaFixa;
    private Integer horasMaximas;
    private String requisito;
    private boolean ativo;
    private LocalDateTime criadoEm;

    public static TipoAtividadeResponseDTO from(TipoAtividade t) {
        TipoAtividadeResponseDTO dto = new TipoAtividadeResponseDTO();
        dto.setId(t.getIdTA());
        dto.setNome(t.getNome());
        dto.setCategoriaFixa(t.getCategoriaF());
        dto.setHorasMaximas(t.getHorasMaximas());
        dto.setRequisito(t.getRequisito());
        dto.setAtivo(t.isAtivo());
        dto.setCriadoEm(t.getCriadoEm());
        return dto;
    }
}
