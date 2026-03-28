package br.edu.pe.senac.projeto_pi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoAtividadeResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private Integer horasMaximas;
    private String requisito;
    private Long idCategoria;
    private String nomeCategoria;
}
